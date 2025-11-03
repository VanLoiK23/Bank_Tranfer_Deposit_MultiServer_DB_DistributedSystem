package controller.LoginAndController;

import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.util.ResourceBundle;

import controller.TransferController;
import controller.Common.CommonController;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.User;
import service.UserService;
import util.PasswordUtil;

public class LoginController implements Initializable {

	@FXML
	private ImageView img;

	@FXML
	private PasswordField password;

	@FXML
	private TextField accountNumber;

	@FXML
	private  ComboBox<String> serverChoice;

	@FXML
	public void exit(MouseEvent event) {
		Stage stage = (Stage) img.getScene().getWindow();
		stage.close();
	}

	private CommonController commonController = new CommonController();

	private UserService userService = new UserService();

	@FXML
	void submit(MouseEvent event) throws IOException, InterruptedException, NotBoundException {
		if (accountNumber.getText() != null && password.getText() != null && !accountNumber.getText().isEmpty()
				&& !password.getText().isEmpty()) {
			if (!commonController.isValidNumber(accountNumber.getText())) {
				commonController.alertInfo(Alert.AlertType.WARNING, "Lỗi!", "Số tài khoản không đúng định dạng!");
			} else if (accountNumber.getText().length() != 6) {
				commonController.alertInfo(Alert.AlertType.WARNING, "Lỗi!", "Số tài khoản phải có 6 chữ số!!!");
			} else {
				User userRequest = new User();
				userRequest.setAccountNumber(accountNumber.getText());
				userRequest.setPassword(password.getText());

				User userResult = userService.getUserById(accountNumber.getText());

				if (userResult == null) {
					commonController.alertInfo(AlertType.WARNING, "Fail!!!!", "Tài khoản không tồn tại");
				} else {
					if (PasswordUtil.checkPassword(password.getText(), userResult.getPassword())) {
						commonController.alertInfo(AlertType.WARNING, "Fail!!!!", "Mật khẩu không chính xác!!!");
					} else {
						commonController.alertInfo(AlertType.CONFIRMATION, "Success!!!!",
								"Bạn đã đăng nhập thành công");

						TransferController mainController = commonController.loaderToResource(event, "bank")
								.getController();

						String[] parts = serverChoice.getValue().split(" - ");
						String url = parts.length > 1 ? parts[1].trim() : "";
						System.out.println("Server choice " + url);
						mainController.setUser(userResult, url);
					}
				}

				clean();

			}
		} else {
			commonController.alertInfo(Alert.AlertType.WARNING, "Cảnh báo!", "Vui lòng nhập đầy đủ thông tin!");
			clean();
		}
	}

	void clean() {
		accountNumber.setText(null);
		password.setText(null);
	}

	@FXML
	void openRegister(MouseEvent event) throws IOException {
		commonController.loaderToResource(event, "LoginAndRegister/Register");
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		serverChoice.setCellFactory(listView -> new javafx.scene.control.ListCell<String>() {
		    @Override
		    protected void updateItem(String item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty || item == null) {
		            setText(null);
		        } else {
		            setText(item);
		            setTextFill(javafx.scene.paint.Color.WHITE); // text màu trắng
		            setStyle("-fx-background-color: #151928;"); // nền tối cho item
		        }
		    }
		});
		serverChoice.setButtonCell(serverChoice.getCellFactory().call(null));
		
		serverChoice.setItems(FXCollections.observableArrayList("Server A - 192.168.1.247:2020/BankA",
				"Server B - 192.168.1.122:2025/BankB"));

		// default
		serverChoice.setValue("Server A - 192.168.1.247:2020/BankA");
	}

}
