/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SDashboard;

import com.jfoenix.controls.JFXButton;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;
import moodleclient.LogOutAlertBox;
import moodleclient.Moodleclient;
import moodleclient.SyncAlertBox;
import moodleclient.exceptions.ServerUnreachableException;
import moodleclient.helpers.CoursesHelper;
import moodleclient.util.HibernateUtil;
import org.hibernate.Session;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import moodleclient.SyncAlertBox;
import moodleclient.entity.AssignmentSubmission;
import moodleclient.entity.PrivateFile;
import moodleclient.exceptions.NotValidSessionException;
import moodleclient.helpers.CommandRunner;
import moodleclient.helpers.PrivateFileHelper;
import moodleclient.helpers.RequestCommand;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * FXML Controller class
 *
 * @author ralie
 */
public class TopDashboardController implements Initializable {
    private boolean serverReachable, isManual, isSyncing = false;
    private String textConnexion;
    
    private String startSyncText = "Start syncing";
    private String stopSyncText = "Stop syncing";
    public static int syncDone = 0;
    
    private BooleanProperty isSyncingProp ;
    private final Timer timer = new Timer();
    private RotateTransition rt;
    
    private CoursesLoader courseLoader;
    private PrivateFilesLoader privateFileLoader;
    
    @FXML
    private JFXButton syncBtn;
    @FXML
    private ImageView syncImg;
    @FXML
    private Label loginIndic;
    @FXML
    private Circle circleIndic;
    @FXML
    private Label username;
    @FXML
    private MenuItem syncMenu;
    @FXML
    private MenuItem logOutMenu;

    @FXML
    private Label syncingText;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        new ConnectivityListenner().start();
        
        this.syncingText.setVisible(false);

        this.isManual = !moodleclient.Moodleclient.autoSync;

        Tooltip toolTextSync = new Tooltip(startSyncText);
        syncBtn.setTooltip(toolTextSync);  
        
        syncBtn.setDisable(!this.isManual);


