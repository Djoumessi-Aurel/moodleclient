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

/**
 *
 * @author ralie
 */
public class ServerSettingsAB {
    
    public static String serverAddress = "";
    public static String PRIVILEGED_TOKEN = "";
    private static int result = 0;
    
    
    public static int display(){ //Renvoie 0 si on clique sur Cancel ou si on ferme la fenêtre
        
        result = 0;
        
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //TO BLOCK ANOTHER WINDOWS
        window.setTitle("Moodle Client - Server settings");
        window.setWidth(600); window.setMaxWidth(650);
        window.setMaxHeight(300);
        
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 50, 0, 50));
        
        HBox hb1 = new HBox();      
        hb1.setPadding(new Insets(10,0,0,0));
        hb1.setSpacing(50);
        hb1.setAlignment(Pos.CENTER_LEFT);
        
        HBox hb2 = new HBox();
        hb2.setPadding(new Insets(10,0,0,0));
        hb2.setSpacing(150);
        hb2.setAlignment(Pos.CENTER);
        
        HBox hb3 = new HBox();
        hb3.setPadding(new Insets(10,0,0,0));
        hb3.setSpacing(50);
        hb3.setAlignment(Pos.CENTER_LEFT);
        
        
        Label label = new Label("Server address: ");        
        JFXTextField textAddress = new JFXTextField(moodleclient.Moodleclient.serverAddress);
        Label label2 = new Label("Admin token: ");        
        JFXTextField textToken = new JFXTextField(moodleclient.Moodleclient.PRIVILEGED_TOKEN);
        textToken.setPrefWidth(300);
                
        JFXButton saveBtn = new JFXButton("Save");
        saveBtn.setStyle("-fx-background-color:#CDCDCD");
        JFXButton cancelBtn = new JFXButton("Cancel");
        cancelBtn.setStyle("-fx-background-color:#CDCDCD");
        
        
        ImageView img = new ImageView();
        img.setImage(new Image("/images/icons8-cloud_database_gray.png"));
        
        hb1.getChildren().addAll(label, textAddress);
        hb3.getChildren().addAll(label2, textToken);
        hb2.getChildren().addAll(cancelBtn, saveBtn);
        vbox.getChildren().addAll(img, hb1, hb3, hb2);
        
        saveBtn.setOnAction(e -> {
            serverAddress = textAddress.getText();
            PRIVILEGED_TOKEN = textToken.getText();
            result = 1;
            window.close();        
        });
        
        cancelBtn.setOnAction(e -> {
            window.close();
        });
        
        Scene scene = new Scene(vbox, 500, 200);
	window.setScene(scene);
        window.showAndWait();
        
        return result;
    }
}
