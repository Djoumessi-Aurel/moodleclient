/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Map;

import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import moodleclient.Moodleclient;
import static moodleclient.Moodleclient.root;
import moodleclient.exceptions.ServerUnreachableException;
import moodleclient.exceptions.WrongCredentialsException;
import moodleclient.helpers.AccountHelper;

import org.hibernate.Session;
import org.json.simple.JSONObject;

import org.json.simple.parser.ParseException;

/**
 * FXML Controller class
 *
 * @author ralie
 */
public class LoginController implements Initializable {

    @FXML
    private Label errMsg;
    @FXML
    private JFXTextField usernameInput;
    @FXML
    private JFXTextField passwordInput;
    @FXML
    private JFXRadioButton studentRadio;
    @FXML
    private ToggleGroup userRole;
    @FXML
    private JFXRadioButton teacherRadio;
    @FXML
    private JFXTextField serverAddInput;
    @FXML
    private JFXButton loginBtn;
    
    @FXML
    private JFXButton serverBtn;
    
    private Session session = Moodleclient.session;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        this.errMsg.setText("");
        this.errMsg.setVisible(false);
    }    

    @FXML
    private void handleLoginBtn(ActionEvent event) throws IOException, ProtocolException, ParseException, ServerUnreachableException {
        String username = usernameInput.getText();
        String password = passwordInput.getText();
        String serverAdd = "";
        
        boolean isStudent = studentRadio.isSelected();
        //Login function HERE !
        
        AccountHelper accountHelper;
        
        //load the server address from the configuration file
        JSONObject jo = Moodleclient.loadConfiguraton();
        
        try{
            serverAdd = jo.get("serverAddress").toString();
            
            System.out.println(serverAdd);
            
            Moodleclient.serverAddress = serverAdd;

            boolean serverReachable;

            try{
                serverReachable = Moodleclient.serverReachable();
                accountHelper = new AccountHelper();
                                
                if(Moodleclient.serverReachable()){

                    try{
                       System.out.println("reached");
                       
                       Map<String, String> userData = accountHelper.getUserData(username, password);
                       System.out.println("userData : "+userData.toString());
                       Byte b;

                       if(isStudent){
                           b = 1;
                       }else{
                           b = 0;
                       }
                       System.out.println("RemoteId : "+userData.get("remoteid"));

                       Moodleclient.user = accountHelper.saveAccount(username, password, userData.get("token"), b, Integer.valueOf((String)userData.get("remoteid")));
                       
                       //accountHelper.getSessionId();

                       Dry dry = new Dry();
                       dry.showDashboard(root);

                       System.out.println("account created");

                   }catch(WrongCredentialsException e){

                       //display the wrong credential message
                       //************************************
                       this.errMsg.setText("Wrong credentials");
                       this.errMsg.setVisible(true);
                       
                   }catch(MalformedURLException e){
                        e.printStackTrace();
                        
                        this.errMsg.setText("Please fill the username and the password.");
                        this.errMsg.setVisible(true);
                   }

               }else{

                  this.errMsg.setText("Server not reachable");
                  this.errMsg.setVisible(true);
                  System.out.println("not reached");   // ajout de ma part

               }

            }catch(MalformedURLException e){
                this.errMsg.setText("Mal formatted url");
                this.errMsg.setVisible(true);
            }
            
        }catch(NullPointerException e){
            e.printStackTrace();
            
            this.errMsg.setText("You must first set the server address.");
            this.errMsg.setVisible(true);
        }
    }
    
    @FXML
    private void handleServerBtn(ActionEvent event) throws Exception {
        
        String serverAddress = ServerSettingsAB.display();

        System.out.println("Adresse du serveur=" + serverAddress);
        
        if(serverAddress.isEmpty()){} //The user choose cancel so we do nothing
        
        else{
            //take the old value of the server Address
            JSONObject jo = Moodleclient.loadConfiguraton();
            
            //Update the value of the server ...
            jo.put("serverAddress", serverAddress);

            Moodleclient.saveConfiguration(jo);
        }
    }

}
