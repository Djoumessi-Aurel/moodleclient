package SDashboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import moodleclient.Moodleclient;
import moodleclient.Moodleclient.FileLauncher;
import moodleclient.entity.Cours;
import moodleclient.entity.CourseFile;
import moodleclient.entity.Sections;
import moodleclient.helpers.SortableSection;
import moodleclient.util.HibernateUtil;
import org.hibernate.Session;
import sun.font.TextLabel;


public class DashboardController implements Initializable{

    @FXML
    private ScrollPane scrollpane;
  
    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        System.out.println("### INIT DASHBOARD ###");
        if(Moodleclient.courses.size() > 0){
            GridPane gridpane = new GridPane();

            try {

                for(int i = 0; i < Moodleclient.courses.size(); i++){

                    Cours cours = (Cours) Moodleclient.courses.get(i);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/SCourseCard/AvailableCourseCard.fxml"));

                    Pane content = (Pane)loader.load();

                    //set the onmmouseclicked event of the component
                    content.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {

                            try {
                                Moodleclient.dashboardCourse = cours;
                                
                                //load the anchorPane for the view of the course
                                FXMLLoader courseLoader = new FXMLLoader(getClass().getResource("/SCourse/StudentCourse.fxml"));

                                AnchorPane anchorPane = (AnchorPane) courseLoader.load();

                                Label courseName2  = (Label) courseLoader.getNamespace().get("courseName");
                                courseName2.setText(cours.getNom());
                                
                                //load and set the sections of the course
                                Set sections = cours.getSectionses();
                                
                                List sectionsList = new ArrayList();
                                
                                for(Object section : sections){

                                    sectionsList.add(new SortableSection( (Sections) section));
                                }

                                Collections.sort(sectionsList);

                                ScrollPane scrollpane2 = (ScrollPane) courseLoader.getNamespace().get("scrollpane");

                                GridPane sectionGridPane = new GridPane();

                                for(int j = 0; j < sectionsList.size(); j++){

                                    Sections section = ((SortableSection)sectionsList.get(j)).section;

                                    FXMLLoader sectionLoader = new FXMLLoader(getClass().getResource("/SCourse/courseSection.fxml"));

                                    Pane sectionPane = (Pane) sectionLoader.load();

                                    Label sectionName = (Label) sectionLoader.getNamespace().get("sectionName");
                                    sectionName.setText(section.getNom());
                                    
                                    Label labelGererRessources = (Label) sectionLoader.getNamespace().get("labelGererRessources");
                                    
                                    labelGererRessources.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){
                                        @Override
                                        public void handle(MouseEvent event) {
                                            Moodleclient.dashboardSection = section;
                                            System.out.println("Section mise à jour");
                                        }                                        
                                    });

                                    //load and set the files of the section
                                    BorderPane filesBorderPane = (BorderPane) sectionLoader.getNamespace().get("filesBorderPane");

                                    GridPane filesGridPane = new GridPane();

                                    Set sectionFiles = section.getCourseFiles();

                                    int k = 0;

                                    for(Object file : sectionFiles){

                                        CourseFile courseFile = (CourseFile) file;

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
                                                FileLauncher fileLauncher = new Moodleclient.FileLauncher(courseFile.getHashName());
                                                fileLauncher.start();
                                            }
                                        });

                                        Label fileName = (Label) fileLoader.getNamespace().get("fileName");


                                        fileName.setText(courseFile.getFileName());

                                        filesGridPane.add(fileButton, 0, k , 1, 1);
                                        k++;

                                    }

                                    filesBorderPane.setCenter(filesGridPane);

                                    sectionGridPane.add(sectionPane, 0, j, 1, 1);
                                }

                                scrollpane2.setContent(sectionGridPane);

                                //change the center of the root scene
                                moodleclient.Moodleclient.root.setCenter(anchorPane);


                            } catch (IOException ex) {

                                ex.printStackTrace();
                            }
                        }
                    });

                    Label courseName = (Label) loader.getNamespace().get("courseName");
                    Label coursDesc = (Label) loader.getNamespace().get("courseDesc");

                    courseName.setText(cours.getNom());
                    coursDesc.setText(cours.getDescription());

                    gridpane.add(content, 0, i, 1, 1);
                }   

                this.scrollpane.setContent(gridpane);

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
        
}
               
        
       
       
       
       
      
       
       
       
       
       
       


