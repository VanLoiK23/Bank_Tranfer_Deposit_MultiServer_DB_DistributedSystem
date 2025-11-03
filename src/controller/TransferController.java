package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

public class TransferController {

    @FXML
    private Button btnProfile;

    @FXML
    private Button btnTransfer;

    @FXML
    private Label lblBalance;

    @FXML
    private Label lblTodaySpending;

    @FXML
    private Label myAccountNumber;

    @FXML
    private Label myBanlance;

    @FXML
    private ListView<Node> recentList;

    @FXML
    private TextField tfAmount;

    @FXML
    private TextField tfNote;

    @FXML
    private TextField tfTo;

    @FXML
    void onTransferClicked(ActionEvent event) {

    }

}
