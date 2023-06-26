/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TGradingAssignment;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;


/**
 * FXML Controller class
 *
 * @author ralie
 */
public class GradingController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Label courseName;

    @FXML
    private JFXButton settingsBtn;
    
    @FXML
    private Label assignmentName;
    
    @FXML
    private Label assignmentId;
    
    @FXML
    private GridPane gridpane;
    // c'est ce grid pane qui contient toutes les soumissions
    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
