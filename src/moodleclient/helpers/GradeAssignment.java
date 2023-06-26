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
import moodleclient.entity.Devoirs;

/**
 *
 * @author STEVE LENING
 */
public class GradeAssignment {
    
    public static void grading(Label grade, int subId){
        
        //Parent parent = FXMLLoader.load(getClass().getResource("./logoutAB.fxml"));
       
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //TO BLOCK ANOTHER WINDOWS
        window.setTitle("Moodle Client - Grading");
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
   
        
        Label label = new Label("Insert grade here : ");
        label.setFont(new Font("Arial",16));
        
        Label label1 = new Label(" / 100");
        label1.setFont(new Font("Arial",16));
        
        JFXButton yesBtn = new JFXButton("Yes");
        yesBtn.setStyle("-fx-background-color: #0F6CBF");
        yesBtn.setTextFill(Color.WHITE);
        
        JFXButton cancelBtn = new JFXButton("Cancel");
        cancelBtn.setStyle("-fx-background-color:#CDCDCD");
        
        ImageView img = new ImageView();
        img.setImage(new Image("/images/icons8_pencil_48px_1.png"));
        img.setFitHeight(50);
        img.setFitWidth(50);
        img.setPreserveRatio(true);
        
        TextField input = new TextField();
        input.setPrefSize(150, 25);
        
        
        hb1.getChildren().addAll(label, input, label1);
        hb2.getChildren().addAll(cancelBtn, yesBtn);
        vbox.getChildren().addAll(img, hb1, hb2);
        
        yesBtn.setOnAction(e -> {
            // on met a jour la note ici
            System.out.println("Contenu de l'input : "+input.getText());
            
            if(!input.getText().equals("")){
                try{
                    double valeur = Double.parseDouble(input.getText());
                    if(valeur<0){
                        valeur = 0;
                    }
                    else if(valeur>100){
                        valeur = 100;
                    }
                    grade.setText(""+valeur);
                    // on modifie la note du devoir dans la BD
                    List submissions =  Moodleclient.session.createQuery("from AssignmentSubmission sub where sub.id='"+subId+"'").list();
                    AssignmentSubmission submission = (AssignmentSubmission) submissions.get(0);
                    submission.setGrade(valeur);
                    
                    // on met a jour la BD 
                    Moodleclient.session.beginTransaction();
                    Moodleclient.session.createQuery("update AssignmentSubmission set grade='"+valeur+"' where id="+subId).executeUpdate();
                    Moodleclient.session.getTransaction().commit();
                    
                }catch(Exception ex){
                    System.out.println("Exception : "+ex.getMessage());
                }
                
                window.close();
            }
            else {
                window.close();
            }
        });
        
        cancelBtn.setOnAction(e -> {
            window.close();
        
        });
        
        
        
        Scene scene = new Scene(vbox, 500, 150);
	window.setScene(scene);
        window.showAndWait();
    }
    
}
