/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 *
 * @author ralie
 */
public class Dry {
    public Dry(){
        
    }
    
    public void showDashboard(BorderPane root) throws IOException{
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/SDashboard/topDashboard.fxml"));
        
        AnchorPane topMenu =  (AnchorPane)loader.load();
        
        Label username = (Label) loader.getNamespace().get("username");
        username.setText(moodleclient.Moodleclient.user.getUsername());
        
        root.setTop(topMenu);
        
       
        AnchorPane leftMenu =  (AnchorPane)FXMLLoader.load(getClass().getResource("/SDashboard/leftDashboard.fxml"));
        root.setLeft(leftMenu);
        
        AnchorPane rightMenu =  (AnchorPane)FXMLLoader.load(getClass().getResource("/SDashboard/rightDashboard.fxml"));
        root.setRight(rightMenu);
        
        //Le leftMenu se charge de mettre à jour le contenu central donc, plus besoin de ceci.
        //AnchorPane content =  (AnchorPane)FXMLLoader.load(getClass().getResource("/SDashboard/StudentDashboard.fxml"));
        //root.setCenter(content);
    }

}
