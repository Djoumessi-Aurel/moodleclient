package TAssignment;

import SAssignment.*;
import SSubmitAssignment.SubmitAssignmentController;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import static moodleclient.Moodleclient.root;

public class TeacherAssignmentController implements Initializable {
	
    
    @FXML
    private Label topicName;

//    @FXML
//    private Label filename;
    
    @FXML 
    private VBox vbox;
    
    @FXML
    private GridPane gridpane;
    
    @FXML
    private Label openDate;
    
    @FXML
    private Label dueDate;
    
    @FXML
    private Label gradeLabel;
    
    @FXML
    private Label devoirId;
    


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        // TODO Auto-generated method stub
//           devoirId.setVisible(false);
//           gradeLabel.setText("");
    }

    @FXML
    public void handleAddSubmission() throws IOException {
        
        // voici l'endroit ou il faut mettre la logique du boutton "Grade"

    }
    
}
