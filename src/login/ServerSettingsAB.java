/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package login;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

/**
 *
 * @author ralie
 */
public class ServerSettingsAB {
    
    private static String serverAddress;
    
    
    public static String display(){
        
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //TO BLOCK ANOTHER WINDOWS
        window.setTitle("Moodle Client - Server settings");
        window.setMaxWidth(500);
        window.setMaxHeight(200);
        
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 50, 0, 50));
        
        HBox hb1 = new HBox();
        hb1.setPadding(new Insets(10,0,0,0));
      
        hb1.setAlignment(Pos.CENTER);
        hb1.setPadding(new Insets(10,0,0,0));
        hb1.setSpacing(50);
        
        HBox hb2 = new HBox();
        hb2.setPadding(new Insets(10,0,0,0));
        hb2.setSpacing(150);
        hb2.setAlignment(Pos.CENTER);
        
        
        Label label = new Label("Server address: ");        
        JFXTextField textAddress = new JFXTextField(moodleclient.Moodleclient.serverAddress);
                
        JFXButton saveBtn = new JFXButton("Save");
        saveBtn.setStyle("-fx-background-color:#CDCDCD");
        JFXButton cancelBtn = new JFXButton("Cancel");
        cancelBtn.setStyle("-fx-background-color:#CDCDCD");
        
        
        ImageView img = new ImageView();
        img.setImage(new Image("/images/icons8-cloud_database_gray.png"));
        
        hb1.getChildren().addAll(label, textAddress);
        hb2.getChildren().addAll(cancelBtn, saveBtn);
        vbox.getChildren().addAll(img, hb1, hb2);
        
        saveBtn.setOnAction(e -> {
            serverAddress = textAddress.getText();
            window.close();
        
        });
        
        cancelBtn.setOnAction(e -> {
            window.close();
        
        });
        
        Scene scene = new Scene(vbox, 500, 200);
	window.setScene(scene);
        window.showAndWait();
        
        return serverAddress;
    }
}
