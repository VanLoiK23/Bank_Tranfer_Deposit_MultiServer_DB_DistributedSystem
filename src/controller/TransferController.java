package controller;

import java.net.URL;
import java.util.ResourceBundle;

import controller.Common.CommonController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import model.User;

public class TransferController implements Initializable{

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
    
    private CommonController commonController=new CommonController();
    
    private User user;
    
    public void setUser(User user) {
    	this.user=user;
    	
    	myAccountNumber.setText(user.getAccountNumber());
    	String formattedCurrency=commonController.formatCurrency(user.getBalance());
    	myBanlance.setText(formattedCurrency);
    	
    	lblBalance.setText("Số dư :"+formattedCurrency);
    }

    @FXML
    void onTransferClicked(ActionEvent event) {

    }

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
	}

}
