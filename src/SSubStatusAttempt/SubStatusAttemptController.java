package SSubStatusAttempt;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class SubStatusAttemptController {
	

    @FXML
    private Label subStatus;

    @FXML
    private Label dueDate;

    @FXML
    private Label gradingStatus;

    @FXML
    private Label time;

    @FXML
    private ListView<?> fileSubmitted;

    @FXML
    private JFXButton btnEditSub;

    @FXML
    private JFXButton btnRemoveSub;

}
