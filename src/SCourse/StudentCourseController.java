package SCourse;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import login.Dry;
import moodleclient.Moodleclient;
import static moodleclient.Moodleclient.root;
import moodleclient.entity.Cours;
import moodleclient.entity.CourseFile;
import moodleclient.entity.Sections;
import moodleclient.helpers.CommandRunner;
import moodleclient.helpers.DialogCreator;
import moodleclient.util.HibernateUtil;

public class StudentCourseController implements Initializable{
	
    
    @FXML
    private Label courseName;
    @FXML
    private Label labelModifyCourse;
    @FXML
    private Label labelDeleteCourse;

    @FXML
    private ScrollPane scrollpane;

    
	private void handleButtonDashboard(ActionEvent event) throws Exception{
		Parent parent = FXMLLoader.load(getClass().getResource("/SDashboard/StudentDashboard.fxml"));
		Scene scene = new Scene(parent);
		Stage app_stage;
		app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		app_stage.setScene(scene);
		app_stage.show();
	}
    
    
	private void handleButtonSPrivaFiles(ActionEvent event) throws Exception{
		Parent parent = FXMLLoader.load(getClass().getResource("/SSavePrivateFiles/StudentSavePrivateFiles.fxml"));
		Scene scene = new Scene(parent);
		Stage app_stage;
		app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		app_stage.setScene(scene);
		app_stage.show();
	}
    
   	private void handleButtonSHome(ActionEvent event) throws Exception{
   		Parent parent = FXMLLoader.load(getClass().getResource("/SHome/StudentHome.fxml"));
   		Scene scene = new Scene(parent);
   		Stage app_stage;
   		app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
   		app_stage.setScene(scene);
   		app_stage.show();
   	}
    
	private void handleButtonSAssignment(ActionEvent event) throws Exception{
		Parent parent = FXMLLoader.load(getClass().getResource("/SAssignment/StudentAssignment.fxml"));
		Scene scene = new Scene(parent);
		Stage app_stage;
		app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
		app_stage.setScene(scene);
		app_stage.show();
	}
    
    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
        if(Moodleclient.user.isStudent()){
            labelModifyCourse.setDisable(true); labelDeleteCourse.setDisable(true);
            labelModifyCourse.setVisible(false); labelDeleteCourse.setVisible(false);
        }
        
        if(Integer.valueOf(Moodleclient.dashboardCourse.getRemoteId()) > 0){ //Si le cours existe déjà sur le serveur, on enlève le bouton "Delete"
            labelDeleteCourse.setDisable(true); //labelModifyCourse.setDisable(true);
            labelDeleteCourse.setVisible(false); //labelModifyCourse.setVisible(false);
        }

    }
    
    @FXML
    public void handleModifyCourse(){
        //System.out.println("MODIFICATION DU COURS " + Moodleclient.dashboardCourse.getNom());
        Optional<Cours> opCourse = DialogCreator.launchCourseDialog("modify", Moodleclient.dashboardCourse);
        
        if(opCourse.isPresent()){
            
            Moodleclient.dashboardCourse.setNom(opCourse.get().getNom());
            Moodleclient.dashboardCourse.setNomAbrege(opCourse.get().getNomAbrege());
            Moodleclient.dashboardCourse.setDescription(opCourse.get().getDescription());
            Moodleclient.dashboardCourse.setSynced((byte)0);
       
            Moodleclient.session.beginTransaction();
            Moodleclient.session.save(Moodleclient.dashboardCourse); //Save(update) the course to database
            Moodleclient.session.getTransaction().commit();
            Moodleclient.session.close();
        
            Moodleclient.session = HibernateUtil.getSessionFactory().openSession();
            
            Moodleclient.courses =  Moodleclient.session.createQuery("from Cours").list();
            
            courseName.setText(Moodleclient.dashboardCourse.getNom());
        }
    }
    
    @FXML
    public void handleDeleteCourse() throws IOException{
        
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete a course");
        alert.setHeaderText("This will delete the course \"" + Moodleclient.dashboardCourse.getNom() + "\" and all its content. \nThis action is irreversible.");
        alert.setContentText("Are you ok with that?");

        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK){
            Moodleclient.session.beginTransaction();
            
            for(Object section : Moodleclient.dashboardCourse.getSectionses()){
                
                for(Object cFile: ((Sections)section).getCourseFiles()){
                    //On supprime les fichiers associés à cette section
                    new CommandRunner("del \".\\files\\" + ((CourseFile)cFile).getHashName() + "\"").start();
                    
                    //On supprime le courseFile en BD
                    Moodleclient.session.delete(cFile);
                }
                //On supprime la section en BD
                Moodleclient.session.delete(section);
            }
            //On supprime le cours en BD
            Moodleclient.session.delete(Moodleclient.dashboardCourse); //Delete the course from the database
            
            Moodleclient.session.getTransaction().commit();
            Moodleclient.session.close();
        
            Moodleclient.session = HibernateUtil.getSessionFactory().openSession();
            Moodleclient.courses =  Moodleclient.session.createQuery("from Cours").list();
            
            Moodleclient.dashboardCourse = null;
            new Dry().showDashboard(root); //Display the dashboard page
        }
            
    }

}
