/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moodleclient.helpers;

import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import moodleclient.entity.Cours;

/**
 *
 * @author DELL
 */
public class DialogCreator {
    
    //Lance la boîte de dialogue de création/modification de cours
    public static Optional<Cours> launchCourseDialog(String mode, Cours course){ //mode = "create" ou "modify"
        
        String title, buttonText, headerText;
        
        if(mode.equalsIgnoreCase("create")){
            title = "New course";
            headerText = "Create a new course";
            buttonText = "Create";
        }
        else{
            title = "Update course";
            headerText = "Modify a course";
            buttonText = "Update";
        }
                // Create the custom dialog.
        Dialog<Cours> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);

        // Set the icon (must be included in the project).
        //dialog.setGraphic(new ImageView(this.getClass().getResource("login.png").toString()));

        // Set the button types.
        ButtonType okButtonType = new ButtonType(buttonText, ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Course name");
        TextField shortName = new TextField();
        shortName.setPromptText("Course short name");
        TextField description = new TextField();
        description.setPromptText("Course description");
        
        if(!mode.equalsIgnoreCase("create")){
            name.setText(course.getNom());
            shortName.setText(course.getNomAbrege());
            description.setText(course.getDescription());
        }

        grid.add(new Label("Name: "), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Short name: "), 0, 1);
        grid.add(shortName, 1, 1);
        grid.add(new Label("Description: "), 0, 2);
        grid.add(description, 1, 2);

        // Enable/Disable ok button depending on whether a course name was entered.
        Node okButton = dialog.getDialogPane().lookupButton(okButtonType);
        if(mode.equalsIgnoreCase("create")) okButton.setDisable(true);

        // Do some validation (using the Java 8 lambda syntax).
        name.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Request focus on the username field by default.
        Platform.runLater(() -> name.requestFocus());

        // Convert the result to a username-password-pair when the ok button is clicked.
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                return new Cours(name.getText().trim(), shortName.getText().trim(), description.getText());
            }
            return null;
        });

        Optional<Cours> result = dialog.showAndWait();
        
        return result;
    }
    
}
