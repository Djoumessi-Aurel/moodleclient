
package moodleclient.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import static moodleclient.Moodleclient.root;

/**
 *
 * @author STEVE LENING
 */
public class CreateAssignment {
    public void CreateAssignment() {
        // on met la logique ici
        try{
            FXMLLoader fileLoader = new FXMLLoader(getClass().getResource("/TCreateAssignment/TCreateAssignment.fxml"));
            Pane content = (Pane) fileLoader.load();
            
            
            
            root.setCenter(content);
        }catch(Exception ex){
            System.out.println("Exception : "+ex.getMessage());
        }
    }
}
