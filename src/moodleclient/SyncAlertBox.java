/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.sun.java.swing.plaf.windows.resources.windows;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
//import java.io.IOException;
//import javafx.fxml.FXMLLoader;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.geometry.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import static moodleclient.Moodleclient.autoSync;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
/**
 *
 * @author ralie
 */
public class SyncAlertBox {
    
    private static boolean oldValueManual /*Value of the cache*/;
    
    public static boolean display() throws IOException, FileNotFoundException, ParseException{
        
        //Parent parent = FXMLLoader.load(getClass().getResource("./logoutAB.fxml"));
        
        //load the oldvalueManual
        JSONObject jo = moodleclient.Moodleclient.loadConfiguraton();
        
        if(jo.get("autoSync").toString().equalsIgnoreCase("true")){
            oldValueManual = false;
        }else{
            oldValueManual = true;
        }
       
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //TO BLOCK ANOTHER WINDOWS
        window.setTitle("Moodle Client - Sync");
        window.setMaxWidth(500);
        window.setMaxHeight(200);
        
        
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 50, 0, 50));
        
        HBox hb1 = new HBox();
        hb1.setPadding(new Insets(10,0,0,0));
        hb1.setAlignment(Pos.CENTER);
        
        HBox hb2 = new HBox();
        hb2.setPadding(new Insets(10,0,10,0));
        hb2.setSpacing(150);
        hb2.setAlignment(Pos.CENTER);
        
        HBox hb3 = new HBox();
        hb3.setPadding(new Insets(10,0,0,0));
        hb3.setAlignment(Pos.CENTER);
        
        Region reg = new Region();
        reg.setPrefSize(Control.USE_COMPUTED_SIZE,Control.USE_COMPUTED_SIZE);
        HBox.setHgrow(reg,Priority.ALWAYS);
        
        Label label = new Label("Method of synchronization");
        label.setFont(new Font("Arial",16));
        
        JFXButton saveBtn = new JFXButton("Save");
        saveBtn.setStyle("-fx-background-color:#CDCDCD");
        saveBtn.setCursor(Cursor.HAND);
        
        JFXButton cancelBtn = new JFXButton("Cancel");
        cancelBtn.setStyle("-fx-background-color:#CDCDCD");
        cancelBtn.setCursor(Cursor.HAND);
        
        ToggleGroup syncGroup = new ToggleGroup();
        
        JFXRadioButton manualRadio = new JFXRadioButton("Manually");
        manualRadio.setSelected(oldValueManual);
        manualRadio.setToggleGroup(syncGroup);
        
        JFXRadioButton autoRadio = new JFXRadioButton("Automatically");
        autoRadio.setToggleGroup(syncGroup);
        autoRadio.setSelected(!oldValueManual);
        
        ImageView img = new ImageView();
        img.setImage(new Image("/images/icons8-available_updates.png"));
        
        hb1.getChildren().add(label);
        hb2.getChildren().addAll(manualRadio, autoRadio);
        hb3.getChildren().add(saveBtn);
        vbox.getChildren().addAll(img, hb1, hb2, hb3);
        
        
        
        saveBtn.setOnAction(e -> {
            if(manualRadio.isSelected()){
                //sync is manual
                moodleclient.Moodleclient.autoSync = false;
                
                try {
                    JSONObject jo2 = Moodleclient.loadConfiguraton();
                    
                    jo2.put("autoSync", "false");
                    
                    moodleclient.Moodleclient.saveConfiguration(jo2);
                    
                } catch (FileNotFoundException ex) {
                    
                    ex.printStackTrace();
                    
                } catch (IOException ex) {
                    
                    ex.printStackTrace();
                } catch (ParseException ex) {
                    
                    ex.printStackTrace();
                }
                
            } else {
                //SYnc is automatic
                moodleclient.Moodleclient.autoSync = true;
               
                try {
                    
                    JSONObject jo2 = Moodleclient.loadConfiguraton();
                    
                    jo2.put("autoSync", "true");
                    
                    moodleclient.Moodleclient.saveConfiguration(jo2);
                    
                } catch (FileNotFoundException ex) {
                    
                    ex.printStackTrace();
                    
                } catch (IOException ex) {
                    
                    ex.printStackTrace();
                    
                } catch (ParseException ex) {
                    
                    ex.printStackTrace();
                    
                }
                
            }
            window.close();
        });
        
        cancelBtn.setOnAction(e ->{
            window.close();
        });
        
        Scene scene = new Scene(vbox, 500, 200);
	window.setScene(scene);
        window.showAndWait();
        
        return moodleclient.Moodleclient.autoSync;
    }
}