        //Rotation transition for the sync icon
        this.rt = new RotateTransition(Duration.millis(3000), syncImg);
        this.rt.setByAngle(360);
        this.rt.setCycleCount(Animation.INDEFINITE);
        this.rt.setInterpolator(Interpolator.LINEAR);
    } 
        
    
    @FXML
    private void handleSyncBtn(ActionEvent event) throws Exception{
        
        this.syncDone = 0;
        
        this.isSyncing = !this.isSyncing;
        
        if(this.isSyncing){
           
            //upload modifications of the private files
            Moodleclient.session.beginTransaction();
            
            List privateFiles = Moodleclient.session.createQuery("from PrivateFile PF where PF.synced=0").list();
            
            /*petit test*/System.out.print("La taille de la liste est : "+privateFiles.size()+"\n");
            
            for(Object obj: privateFiles){
                
                PrivateFile privateFile = (PrivateFile) obj;
                
                //voyons le contenu de cet objet
                /*petit test*/System.out.println("Contenu de l'objet obj : "+privateFile.getHashName());
                
                //build the url to update the file in the user's draft
                //String request = "curl -X POST -F \"file_1=@./files/" + privateFile.getHashName() + "\" " + moodleclient.Moodleclient.serverAddress + "webservice/upload.php?token=" + moodleclient.Moodleclient.user.getToken();
                
                //build the url to update the file in the user's draft
                String request = "cd files && curl -X POST -F \"file_1=@./" + privateFile.getHashName() + "\" " + moodleclient.Moodleclient.serverAddress + "webservice/upload.php?token=" + moodleclient.Moodleclient.user.getToken()+" && cd ../";
                //String request = "cd files && curl -X POST -F \"file_1=@./1671013096452_Chapitre1.pptx\" " + moodleclient.Moodleclient.serverAddress + "webservice/upload.php?token=" + moodleclient.Moodleclient.user.getToken()+" && cd ../";
                
                /*petit test*/System.out.println(request);
                
                String requestResponse = new RequestCommand(request).runCommand();
                
                // Affichons un peu le resultat de cette commande
                /*petit test*/System.out.println(requestResponse);
                
                //build the request to move the file to the private area of the user
                JSONParser parser = new JSONParser();
                
                JSONArray jarr = (JSONArray) parser.parse(requestResponse);
                
                JSONObject jobj = (JSONObject) jarr.get(0);
                
                //move the file to the private files of the user
                //build the request
                String draftId = jobj.get("itemid").toString();
                
                String request2 = "curl " + moodleclient.Moodleclient.serverAddress + "webservice/rest/server.php?moodlewsrestformat=json --data 'draftid=" + draftId + "&wsfunction=core_user_add_user_private_files&wstoken=" + moodleclient.Moodleclient.user.getToken() + "'";
                
                CommandRunner command2 = new CommandRunner(request2);
                command2.start();
                
                //set the current private file as uploaded
                byte b = 1;
                
                privateFile.setSynced(b);
                
                Moodleclient.session.save(privateFile);
            }
            
            Moodleclient.session.getTransaction().commit();
            
            //upload the assignment submissions
            Moodleclient.session.beginTransaction();
            
            List submissions = Moodleclient.session.createQuery("from AssignmentSubmission sub where sub.synced=0").list();
            
            for(Object obj: submissions){
                
                AssignmentSubmission submission = (AssignmentSubmission) obj;
                
                //build the url to update the file in the user's draft
                String request = "curl -X POST -F \"file_1=@./files/" + submission.getHashName() + "\" " + moodleclient.Moodleclient.serverAddress + "webservice/upload.php?token=" + moodleclient.Moodleclient.user.getToken();
                
                String requestResponse = new RequestCommand(request).runCommand();
                System.out.println(requestResponse);
                //build the request to move the file to the private area of the user
                JSONParser parser = new JSONParser();
                
                JSONArray jarr = (JSONArray) parser.parse(requestResponse);
                
                JSONObject jobj = (JSONObject) jarr.get(0);
                
                //move the file to the private files of the user
                //build the request
                String draftId = jobj.get("itemid").toString();
                
                String request2 = "curl " + moodleclient.Moodleclient.serverAddress +"webservice/rest/server.php?moodlewsrestformat=json --data 'wsfunction=mod_assign_save_submission&wstoken=" + moodleclient.Moodleclient.user.getToken() + "&assignmentid=" + submission.getDevoirs().getRemoteId() + "&plugindata[onlinetext_editor][text]=some_text&plugindata[onlinetext_editor][format]=1&plugindata[onlinetext_editor][itemid]=" + draftId + "&plugindata[files_filemanager]=" + draftId + "'" ;
                System.out.println(request2);
                
                String requestResponse2 = new RequestCommand(request2).runCommand();
                
                System.out.println(requestResponse2);
                
                //set the current private file as uploaded
                byte b = 1;
                
                submission.setSynced(b);
                
                Moodleclient.session.save(submission);
            }
            
            Moodleclient.session.getTransaction().commit();
            
            Moodleclient.session.close();
            
            Moodleclient.session = HibernateUtil.getSessionFactory().openSession();
            
            
            //delete the downloaded files
            //new CommandRunner("rm ./files/*").start(); //Version Linux
            System.out.println("Commande:__" + "del /s /q \".\\files\\*\"");
            new CommandRunner("del /s /q \".\\files\\*\"").start();
            
            moodleclient.Moodleclient.clearLocalDatabase();
            
            this.syncingText.setVisible(true);
            
            this.rt.play();
            
            this.courseLoader = new CoursesLoader();
            this.courseLoader.start();
            
            this.privateFileLoader = new PrivateFilesLoader();
            this.privateFileLoader.start();
            
            Tooltip toolTextSync = new Tooltip(stopSyncText);
            syncBtn.setTooltip(toolTextSync);
            
        } else {
            
            this.syncingText.setVisible(false);
            
            Tooltip toolTextSync = new Tooltip(startSyncText);
            syncBtn.setTooltip(toolTextSync); 
            
            this.rt.stop();
        }
    }
    
     
    @FXML
    private void handleSyncMenu(ActionEvent event) throws Exception{
        
        this.isManual = !SyncAlertBox.display();
        
        syncBtn.setDisable(!this.isManual);
    }

    @FXML
    private void handleLogOutMenu(ActionEvent event) throws Exception{
        LogOutAlertBox.display();
    }
    
    private void setConnectivity() throws Exception{
    
        if(this.serverReachable){
            textConnexion = "You are connected to the server.";
            Tooltip toolTextConnexion = new Tooltip(textConnexion);
            //loginIndic.setTooltip(toolTextConnexion);
            circleIndic.setFill(Color.valueOf("37CA41")); //the color is green

        } else{
            textConnexion = "You are not connected to the server.";
            Tooltip toolTextConnexion = new Tooltip(textConnexion);
            //loginIndic.setTooltip(toolTextConnexion);
            circleIndic.setFill(Color.valueOf("FA5F57")); //the color is red
        }
    }
    
    //class for the connectivity listenning
    class ConnectivityListenner extends Thread{

        @Override
        public void run() {
            new Timer().scheduleAtFixedRate(new TimerTask(){
                @Override
                public void run(){
                    try {
                    // your code here
                        serverReachable = moodleclient.Moodleclient.serverReachable();
                        
                        try {
                            
                            setConnectivity();
                            
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    } catch (IOException ex) {

                        ex.printStackTrace();
                    }
                }
                
            }, 0, 3000);
        }
    }
    
    //function to update the application list of courses
    public void updateCoursesList() throws IOException{
        
        this.syncDone++;
        
        if(syncDone == 2){
            this.syncingText.setVisible(false);
            this.rt.stop();

            Tooltip toolTextSync = new Tooltip(startSyncText);
            this.syncBtn.setTooltip(toolTextSync);
        }
        
        Moodleclient.session.beginTransaction();
        
        Moodleclient.courses =  Moodleclient.session.createQuery("from Cours").list();
        
        Moodleclient.session.getTransaction().commit();
        
    }
    
    //function to update the application list of private files
    public void updatePrivateFilesList() throws IOException{
        
        this.syncDone++;
        
        if(syncDone == 2){
            this.syncingText.setVisible(false);
            this.rt.stop();

            Tooltip toolTextSync = new Tooltip(startSyncText);
            this.syncBtn.setTooltip(toolTextSync);
        }
        
        Moodleclient.session.beginTransaction();
        
        Moodleclient.privateFiles =  Moodleclient.session.createQuery("from PrivateFile").list();
        
        Moodleclient.session.getTransaction().commit();
    }
    
    //function to pull and display courses
    class CoursesLoader extends Thread{
        
        public CoursesLoader(){
            
        }

        @Override
        public void run() {
        
          CoursesHelper coursesHlpr = new CoursesHelper();
            
            JSONArray courses, assignments;
            
            try {
                
                courses = coursesHlpr.pullcourses();
                assignments = coursesHlpr.pullAssignments();
                
                coursesHlpr.saveCourses(courses, assignments);
                
                updateCoursesList();
                
            } catch (IOException ex) {
                
                ex.printStackTrace();
                
            } catch (ParseException ex) {
                
                ex.printStackTrace();
                
            } catch (ServerUnreachableException ex) {
                
                ex.printStackTrace();
                
            }
        }
   
     }
    
    //function to pull and display private files
    class PrivateFilesLoader extends Thread{
        
        public PrivateFilesLoader(){
            
        }

        @Override
        public void run() {
        
            PrivateFileHelper privateFileHelper = new PrivateFileHelper();
            
            try {
                
                privateFileHelper.pullPrivateFiles();
                
                updatePrivateFilesList();
                
            } catch (ParseException ex) {
                
                ex.printStackTrace();
                
            } catch (NotValidSessionException ex) {
                
                ex.printStackTrace();
                
            } catch (ServerUnreachableException ex) {
                
                ex.printStackTrace();
                
            } catch (IOException ex) {
                
                ex.printStackTrace();
            }
        }
    }
    
}
