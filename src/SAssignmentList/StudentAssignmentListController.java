package SAssignmentList;

import SDashboard.DashboardController;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import moodleclient.Moodleclient;
import moodleclient.Moodleclient.FileLauncher;
import moodleclient.entity.Cours;
import moodleclient.entity.CourseFile;
import moodleclient.entity.Devoirs;
import moodleclient.entity.RessourceDevoir;
import moodleclient.entity.Sections;
import moodleclient.helpers.SortableSection;
import moodleclient.util.HibernateUtil;

public class StudentAssignmentListController implements Initializable{
	
    @FXML
    private ScrollPane scrollpane;

    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
        
        if(moodleclient.Moodleclient.assignments.size() > 0){
            
            GridPane gridpane = new GridPane();

            try {

                for(int i = 0; i < moodleclient.Moodleclient.assignments.size(); i++){

                    Devoirs devoir = (Devoirs)moodleclient.Moodleclient.assignments.get(i);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/SAssignmentCard/AvailableAssignmentCard.fxml"));

                    Pane content = (Pane)loader.load();

                    //set the onmmouseclicked event of the component
                    content.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {

                            try {
                                //load the anchorPane for the view of the course
                                FXMLLoader assignmentLoader = new FXMLLoader(getClass().getResource("/SAssignment/StudentAssignment_1.fxml"));

                                AnchorPane anchorPane = (AnchorPane) assignmentLoader.load();
                                
                                Label topic  = (Label) assignmentLoader.getNamespace().get("topicName");
                                topic.setText(devoir.getCours().getNom() + "/" + devoir.getEnonce());
                                
                                moodleclient.Moodleclient.currentAssignmentId = devoir.getId().toString();
                                
                                Label status  = (Label) assignmentLoader.getNamespace().get("status");
                                JFXButton btnAddSub  = (JFXButton) assignmentLoader.getNamespace().get("btnAddSub");
                                
                                Label dueDate  = (Label) assignmentLoader.getNamespace().get("dueDate");
                                dueDate.setText(Moodleclient.dateFormat.format(devoir.getDateLimite()));
                                
                                //Mise en forme
                                if(devoir.getEtat().equalsIgnoreCase("submitted")){
                                    status.setTextFill(Color.BLUE);
                                    btnAddSub.setText("View/Modify submission");
                                }
                                else{
                                    status.setTextFill(Color.MAROON);
                                    
                                    if(devoir.getDateLimite().compareTo(new Date()) <= 0){
                                     dueDate.setTextFill(Color.MAROON);
                                    }
                                }
                                
                                String _etat = devoir.getEtat();
                                if(devoir.getNote() != null){
                                    _etat += ", graded";
                                    Label gradeLabel  = (Label) assignmentLoader.getNamespace().get("gradeLabel");
                                    gradeLabel.setText(devoir.getNote()+ "/" + devoir.getNoteMax());
                                    
                                    btnAddSub.setText("View submission");
                                }
                                status.setText(_etat);
                                
                                
                                GridPane gridpane = (GridPane) assignmentLoader.getNamespace().get("gridpane");
                                
                                Moodleclient.session = HibernateUtil.getSessionFactory().openSession();
                                
                                //load and set the files of the assignment
                                Set assignmentFiles = devoir.getRessourceDevoirs();

                                int k = 3;

                                for(Object file : assignmentFiles){

                                    RessourceDevoir courseFile = (RessourceDevoir) file;

                                    //create the component to display the file
                                    FXMLLoader fileLoader = new FXMLLoader(getClass().getResource("/SCourse/documentButton.fxml"));
                                    Pane fileButton = (Pane) fileLoader.load();

                                    //disable the delete button
                                    ImageView deleteImg = (ImageView) fileLoader.getNamespace().get("deleteBtn");

                                    deleteImg.setDisable(true);
                                    deleteImg.setVisible(false);

                                    fileButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                        @Override
                                        public void handle(MouseEvent event) {

                                            // lorsqu'on clique sur un devoir (when we clic on an assignemnt)
                                            FileLauncher fileLauncher = new FileLauncher(courseFile.getHashName());
                                            fileLauncher.start();
                                        }
                                    });

                                    Label fileName = (Label) fileLoader.getNamespace().get("fileName");

                                    BorderPane t = new BorderPane();

                                    fileName.setText(courseFile.getFileName());

                                    gridpane.add(fileButton, 0, k , 1, 1);
                                    
                                    k++;
                                }
                                
                                //change the center of the root scene
                                moodleclient.Moodleclient.root.setCenter(anchorPane);
                                
                            } catch (IOException ex) {
                                
                                ex.printStackTrace();
                            }
                        }
                    });

                    Label assignmentName = (Label) loader.getNamespace().get("courseAssignmentName");
                    Label assignMentDesc = (Label) loader.getNamespace().get("assignmentDesc");
                    Label assignmentStatus = (Label) loader.getNamespace().get("assignmentStatus");

                    assignmentName.setText(devoir.getCours().getNom() + "/" + devoir.getEnonce());
                    assignMentDesc.setText("due date: " + Moodleclient.dateFormat.format(devoir.getDateLimite()));
                    
                    String _etat = devoir.getEtat();
                    if(devoir.getNote() != null) _etat += ", graded";
                    assignmentStatus.setText(_etat);
                    
                    //Mise en forme. Si le devoir n'est pas soumis et que la date limite est dépassée...
                    if(devoir.getEtat().equalsIgnoreCase("submitted")){
                        assignmentStatus.setTextFill(Color.BLUE);
                    }
                    else{
                        assignmentStatus.setTextFill(Color.MAROON);
                        if(devoir.getDateLimite().compareTo(new Date()) <= 0){
                         assignMentDesc.setTextFill(Color.MAROON);
                        }
                    }

                    gridpane.add(content, 0, i, 1, 1);
                }
                
            } catch (IOException ex) { 
                
                ex.printStackTrace();
            }
            
            this.scrollpane.setContent(gridpane);
            
        }

    }
}
