package SSubmitAssignment;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import moodleclient.Moodleclient;
import moodleclient.entity.AssignmentSubmission;
import moodleclient.entity.Devoirs;
import moodleclient.helpers.CommandRunner;
import moodleclient.util.HibernateUtil;

public class SubmitAssignmentController implements Initializable{
	

    @FXML
    private Label courseName;

    @FXML
    private JFXButton fileChooser;

    @FXML
    private JFXButton btnSave;

    @FXML
    private JFXButton btnCancel;


    @FXML
    private TextArea list;

    final FileChooser fc = new FileChooser();

    @FXML
    private Label devoirId;
    @FXML
    private ScrollPane scrollpane;
    
    private List<File> filesList = new ArrayList<File>();
    private List devoirs;
    private Devoirs devoir = null;
    private boolean viewOnly = false; //On est en mode viewOnly lorsque le devoir est déjà noté
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
       // TODO Auto-generated method stub
       devoirs = Moodleclient.session.createQuery("from Devoirs D where D.id=" + Moodleclient.currentAssignmentId).list();
       if(devoirs.size()>0){
           devoir = (Devoirs) devoirs.get(0);
           if(devoir.getNote() != null){
               viewOnly = true;
               fileChooser.setDisable(true); btnSave.setDisable(true); btnCancel.setDisable(true);
           }
       }
       loadSubmissions();
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
        
            if (files != null){
                for (int i = 0; i< files.size(); i++){
                    list.appendText(files.get(i).getAbsoluteFile()+ "\n");
                    //filesList.getItems().add(files.get(i).getAbsolutePath());

                    //append the file to the files List
                    filesList.add(files.get(i));
                }
                
            }else{
                System.out.println("File is invalid!");
            }
            
    }
    
    public void saveFiles() throws IOException{
        
        Moodleclient.session.beginTransaction();
        
        for(Object obj: filesList){
            File file = (File)obj;
            
            String hashName = file.getName();
            
            //CommandRunner commandRunner = new CommandRunner("cp '" + file.getAbsoluteFile() + "' ./files/'" + hashName + "'");
            CommandRunner commandRunner = new CommandRunner("copy \"" + file.getAbsoluteFile() + "\" \"./files/" + hashName + "\"");

            commandRunner.start();
            
            //save the file in the database            
            if(devoir != null){
                
                byte b = 0;
                AssignmentSubmission subFile = new AssignmentSubmission(devoir, file.getName(), hashName, new Date(), new Date(), b);
            
                Moodleclient.session.save(subFile);
                
            }
            
        }

        Moodleclient.session.getTransaction().commit();
        
        Moodleclient.session.close();
        
        Moodleclient.session = HibernateUtil.getSessionFactory().openSession();

        loadSubmissions();
        
        //clear the list of files
        cancel();
    }
    
    public void cancel(){
        //clear the files list
        filesList.clear();
        
        list.setText("");
    }
    
    //fonction to load the submissions of the assignment
    public void loadSubmissions(){
        
        Moodleclient.session.beginTransaction();
        
        List submissions = Moodleclient.session.createQuery("from AssignmentSubmission sub where sub.devoirs.id=" + Moodleclient.currentAssignmentId).list();
        
        Moodleclient.session.getTransaction().commit();
        
        try {

            GridPane filesGridPane = new GridPane();

            for(int i = 0; i < submissions.size(); i++){

                AssignmentSubmission asub = (AssignmentSubmission)submissions.get(i);

                //create the component to display the file
                FXMLLoader fileLoader = new FXMLLoader(getClass().getResource("/SCourse/documentButton.fxml"));
                Pane fileButton = (Pane) fileLoader.load();

                Label fileName = (Label) fileLoader.getNamespace().get("fileName");

                fileName.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Moodleclient.FileLauncher fileLauncher = new Moodleclient.FileLauncher(asub.getHashName());
                        fileLauncher.start();
                    }
                });

                fileName.setText(asub.getFileName());

                //set the delete button
                ImageView deleteImg = (ImageView) fileLoader.getNamespace().get("deleteBtn");
                
                if(viewOnly){
                    //disable the delete button
                    deleteImg.setDisable(true);
                    deleteImg.setVisible(false);
                }

                deleteImg.setOnMouseClicked(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        //delete the current private file from the local storage
                        //CommandRunner deleteCommand = new CommandRunner("rm ./files/'" + asub.getHashName() + "'");
                        CommandRunner deleteCommand = new CommandRunner("del \".\\files\\" + asub.getHashName() + "\"");
                        deleteCommand.start();
                        
                        //delete the instance from the database
                        Moodleclient.session.beginTransaction();

                        Moodleclient.session.delete(asub);

                        Moodleclient.session.getTransaction().commit();

                        loadSubmissions();
                    }
                });

                filesGridPane.add(fileButton, 0, i, 1, 1);

            }   

            this.scrollpane.setContent(filesGridPane);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
