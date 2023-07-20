package SDashboard;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
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
import login.Dry;
import moodleclient.Moodleclient;
import moodleclient.Moodleclient.FileLauncher;
import static moodleclient.Moodleclient.root;
import moodleclient.entity.Cours;
import moodleclient.entity.CourseFile;
import moodleclient.entity.Sections;
import moodleclient.helpers.DialogCreator;
import moodleclient.helpers.SortableSection;
import moodleclient.util.HibernateUtil;
import org.hibernate.Session;
import sun.font.TextLabel;


public class DashboardController implements Initializable{

    @FXML
    private ScrollPane scrollpane;
    @FXML
    private Label labelNewCourse;
  
    @Override
    public void initialize(URL arg0, ResourceBundle arg1){
        
        if(Moodleclient.user.isStudent()){
            labelNewCourse.setDisable(true);
            labelNewCourse.setVisible(false);
        }
        
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
    
    
    @FXML
    public void handleNewCourse() throws IOException{
        //System.out.println("CREATION D'UN NOUVEAU COURS");
        Optional<Cours> opCourse = DialogCreator.launchCourseDialog("create", null);
        
        if(opCourse.isPresent()){
            Byte b = 0;
            Cours cours = new Cours(opCourse.get().getNom(), opCourse.get().getNomAbrege(), opCourse.get().getDescription(), "-1", new Date(), new Date(), new HashSet(), new HashSet(), b);
       
            Moodleclient.session.beginTransaction();
            
            Moodleclient.session.save(cours); //Save the new course to database
            
            Moodleclient.session.save(new Sections(cours, "Généralités", new Date(), new Date(), -5, new HashSet(), (byte)0));
            Moodleclient.session.save(new Sections(cours, "Section 1", new Date(), new Date(), -4, new HashSet(), (byte)0));
            Moodleclient.session.save(new Sections(cours, "Section 2", new Date(), new Date(), -3, new HashSet(), (byte)0));
            Moodleclient.session.save(new Sections(cours, "Section 3", new Date(), new Date(), -2, new HashSet(), (byte)0));
            Moodleclient.session.save(new Sections(cours, "Section 4", new Date(), new Date(), -1, new HashSet(), (byte)0));
            
            Moodleclient.session.getTransaction().commit();
            Moodleclient.session.close();
        
            Moodleclient.session = HibernateUtil.getSessionFactory().openSession();
            
            Moodleclient.courses =  Moodleclient.session.createQuery("from Cours").list();
            new Dry().showDashboard(root); //Refresh the dashboard page
        }
    }
        
}
               
        
       
       
       
       
      
       
       
       
       
       
       


