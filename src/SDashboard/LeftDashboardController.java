/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SDashboard;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import moodleclient.Moodleclient;
import static moodleclient.Moodleclient.root;
import moodleclient.exceptions.NotValidSessionException;
import moodleclient.exceptions.ServerUnreachableException;
import moodleclient.helpers.CurrentTab;
import moodleclient.helpers.PrivateFileHelper;
import org.json.simple.parser.ParseException;

/**
 * FXML Controller class
 *
 * @author ralie
 */
public class LeftDashboardController implements Initializable {

    @FXML
    private JFXButton btnDashboard;
    @FXML
    private JFXButton btnSiteHome;
    @FXML
    private JFXButton btnPrivateFiles;
    @FXML
    private JFXButton btnAssignments;
    @FXML
    private JFXButton btnCourses;
        

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // On désélectionne tout
       deselectButton(btnAssignments); deselectButton(btnDashboard); deselectButton(btnPrivateFiles);
        
        try{
            AnchorPane content = null;
            // On sélectionne l'onglet courant
                switch(Moodleclient.CURRENT_TAB){
                        case DASHBOARD:
                            selectButton(btnDashboard);
                            content =  (AnchorPane)FXMLLoader.load(getClass().getResource("/SDashboard/StudentDashboard.fxml"));
                            break;
                        case PRIVATE_FILES:
                            selectButton(btnPrivateFiles);
                            content = (AnchorPane)FXMLLoader.load(getClass().getResource("/SSavePrivateFiles/StudentSavePrivateFiles_1.fxml"));
                            break;
                        case ASSIGNMENTS:
                            selectButton(btnAssignments);
                            content = (AnchorPane)FXMLLoader.load(getClass().getResource("/SAssignmentList/StudentAssignmentList_1.fxml"));
                            break;
                    }

                if(content != null) root.setCenter(content);
                }catch(IOException e){
                    e.printStackTrace();
                }
        
    }    

    @FXML
    private void handleButtonDashboard(ActionEvent event) throws Exception{
        
        AnchorPane content = (AnchorPane)FXMLLoader.load(getClass().getResource("/SDashboard/StudentDashboard.fxml"));
        
        root.setCenter(content);
        
        Moodleclient.CURRENT_TAB = CurrentTab.DASHBOARD;
        selectButton(btnDashboard);
        deselectButton(btnPrivateFiles); deselectButton(btnAssignments);
    }

    @FXML
    private void handleButtonSHome(ActionEvent event) throws IOException {
        AnchorPane content = (AnchorPane)FXMLLoader.load(getClass().getResource("/SHome/StudentHome.fxml"));
        
        root.setCenter(content);
    }

    @FXML
    private void handleButtonSPrivaFiles(ActionEvent event) throws IOException, ParseException, NotValidSessionException, MalformedURLException, ServerUnreachableException {
        
        AnchorPane content = (AnchorPane)FXMLLoader.load(getClass().getResource("/SSavePrivateFiles/StudentSavePrivateFiles_1.fxml"));

        root.setCenter(content);

        Moodleclient.CURRENT_TAB = CurrentTab.PRIVATE_FILES;
        selectButton(btnPrivateFiles);
        deselectButton(btnDashboard); deselectButton(btnAssignments);
    }

    @FXML
    private void handleButtonSAssignment(ActionEvent event) throws IOException {

        AnchorPane content;
        if(Moodleclient.user.isStudent()) content = (AnchorPane)FXMLLoader.load(getClass().getResource("/SAssignmentList/StudentAssignmentList_1.fxml")); //Si étudiant
        else content = null; //Si enseignant

        root.setCenter(content);
        
        Moodleclient.CURRENT_TAB = CurrentTab.ASSIGNMENTS;
        selectButton(btnAssignments);
        deselectButton(btnDashboard); deselectButton(btnPrivateFiles);
    }
    
    private void selectButton(JFXButton bouton){
        bouton.setStyle("-fx-border-color: D1D0CE; -fx-background-color: #357EC7");
        bouton.setTextFill(Color.WHITE);
        bouton.setFont(Font.font("System", FontWeight.BOLD, 14));
    }
    
    private void deselectButton(JFXButton bouton){
        bouton.setStyle("-fx-border-color: D1D0CE; -fx-background-color: #F4F4F4");
        bouton.setTextFill(Color.BLACK);
        bouton.setFont(Font.font("System", FontWeight.NORMAL, 14));
    }
    
    
    //Functions to refresh pages
    
   /* public static void refreshDashboard(){
        handleButtonDashboard(new ActionEvent());
    }
    
    public static void refreshPrivateFiles(){
        btnPrivateFiles.fire();
    }
    
    public static void refreshAssignments(){
        btnAssignments.fire();
    }*/
    
}
