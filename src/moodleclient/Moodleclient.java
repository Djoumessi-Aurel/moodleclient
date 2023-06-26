/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import login.Dry;
import moodleclient.entity.Cours;
import moodleclient.entity.Sections;

import moodleclient.entity.Users;
import moodleclient.helpers.AccountHelper;
import moodleclient.helpers.CurrentTab;
import moodleclient.util.HibernateUtil;

import org.hibernate.Session;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author pepi
 */
public class Moodleclient extends Application {
   
    public static Session session;
    public static Users user;
    public static String sessionId = "";
    public static String serverAddress;
    public static String PRIVILEGED_TOKEN = "c1b2822bd4e1187c62fd43aa765cd895"; //Permet d'effectuer des opérations telles que: récupérer les soumissions de devoirs
    public static CurrentTab CURRENT_TAB = CurrentTab.DASHBOARD; //Représente l'onglet courant (Dashboard, Private Files ou Assignment).
    
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy , HH:mm");
    
    public static Cours dashboardCourse = null; //Le cours actuellement sélectionné dans "dashboard"
    public static Sections dashboardSection = null; //La section actuellement sélectionnée dans "dashboard"
    
    public static boolean autoSync;
    
    public static List courses = new ArrayList();
    public static List privateFiles = new ArrayList();
    public static List assignments = new ArrayList();
    public static String currentAssignmentId = "";
    
    public static AnchorPane loginAnchorPane;
    
    public static BorderPane root;
    private DoubleProperty widthProperty;
    
    @Override
    public void start(Stage stage) throws Exception {
        
        //Initialization of the session for the communication with the database
        this.session = HibernateUtil.getSessionFactory().openSession();
        
        //load the application configuration
        loadConfiguraton();
        
        this.root = (BorderPane) FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        Scene scene = new Scene(this.root);
        
        //check if there is always an account in the application
        AccountHelper accountHelper = new AccountHelper();
        
        //load the login anchorpane
        this.loginAnchorPane = (AnchorPane)FXMLLoader.load(getClass().getResource("/login/login.fxml"));

                
        if(!accountHelper.accountCreated()){
            //load the fxml document to create an account and set the server's address
            //*******************************************
            
            this.root.setCenter(loginAnchorPane);   
            
        }else{
            
            this.user = accountHelper.getAccount();
            
            //set the server address
            this.serverAddress = this.user.getServerUrl();    
            
            //get the list of courses from the local database
            this.courses = this.session.createQuery("from Cours").list();
            
            //get the list of private files from the local database
            this.privateFiles = this.session.createQuery("from PrivateFile").list();
            
            //get the list of assignments from the local database
            this.assignments = this.session.createQuery("from Devoirs").list();
            
            //load the fxml document to display the home of the application
            //**************************************************************
            Dry dry = new Dry();
            dry.showDashboard(this.root);
        }
        
        stage.setScene(scene);
        stage.setMinWidth(1080);
        stage.setHeight(650);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    //function to check if the server is reachable
    public static boolean serverReachable() throws MalformedURLException, IOException{
                    
        URL url = new URL(serverAddress);
            
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
            
        con.setRequestMethod("GET");
        
        try{
           con.connect();
           
        }catch(Exception e){
            return false;
        }
        
        return true;
    }
    
    //function to save the configuration in the configuration file
    public static void saveConfiguration(JSONObject jo) throws FileNotFoundException, IOException, ParseException{
        
        
        
        PrintWriter pw = new PrintWriter("./etc/moodleclientconf.json");
        pw.write(jo.toString());
        
        pw.flush();
        pw.close();
        
        loadConfiguraton();
    }
    
    //function to load parameters from the configuration file
    public static JSONObject loadConfiguraton() throws FileNotFoundException, IOException, ParseException{
        
        Object obj = new JSONParser().parse(new FileReader("./etc/moodleclientconf.json"));
        
        JSONObject jo = (JSONObject) obj;
        
        //set the server's address
        try{
            
            serverAddress = jo.get("serverAddress").toString();
            
        }catch(NullPointerException e){
            
            e.printStackTrace();
        }
        
        if(jo.get("autoSync").toString().equalsIgnoreCase("true")){
            autoSync = true;
            
        }else{
            autoSync = false;
        }
        
        return jo;
    }
    
    //function to clear the local database
    public static void clearLocalDatabase(){
        session.beginTransaction();
        
        session.createQuery("delete from CourseFile").executeUpdate();
        session.createQuery("delete from RessourceDevoir").executeUpdate();
        session.createQuery("delete from PrivateFile").executeUpdate();
        session.createQuery("delete from Sections").executeUpdate();
        session.createQuery("delete from AssignmentSubmission").executeUpdate();
        session.createQuery("delete from Devoirs").executeUpdate();
        session.createQuery("delete from Cours").executeUpdate();
        
        session.getTransaction().commit();
        
        new FilesCleaner().start();
    }
    
    //class used to clean/delete files
    static class FilesCleaner extends Thread{
        
        public FilesCleaner(){
            super();
        }

        @Override
        public void run() {
            
            ProcessBuilder processBuilder = new ProcessBuilder();

            //processBuilder.command("bash", "-c", "rm ./files/*" + " &");
            //on vide le contenu du dossier files
            processBuilder.command("cmd.exe", "/c", " del /q .\\files\\*");

            try{
                Process process = processBuilder.start();

                StringBuilder output = new StringBuilder();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;

                while((line = reader.readLine()) != null){
                    output.append(line + "\n");
                    System.out.println(line + "\n");
                }

                int exitval = process.waitFor();

                if(exitval == 0){
                    System.out.println("success!");
                    System.out.println(output);

                }else{

                }

            }catch(IOException e){
                e.printStackTrace();

            }catch(InterruptedException e){
                e.printStackTrace();

            }
        }
    }
    
    //class used to launch a file/ classe utilisee pour lancer un fichier
    public static class FileLauncher extends Thread{
        
        public String fileName;
        
        public FileLauncher(String _fileName){
            super();
            
            this.fileName = _fileName;
        }

        @Override
        public void run() {
            
            ProcessBuilder processBuilder = new ProcessBuilder();

            processBuilder.command("cmd.exe", "/c", " \".\\files\\"+fileName+"\"");

            try{
                Process process = processBuilder.start();

                StringBuilder output = new StringBuilder();

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;

                while((line = reader.readLine()) != null){
                    output.append(line + "\n");
                }

                int exitval = process.waitFor();

                if(exitval == 0){
                    System.out.println("success!");
                    System.out.println(output);

                }else{

                }

            }catch(IOException e){
                e.printStackTrace();

            }catch(InterruptedException e){
                e.printStackTrace();

            }
        }
    }
}
