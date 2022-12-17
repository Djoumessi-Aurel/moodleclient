package SCourse;

import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class StudentCourseController implements Initializable{
	
    
    @FXML
    private Label courseName;

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
		
	}

}
