/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SCourse;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;
import moodleclient.Moodleclient;
import static moodleclient.Moodleclient.root;

/**
 * FXML Controller class
 *
 * @author pepi
 */
public class CourseSectionController implements Initializable {

    @FXML
    private Label sectionName;
    @FXML
    private Label labelGererRessources;
    @FXML
    private BorderPane filesBorderPane;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        if(Moodleclient.user.isStudent()){
            labelGererRessources.setDisable(true);
            labelGererRessources.setVisible(false);
        }
    }
    
    @FXML
    public void handleGererRessources() throws IOException{
        System.out.println("Vous avez cliqué sur " + Moodleclient.user);
        FXMLLoader courseFilesLoader = new FXMLLoader(getClass().getResource("/SCourse/ManageCourseFiles.fxml"));
        
        AnchorPane content = courseFilesLoader.load();
        
        Label title  = (Label) courseFilesLoader.getNamespace().get("title");
        title.setText(Moodleclient.dashboardCourse.getNom() + "/" + Moodleclient.dashboardSection.getNom());
        
        root.setCenter(content);
    }
    
}
