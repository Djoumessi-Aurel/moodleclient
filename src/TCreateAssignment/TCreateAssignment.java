package TCreateAssignment;

import Models.Course;
import SSavePrivateFiles.*;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import moodleclient.Moodleclient;
import moodleclient.Moodleclient.FileLauncher;
import moodleclient.entity.Cours;
import moodleclient.entity.CourseFile;
import moodleclient.entity.Devoirs;
import moodleclient.entity.PrivateFile;
import moodleclient.entity.RessourceDevoir;
import moodleclient.entity.Sections;
import moodleclient.helpers.CommandRunner;
import moodleclient.helpers.SortableSection;
import moodleclient.util.HibernateUtil;
import org.hibernate.Session;

public class TCreateAssignment implements Initializable{
	
    @FXML
    private JFXTextArea list;

    final FileChooser fc = new FileChooser();
    
    @FXML
    private JFXButton fileChooser;
    
    @FXML
    private ScrollPane scrollpane;
    
    @FXML
    private JFXButton createAssingnmentBtn;
    
    @FXML
    private JFXButton cancelBtn;
    
    @FXML
    private TextField assignmentName;
    
    @FXML
    private TextField description;
    
    @FXML
    private TextField openDate;
    
    @FXML
    private TextField dueDate;
    
    
    private List<File> filesList = new ArrayList<File>();
	    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
        
        //loadPrivateFiles();
    }

    @FXML
    private void handleBtnChooser(ActionEvent event) {
        
        fc.setTitle("My File Chooser");
        
        //Initial directory for the displayed dialog
        fc.setInitialDirectory(new File(System.getProperty("user.home")));
        
        //Extension filters used in the display dialog
        fc.getExtensionFilters().clear();
        fc.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("All files", "*.*"),
            new FileChooser.ExtensionFilter("Text Files", "*.txt"),
            new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));
        
        List<File> files = fc.showOpenMultipleDialog(null);
        
        if(files != null){
            for (int i = 0; i< files.size(); i++){

               list.appendText(files.get(i).getAbsoluteFile()+ "\n");
               //filesList.getItems().add(files.get(i).getAbsolutePath());
               
               //append the file to the files List
               filesList.add(files.get(i));
                
            }
        }
        else{
            System.out.println("File is invalid!");
        }
            
    }
    
    @FXML
    public void createAssingnment() throws IOException{
        
        Moodleclient.session.beginTransaction();

        System.out.println("Entrée dans la fonction de sauvegarde de fichiers (ressources) du devoir");
        
        
        System.out.println("Enoncé du devoir : "+assignmentName.getText());
        
        //on créé le devoir puis on l'enregistre dans la BD
        //Devoirs newdevoir = new Devoirs(new Course(), assignmentName.getText(), dueDate.getText(), "", Moodleclient.currentAssignmentId, new Date(), new Date(), filesList, new HashSet<>);
            
        //Moodleclient.session.save(newdevoir);
        
        for(Object obj: filesList){
            File file = (File)obj;
            
            String hashName = file.getName();

            //CommandRunner commandRunner = new CommandRunner("cp '" + file.getAbsoluteFile() + "' ./files/'" + hashName + "'"); //Code Linux
            System.out.println("Commande: " + "copy \"" + file.getAbsoluteFile() + "\" \".\\files\\" + hashName + "\"");
            CommandRunner commandRunner = new CommandRunner("copy \"" + file.getAbsoluteFile() + "\" \".\\files\\" + hashName + "\"");

            commandRunner.start();
            
            //save the file in the database
            byte b = 0;
            
            //PrivateFile privateFile = new PrivateFile(file.getName(), hashName, new Date(), new Date(), b);
            
            //RessourceDevoir ressourceDevoir = new RessourceDevoir(newdevoir, file.getName(), hashName, new Date(), new Date());
            
            //Moodleclient.session.save(ressourceDevoir);
        }

        Moodleclient.session.getTransaction().commit();
        
        //update the private files list of the application
        Moodleclient.session.beginTransaction();
        
        moodleclient.Moodleclient.privateFiles = Moodleclient.session.createQuery("from PrivateFile").list();
        
        Moodleclient.session.getTransaction().commit();
        
        //loadPrivateFiles();
        
        //clear the list of files
        cancel();
    }
    
    @FXML
    public void cancel(){
        //clear the files list
        filesList.clear();
        
        list.setText("");
    }
    
    //fonction to load and display the private files
//    public void loadPrivateFiles(){
//
//        if(moodleclient.Moodleclient.privateFiles.size() > 0){
//
//            try {
//                
//                GridPane filesGridPane = new GridPane();
//                
//                for(int i = 0; i < moodleclient.Moodleclient.privateFiles.size(); i++){
//                    
//                    PrivateFile privateFile = (PrivateFile)moodleclient.Moodleclient.privateFiles.get(i);
//
//                    //create the component to display the file
//                    FXMLLoader fileLoader = new FXMLLoader(getClass().getResource("/SCourse/documentButton.fxml"));
//                    Pane fileButton = (Pane) fileLoader.load();
//                    
//                    Label fileName = (Label) fileLoader.getNamespace().get("fileName");
//                    
//                    fileName.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                        @Override
//                        public void handle(MouseEvent event) {
//                            FileLauncher fileLauncher = new FileLauncher(privateFile.getHashName());
//                            fileLauncher.start();
//                        }
//                    });
//
//                    fileName.setText(privateFile.getFileName());
//                    
//                    //set the delete button
//                    ImageView deleteImg = (ImageView) fileLoader.getNamespace().get("deleteBtn");
//                    
//                    deleteImg.setOnMouseClicked(new EventHandler<MouseEvent>() {
//                        @Override
//                        public void handle(MouseEvent event) {
//                            //delete the current private file from the local storage
//                            System.out.println("Commande: " + "del \".\\files\\" + privateFile.getHashName() + "\"");
//                            //CommandRunner deleteCommand = new CommandRunner("rm \"./files/" + privateFile.getHashName() + "\""); //Commande Linux
//                            CommandRunner deleteCommand = new CommandRunner("del \".\\files\\" + privateFile.getHashName() + "\"");
//                            deleteCommand.start();
//                            
//                            //delete the instance from the database
//                            Moodleclient.session.beginTransaction();
//                            
//                            Moodleclient.session.delete(privateFile);
//                            
//                            Moodleclient.session.getTransaction().commit();
//                            
//                            
//                            Moodleclient.session.beginTransaction();
//                            
//                            Moodleclient.privateFiles = Moodleclient.session.createQuery("from PrivateFile").list();
//                            
//                            Moodleclient.session.getTransaction().commit();
//                            
//                            //update the private files list of the application
//                            loadPrivateFiles();
//                        }
//                    });
//
//                    filesGridPane.add(fileButton, 0, i, 1, 1);
//
//                }   
//
//                this.scrollpane.setContent(filesGridPane);
//
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            }
//        }
//        else{ //Si la liste des fichiers privés est vide
//            this.scrollpane.setContent(new GridPane());
//        }
//
//    }

}
