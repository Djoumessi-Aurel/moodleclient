/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient;

import com.jfoenix.controls.JFXButton;
import java.util.ArrayList;
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
import static moodleclient.Moodleclient.root;
import moodleclient.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;

/**
 *
 * @author ralie
 */
public class LogOutAlertBox {
    
    //boolean wantTo = false;
    public static void display(){
        
        //Parent parent = FXMLLoader.load(getClass().getResource("./logoutAB.fxml"));
       
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //TO BLOCK ANOTHER WINDOWS
        window.setTitle("Moodle Client - Log out");
        window.setMaxWidth(500);
        window.setMaxHeight(160);
        
        
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 50, 0, 50));
        
        HBox hb1 = new HBox();
        hb1.setPadding(new Insets(10,0,0,0));
      
        hb1.setAlignment(Pos.CENTER);
        
        HBox hb2 = new HBox();
        hb2.setPadding(new Insets(10,0,0,0));
        hb2.setSpacing(150);
        hb2.setAlignment(Pos.CENTER);
   
        
        Label label = new Label("Are you sure you want to log out");
        label.setFont(new Font("Arial",16));
        
        JFXButton yesBtn = new JFXButton("Yes");
        yesBtn.setStyle("-fx-background-color:#CDCDCD");
        JFXButton cancelBtn = new JFXButton("Cancel");
        cancelBtn.setStyle("-fx-background-color:#CDCDCD");
        
        ImageView img = new ImageView();
        img.setImage(new Image("/images/icons8-puzzled.png"));
        
        hb1.getChildren().add(label);
        hb2.getChildren().addAll(cancelBtn, yesBtn);
        vbox.getChildren().addAll(img, hb1, hb2);
        
        yesBtn.setOnAction(e -> {
        logOut();
        window.close();
        
        });
        
        cancelBtn.setOnAction(e -> {
        window.close();
        
        });
        
        
        
        Scene scene = new Scene(vbox, 500, 150);
	window.setScene(scene);
        window.showAndWait();
    }
    
    //function to log out the user
    private static void logOut(){
        
        System.out.println("reached!");
       
        //delete the user's account from the local database
        Session session = HibernateUtil.getSessionFactory().openSession();
        
        session.beginTransaction();
        
        session.createQuery("delete from Users").executeUpdate();
        
        session.getTransaction().commit();
        
        session.close();
        
        //clear the current data
        moodleclient.Moodleclient.clearLocalDatabase();
        
        moodleclient.Moodleclient.courses = new ArrayList();
        moodleclient.Moodleclient.privateFiles = new ArrayList();
        moodleclient.Moodleclient.assignments = new ArrayList();
        
        //display the login page
        moodleclient.Moodleclient.root.setLeft(null);
        moodleclient.Moodleclient.root.setTop(null);
        moodleclient.Moodleclient.root.setRight(null);
        
        root.setCenter(Moodleclient.loginAnchorPane);
    }
    
}
