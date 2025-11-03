package controller.LoginAndController;

import java.io.IOException;

import controller.TransferController;
import controller.Common.CommonController;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.User;
import service.UserService;
import util.PasswordUtil;

public class LoginController {

	@FXML
	private ImageView img;

	@FXML
	private PasswordField password;

	@FXML
	private TextField accountNumber;

	@FXML
	private ChoiceBox<String> serverChoice;

	@FXML
	public void exit(MouseEvent event) {
		Stage stage = (Stage) img.getScene().getWindow();
		stage.close();
	}

	private CommonController commonController = new CommonController();

	private UserService userService = new UserService();

	@FXML
	void submit(MouseEvent event) throws IOException, InterruptedException {
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

						mainController.setUser(userResult);
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

}
