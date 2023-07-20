package TAssignmentList;



import SDashboard.DashboardController;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import javafx.scene.paint.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import moodleclient.Moodleclient;
import moodleclient.Moodleclient.FileLauncher;
import static moodleclient.Moodleclient.root;
import moodleclient.entity.AssignmentSubmission;
import moodleclient.entity.Cours;
import moodleclient.entity.CourseFile;
import moodleclient.entity.Devoirs;
import moodleclient.entity.RessourceDevoir;
import moodleclient.entity.Sections;
import moodleclient.helpers.CreateAssignment;
import moodleclient.helpers.DeleteCourse;
import moodleclient.helpers.GradeAssignment;
import moodleclient.helpers.RequestCommand;
import moodleclient.helpers.SortableSection;
import moodleclient.util.HibernateUtil;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class TeacherAssignmentListController implements Initializable{
	
    @FXML
    private ScrollPane scrollpane;
    
    private int pos = 0;

    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
        
        if(moodleclient.Moodleclient.courses.size() > 0){
            
            GridPane gridpane = new GridPane();

            try {

                for(int i = 0; i < moodleclient.Moodleclient.courses.size(); i++){

                    Cours cours = (Cours)moodleclient.Moodleclient.courses.get(i);

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/TAssignmentCard/TAvailableAssignmentCard.fxml"));

                    Pane content = (Pane)loader.load();

                    //set the onmmouseclicked event of the component
                    content.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {

                            // On entre dans cette méthode lorqu'on clique sur un devoir
                            // On doit maintenant reccuperer la liste des devoirs correspondant a un cours
                            Devoirs devoirtest = (Devoirs)moodleclient.Moodleclient.assignments.get(0);
                            System.out.println("Devoir 1 : " + devoirtest.getCours().getId());
                            

                            try {

                                
                                // Il faut vider le gridpane avant de mettre les nouveaux éléments a l'intérieur
                                gridpane.getChildren().clear();
                                
                                
                                for(int i = 0; i < moodleclient.Moodleclient.assignments.size(); i++){
                                    
                                    
                                    Devoirs devoir = (Devoirs)moodleclient.Moodleclient.assignments.get(i);
                                    
                                    if (devoir.getCours().getId() == cours.getId()){ // il s'agit d'un devoir de ce cours
                                         
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/TAssignmentOfCourseCard/TAssignmentOfCourseCard.fxml"));

                                        Pane content = (Pane)loader.load();

                                        //set the onmmouseclicked event of the component
                                        content.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                            @Override
                                            public void handle(MouseEvent event) {

                                                try {
                                                    //load the anchorPane for the view of the assignment
                                                    System.out.println("On a cliqué sur le devoir intitulé : " + devoir.getEnonce());
                                                    FXMLLoader assignmentLoader = new FXMLLoader(getClass().getResource("/TAssignment/TeacherAssignment_1.fxml"));
    
                                                    AnchorPane anchorPane = (AnchorPane) assignmentLoader.load();
    //
                                                    Label topic  = (Label) assignmentLoader.getNamespace().get("topicName");
                                                    topic.setText(devoir.getCours().getNom() + "/" + devoir.getEnonce());
    //
                                                    moodleclient.Moodleclient.currentAssignmentId = devoir.getId().toString();
    //
                                                    Label openDate  = (Label) assignmentLoader.getNamespace().get("openDate");
                                                    openDate.setText(Moodleclient.dateFormat.format(devoir.getCreatedAt()));
//                                                    JFXButton btnAddSub  = (JFXButton) assignmentLoader.getNamespace().get("btnAddSub");
    //
                                                    Label dueDate  = (Label) assignmentLoader.getNamespace().get("dueDate");
                                                    dueDate.setText(Moodleclient.dateFormat.format(devoir.getDateLimite()));
                                                    
                                                    //Label filename = (Label) assignmentLoader.getNamespace().get("filename");
                                                    VBox vbox = (VBox) assignmentLoader.getNamespace().get("vbox");
                                                    
                                                    Set assignmentFiles = devoir.getRessourceDevoirs();
                                                    
                                                    System.out.println("Nombres de fchiers du devoir : "+assignmentFiles.size());
                                                    for(Object file : assignmentFiles){
                                                        
                                                        RessourceDevoir courseFile = (RessourceDevoir) file;
                                                        
                                                        // debut vbox
                                                        HBox hb = new HBox();
                                                        hb.setPadding(new Insets(20,0,0,0));
                                                        hb.setSpacing(10);
                                                        hb.setAlignment(Pos.CENTER);
                                                        
                                                        ImageView image1 = new ImageView();
                                                        image1.setFitWidth(49);
                                                        image1.setFitHeight(33);
                                                        image1.setPreserveRatio(true);
                                                        image1.setImage(new Image("/images/icons8_folder_24px.png"));
                                                        
                                                        Label filename1 = new Label("filename1");
                                                        filename1.setTextFill(Color.BLUE);
                                                        filename1.setPrefSize(370, 25);
                                                        filename1.setFont(new Font("System",14));
                                                        filename1.setCursor(Cursor.HAND);
                                                        
                                                        hb.getChildren().addAll(image1, filename1);
                                                        
                                                        vbox.getChildren().add(hb);
                                                        // fin vbox
                                                        
                                                        //filename.setText(courseFile.getFileName());
                                                        filename1.setText(courseFile.getFileName());
                                                        
                                                        filename1.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                                            @Override
                                                            public void handle(MouseEvent event) {
                                                                try {
                                                                    System.out.println("Vous venez de cliquer sur le fichier : \n"+assignmentFiles);
                                                                    // on va maintenant mettre au centre un tableau qui contient toutes les soumissions
                                                                    FileLauncher fileLauncher = new Moodleclient.FileLauncher(courseFile.getHashName());
                                                                    fileLauncher.start();

                                                                }catch(Exception ex){
                                                                    System.out.println("Exception : "+ex.getMessage());
                                                                }
                                                            }
                                                        });
                                                        
                                                    }
                                                    
                                                                                     

                                                    GridPane gridpane = (GridPane) assignmentLoader.getNamespace().get("gridpane");
    //
                                                    Moodleclient.session = HibernateUtil.getSessionFactory().openSession();
    //
                                                    //load and set the files of the assignment
//                                                    Set assignmentFiles = devoir.getRessourceDevoirs();
    //
    //                                                int k = 3;
    //
    //                                                for(Object file : assignmentFiles){
    //
    //                                                    RessourceDevoir courseFile = (RessourceDevoir) file;
    //
    //                                                    //create the component to display the file
    //                                                    FXMLLoader fileLoader = new FXMLLoader(getClass().getResource("/SCourse/documentButton.fxml"));
    //                                                    Pane fileButton = (Pane) fileLoader.load();
    //
    //                                                    //disable the delete button
    //                                                    ImageView deleteImg = (ImageView) fileLoader.getNamespace().get("deleteBtn");
    //
    //                                                    deleteImg.setDisable(true);
    //                                                    deleteImg.setVisible(false);
    //
    //                                                    fileButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
    //                                                        @Override
    //                                                        public void handle(MouseEvent event) {
    //
    //                                                            // lorsqu'on clique sur un devoir (when we clic on an assignemnt)
    //                                                            FileLauncher fileLauncher = new FileLauncher(courseFile.getHashName());
    //                                                            fileLauncher.start();
    //                                                        }
    //                                                    });
    //
    //                                                    Label fileName = (Label) fileLoader.getNamespace().get("fileName");
    //
    //                                                    BorderPane t = new BorderPane();
    //
    //                                                    fileName.setText(courseFile.getFileName());
    //
    //                                                    gridpane.add(fileButton, 0, k , 1, 1);
    //
    //                                                    k++;
    //                                                }
    //
                                                    //change the center of the root scene
                                                    moodleclient.Moodleclient.root.setCenter(anchorPane);
    //
                                                } catch (Exception ex) {
    
                                                    ex.printStackTrace();
                                                }
                                            }
                                        });

                                        Label assignmentName = (Label) loader.getNamespace().get("courseAssignmentName");
                                        Label assignMentDesc = (Label) loader.getNamespace().get("assignmentDesc");
                                        Button editAssignment = (Button) loader.getNamespace().get("editAssignment");
                                        Button deleteAssignment = (Button) loader.getNamespace().get("deleteAssignment");
                                        Button viewAllSubmissions = (Button) loader.getNamespace().get("viewAllSubmissions");

                                        assignmentName.setText(devoir.getCours().getNom() + "/" + devoir.getEnonce());
                                        assignMentDesc.setText("due date: " + Moodleclient.dateFormat.format(devoir.getDateLimite()));
                                        
                                        // La logique des boutons de consultation et de modification des devoirs 
                                        editAssignment.setOnMouseClicked(new EventHandler<MouseEvent>() {
        
                                            @Override
                                            public void handle(MouseEvent event) {

                                                try {
                                                    System.out.println("On a cliqué sur Edit Assignment / " + devoir.getEnonce()+" / "+devoir.getRemoteId());
                                                    // on met la logique ici

                                                }catch(Exception ex){

                                                }
                                            }
                                        });
                                        
                                        deleteAssignment.setOnMouseClicked(new EventHandler<MouseEvent>() {
        
                                            @Override
                                            public void handle(MouseEvent event) {

                                                try {
                                                    System.out.println("On a cliqué sur delete Assignment / " + devoir.getEnonce());
                                                    // DeleteCourse.delete(devoir.getId());
                                                    // on met la logique ici

                                                }catch(Exception ex){

                                                }
                                            }
                                        });
                                        
                                        viewAllSubmissions.setOnMouseClicked(new EventHandler<MouseEvent>() {
        
                                            @Override
                                            public void handle(MouseEvent event) {

                                                try {
                                                    System.out.println("On a cliqué sur View All submissions / " + devoir.getId());
                                                    // on met la logique ici
                                                    System.out.println("On teste la requete de reccupération des soumissions !");
                                                    // on va maintenant mettre au centre un tableau qui contient toutes les soumissions

                                                    // on recupere la liste des soumissions pour le devoir
       
                                                    List submissions =  Moodleclient.session.createQuery("from AssignmentSubmission sub where sub.devoirs='"+devoir.getId()+"'").list();

                                                    System.out.println("submissions size : "+submissions.size());
                                                    
   
                                                    FXMLLoader viewSubmissionLoader = new FXMLLoader(getClass().getResource("/TGradingAssignment/grading_1.fxml"));

                                                    ScrollPane mypane = new ScrollPane();

                                                    AnchorPane content = viewSubmissionLoader.load();
                                                    // on met toute la logique de viewAssignment ici
                                                    Label courseName  = (Label) viewSubmissionLoader.getNamespace().get("courseName");
                                                    courseName.setText(devoir.getCours().getNom());

                                                    Label assignmentId  = (Label) viewSubmissionLoader.getNamespace().get("assignmentId");
                                                    assignmentId.setText(devoir.getRemoteId());

                                                    Label assignmentName  = (Label) viewSubmissionLoader.getNamespace().get("assignmentName");
                                                    assignmentName.setText("Submissions of "+devoir.getEnonce());

                                                    GridPane gridpane = (GridPane) viewSubmissionLoader.getNamespace().get("gridpane"); // contient toutes les soumissions
                                                    RowConstraints rc1 = new RowConstraints();
                                                    rc1.setMinHeight(50);
                                                    rc1.setPrefHeight(80);
                                                    gridpane.getRowConstraints().clear(); // suppressison de toutes les contraintes de ligne
                                                    gridpane.getRowConstraints().add(0, rc1);
                                                    
                                                    //gridpane.setGridLinesVisible(true);
                                                    
                                                    int nblignes = gridpane.getRowConstraints().size();
                                                    int nbcolonnes = gridpane.getColumnConstraints().size();
                                                    System.out.println("Taille : ("+nblignes+", "+nbcolonnes+")");
                            
                                                    // on reccupere un élément de notre gridpane
//                                                    Node node = gridpane.getChildren().stream().filter(e->GridPane.getRowIndex(e) == 0 && GridPane.getColumnIndex(e) == 0)
//                                                            .findFirst()
//                                                            .orElse(null);

//                                                    System.out.println("Element (1, 3) : "+node);

                                                    int position = nblignes; 
                                                    int i = 0;
                                                    int k = 0;
                                                    for (k = 0;k<submissions.size();k++){
                                                        // pour chaque soumission, on va afficher les elements que nous mettrons dans notre assignmentSubmissionCard
                                                        
                                                        AssignmentSubmission ass = (AssignmentSubmission) submissions.get(i);
                                                        System.out.println("filename : "+ass.getFileName()+", devoirId = "+ass.getDevoirs().getId());

                                                        // on créé le bouton
                                                        JFXCheckBox checkbox = new JFXCheckBox();
                                                        checkbox.setCheckedColor(new Color(0.058823529f, 0.615686274f, 0.345098039f, 1.0));
                                                        //checkbox.setUnCheckedColor(new Color(90., 90., 90., 0.8));
                                                        checkbox.setPrefSize(77., 18.);
                                                        checkbox.setAlignment(Pos.CENTER);

                                                        // on créé le imageView
                                                        ImageView image = new ImageView(); // ImageView image = new ImageView("\".\\src\\images\\icons8-male_user.png\"");
                                                        image.setFitWidth(40);
                                                        image.setFitHeight(40);
                                                        image.setImage(new Image("/images/icons8-male_user.png"));

                                                        
                                                        // on exploite le resultat
                                                        Label firstName = new Label(""); // student name
                                                        firstName.setText(ass.getFullName());

                                                        Label emailAddress = new Label(""); // email address
                                                        emailAddress.setText(ass.getEmail());

                                                        Label status = new Label("Submited for Grading"); // status
                                                        status.setStyle("-fx-background-color :  #CCEDCD;");
                                                        status.setMaxHeight(40);
                                                        status.setPrefHeight(80);
                                                        status.setWrapText(true);

                                                        // final grade
                                                        Label finalGrade = new Label("Not graded");
                                                        if(ass.getGrade() != null){
                                                            finalGrade.setText(ass.getGrade().toString());
                                                        }

                                                        // on créé le bouton
                                                        JFXButton grade = new JFXButton("Grade");
                                                        grade.setStyle("-fx-background-color :  #0F6CBF;");
                                                        grade.setTextFill(Color.WHITE);
                                                        grade.setCursor(Cursor.HAND);
                                                        grade.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                                            @Override
                                                            public void handle(MouseEvent event) {
                                                                try {
                                                                    // on note le devoir
                                                                    System.out.println("vous avec cliqué sur grade :)");
                                                                    GradeAssignment.grading(finalGrade, ass);
                                                                }catch(Exception ex){
                                                                    System.out.println(ex.getMessage());
                                                                }
                                                            }
                                                        });


                                                        // last modified
                                                        Label lastModified = new Label(ass.getUpdatedAt().toString());

                                                        
                                                        // file submission
                                                        Label fileSubmission = new Label("");
                                                        fileSubmission.setText(ass.getFileName());

                                                        fileSubmission.setTextFill(Color.BLUE);
                                                        fileSubmission.setCursor(Cursor.HAND);

                                                        fileSubmission.setOnMouseClicked(new EventHandler<MouseEvent>() {
                                                            @Override
                                                            public void handle(MouseEvent event) {
                                                                try {
                                                                    FileLauncher fileLauncher = new Moodleclient.FileLauncher(ass.getHashName());
                                                                    fileLauncher.start();
                                                                    // on ne peut pas ouvrir le fichier pour le moment car l'enseignant ne l'a pas en local sur son PC
                                                                }catch(Exception ex){
                                                                    System.out.println(ex.getMessage());
                                                                }
                                                            }
                                                        });



                                                        gridpane.addRow(position+i, checkbox, image, firstName, emailAddress, status, grade, lastModified, 
                                                                fileSubmission, finalGrade);
                                                        gridpane.getRowConstraints().add(position+i, rc1);
                                                        
                                                        i++;

                                                    }
                                                    mypane.setContent(content);
                                                    root.setCenter(mypane);
                                                    

                                                }catch(Exception ex){
                                                    System.out.println("Exception : "+ex.getMessage());
                                                }
                                            }
                                        });


                                        gridpane.add(content, 0, i, 1, 1);
                                        
                                        pos = i;
                                    }
                                }
                                
                                // Mettons le boutton de création de devoir ici
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/TAddAssignment/TAddAssignment.fxml"));
                                Pane content = (Pane)loader.load();
                                gridpane.add(content, 0, pos + 1, 1, 1);
                                
                                Button addAssignmentCourse = (Button) loader.getNamespace().get("addAssignmentCourse");
                                addAssignmentCourse.setOnMouseClicked(new EventHandler<MouseEvent>() {
//        
                                    @Override
                                    public void handle(MouseEvent event) {

                                        try {
                                            System.out.println("On a cliqué sur Add an Assignment to this course");
                                            // mettre la logique ici
                                            // on appelle la methode creatAssignment de la classe CreatAssignment
                                            CreateAssignment createAssignment = new CreateAssignment();
                                            createAssignment.CreateAssignment();
                                        }catch(Exception ex){

                                        }
                                    }
                                });
                                
                                // fin ajout du bouton 

                            } catch (IOException ex) { 

                                ex.printStackTrace();
                            }
                            
                            
                            
                            
                            
                            
                            
                            
                        }
                    });

                    Label courseFullName = (Label) loader.getNamespace().get("courseFullName");
                    Label courseShortName = (Label) loader.getNamespace().get("courseShortName");

                    courseFullName.setText(cours.getNom() );
                    courseShortName.setText(cours.getDescription());
                    
                    gridpane.add(content, 0, i, 1, 1);
                }
                
            } catch (IOException ex) { 
                
                ex.printStackTrace();
            }
            
            this.scrollpane.setContent(gridpane);
            
        }

    }
}
