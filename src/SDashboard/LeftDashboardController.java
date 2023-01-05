/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SDashboard;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import static moodleclient.Moodleclient.root;
import moodleclient.exceptions.NotValidSessionException;
import moodleclient.exceptions.ServerUnreachableException;
import moodleclient.helpers.PrivateFileHelper;
import org.json.simple.parser.ParseException;

/**
 * FXML Controller class
 *
 * @author ralie
 */
public class LeftDashboardController implements Initializable {

    @FXML
    private JFXButton btnDashboard;
    @FXML
    private JFXButton btnSiteHome;
    @FXML
    private JFXButton btnPrivateFiles;
    @FXML
    private JFXButton btnAssignments;
    @FXML
    private JFXButton btnCourses;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void handleButtonDashboard(ActionEvent event) throws Exception{
        
        AnchorPane content = (AnchorPane)FXMLLoader.load(getClass().getResource("/SDashboard/StudentDashboard.fxml"));
        
        root.setCenter(content);
    }

    @FXML
    private void handleButtonSHome(ActionEvent event) throws IOException {
        AnchorPane content = (AnchorPane)FXMLLoader.load(getClass().getResource("/SHome/StudentHome.fxml"));
        root.setCenter(content);
        
    }

    @FXML
    private void handleButtonSPrivaFiles(ActionEvent event) throws IOException, ParseException, NotValidSessionException, MalformedURLException, ServerUnreachableException {
        
        AnchorPane content = (AnchorPane)FXMLLoader.load(getClass().getResource("/SSavePrivateFiles/StudentSavePrivateFiles_1.fxml"));

        root.setCenter(content);
        
    }

    @FXML
    private void handleButtonSAssignment(ActionEvent event) throws IOException {

        AnchorPane content = (AnchorPane)FXMLLoader.load(getClass().getResource("/SAssignmentList/StudentAssignmentList_1.fxml"));

        root.setCenter(content);
        
    }
    
}
