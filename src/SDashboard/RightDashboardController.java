/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SDashboard;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import moodleclient.Moodleclient;
import moodleclient.entity.Devoirs;

/**
 * FXML Controller class
 *
 * @author ralie
 */
public class RightDashboardController implements Initializable {
    
    @FXML
    private VBox devoirsRemettreBox;
    
    @FXML
    private VBox devoirsNotesBox;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        try {
        //Les devoirs à remettre
        List devoirsRemettre = Moodleclient.session.createQuery("from Devoirs D WHERE D.etat='due' AND D.ignored=0 ORDER BY D.dateLimite").list();
        
        for(int i=0; i<devoirsRemettre.size(); i++){
            Devoirs devoir = (Devoirs) devoirsRemettre.get(i);
            
                FXMLLoader devoirRemettreLoader = new FXMLLoader(getClass().getResource("/SDashboard/panelDevoirRemettre.fxml"));
                Pane panelDevoir = (Pane) devoirRemettreLoader.load();
                
                Label dateRemise  = (Label) devoirRemettreLoader.getNamespace().get("dateRemise");
                Label titreDevoir  = (Label) devoirRemettreLoader.getNamespace().get("titreDevoir");
                Label matiere  = (Label) devoirRemettreLoader.getNamespace().get("matiere");
                
                dateRemise.setText(Moodleclient.dateFormat.format(devoir.getDateLimite()));
                if(devoir.getDateLimite().compareTo(new Date()) <= 0) dateRemise.setTextFill(Color.MAROON);
                titreDevoir.setText(devoir.getEnonce());
                matiere.setText(devoir.getCours().getNom());
                
                devoirsRemettreBox.getChildren().add(panelDevoir);
        }
        
        //Les devoirs notés
        List devoirsNotes = Moodleclient.session.createQuery("from Devoirs D WHERE D.note IS NOT NULL AND D.noteVue=0 ORDER BY D.dateLimite DESC").setMaxResults(5).list();
        
        for(int i=0; i<devoirsNotes.size(); i++){
            Devoirs devoir = (Devoirs) devoirsNotes.get(i);
            
                FXMLLoader devoirNoteLoader = new FXMLLoader(getClass().getResource("/SDashboard/panelDevoirNote.fxml"));
                Pane panelDevoir = (Pane) devoirNoteLoader.load();
                
                Label note  = (Label) devoirNoteLoader.getNamespace().get("note");
                Label titreDevoir  = (Label) devoirNoteLoader.getNamespace().get("titreDevoir");
                Label matiere  = (Label) devoirNoteLoader.getNamespace().get("matiere");
                
                note.setText(devoir.getNote() + "/" + devoir.getNoteMax());
                titreDevoir.setText(devoir.getEnonce());
                matiere.setText(devoir.getCours().getNom());
                
                devoirsNotesBox.getChildren().add(panelDevoir);
        }
        
        } catch (IOException ex) {
                Logger.getLogger(RightDashboardController.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
}
