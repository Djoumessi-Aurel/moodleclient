/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.helpers;

import com.jfoenix.controls.JFXButton;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import moodleclient.Moodleclient;
import moodleclient.entity.AssignmentSubmission;
import org.hibernate.Query;

/**
 *
 * @author STEVE LENING
 */
public class DeleteCourse {
    public static void delete(Integer devId){
        
        //Parent parent = FXMLLoader.load(getClass().getResource("./logoutAB.fxml"));
       
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //TO BLOCK ANOTHER WINDOWS
        window.setTitle("Moodle Client - Delete Assignment");
        window.setMaxWidth(500);
        window.setMaxHeight(300);
        window.setMinHeight(210);
        window.setMinWidth(500);
        
        
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10, 50, 0, 50));
        
        HBox hb1 = new HBox();
        hb1.setPadding(new Insets(10,0,0,0));
      
        hb1.setAlignment(Pos.CENTER);
        
        HBox hb2 = new HBox();
        hb2.setPadding(new Insets(20,0,0,0));
        hb2.setSpacing(150);
        hb2.setAlignment(Pos.CENTER);
   
        
        Label label = new Label("Are you sure you want to delete ?");
        label.setFont(new Font("Arial",16));
        
        
        JFXButton yesBtn = new JFXButton("Yes");
        yesBtn.setStyle("-fx-background-color: #0F6CBF");
        yesBtn.setTextFill(Color.WHITE);
        
        JFXButton cancelBtn = new JFXButton("Cancel");
        cancelBtn.setStyle("-fx-background-color:#CDCDCD");
        
        ImageView img = new ImageView();
        img.setImage(new Image("/images/delete.png"));
        img.setFitHeight(50);
        img.setFitWidth(50);
        img.setPreserveRatio(true);
        
        
        hb1.getChildren().addAll(label);
        hb2.getChildren().addAll(cancelBtn, yesBtn);
        vbox.getChildren().addAll(img, hb1, hb2);
        
        yesBtn.setOnAction(e -> {
            System.out.println("\nTrying to delete assignment number : "+devId);
            try{
                // on met a jour la BD 
                Moodleclient.session.beginTransaction();
                
                Query q = Moodleclient.session.createQuery("delete from AssignmentSubmission sub where sub.devoirs = :devoirId");
                q.setParameter("devoirId", devId);
                q.executeUpdate();
                
                Query q1 = Moodleclient.session.createQuery("delete from RessourceDevoir res where res.devoirs = :devoirId");
                q1.setParameter("devoirId", devId);
                q1.executeUpdate();
                
                Query q2 = Moodleclient.session.createQuery("delete from Devoirs dev where dev.id = :devoirId");
                q2.setParameter("devoirId", devId);
                q2.executeUpdate();
                
                Moodleclient.session.getTransaction().commit();
                System.out.println("\nLe devoir dont l'id est : "+devId+" a été supprimé.\n");

            }catch(Exception ex){
                System.out.println("Exception : "+ex.getMessage());
            }

            window.close();

        });
        
        cancelBtn.setOnAction(e -> {
            window.close();
        
        });
        
        
        Scene scene = new Scene(vbox, 500, 150);
	window.setScene(scene);
        window.showAndWait();
    }
}
