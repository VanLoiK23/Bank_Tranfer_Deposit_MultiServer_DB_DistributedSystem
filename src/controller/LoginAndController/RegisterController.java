package controller.LoginAndController;

import java.io.IOException;
import java.rmi.RemoteException;

import controller.Common.CommonController;
import controller.rmi.IUser;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.User;
import service.UserService;
import util.PasswordUtil;

public class RegisterController {

	@FXML
	private TextField accountNumber;

	@FXML
	private Button button;

	@FXML
	private VBox containerFIleChoose;

	@FXML
	private ImageView img;

	@FXML
	private PasswordField password;

	@FXML
	private ProgressIndicator progress;

	@FXML
	private PasswordField retype;

	@FXML
	private TextField userName;

	private UserService userService = new UserService();

	private CommonController commonController = new CommonController();

	@FXML
	void create(MouseEvent event) throws IOException {
		if (accountNumber.getText() != null && !accountNumber.getText().isEmpty() && userName.getText() != null
				&& !userName.getText().isEmpty() && password.getText() != null && !password.getText().isEmpty()
				&& retype.getText() != null && !retype.getText().isEmpty()) {

			if (!commonController.isValidNumber(accountNumber.getText())) {
				commonController.alertInfo(Alert.AlertType.WARNING, "Lỗi!", "Số tài khoản không đúng định dạng!");
			} else if (accountNumber.getText().length() != 6) {
				commonController.alertInfo(Alert.AlertType.WARNING, "Lỗi!", "Số tài khoản phải có 6 chữ số!!!");
			}
			// check account number exist and user name
			else if (userService.existsById(accountNumber.getText())) {
				commonController.alertInfo(Alert.AlertType.WARNING, "Lỗi!", "Số tài khoản đã tồn tại!!!");
			} else if (userService.existsByUsername(userName.getText())) {
				commonController.alertInfo(Alert.AlertType.WARNING, "Lỗi!", "User name đã tồn tại!!!");
			} else if (!password.getText().equals(retype.getText())) {
				commonController.alertInfo(Alert.AlertType.WARNING, "Lỗi!", "Password không trùng nhau!");
			} else if (!password.getText().equals(retype.getText())) {
				commonController.alertInfo(Alert.AlertType.WARNING, "Lỗi!", "Password không trùng nhau!");
			} else if (password.getText().length() < 6) {
				commonController.alertInfo(Alert.AlertType.WARNING, "Lỗi!", "Password của bạn quá ngắn!");
			} else {
				// TODO: Add new User
				handleCreateAccountAsync(event);
			}
		} else {
			commonController.alertInfo(Alert.AlertType.WARNING, "Cảnh báo!", "Vui lòng nhập đầy đủ thông tin!");
		}
	}

	private void handleCreateAccountAsync(MouseEvent event) {
		progress.setPrefSize(40, 40);
		progress.setVisible(true);
		button.setText(null);
		button.setDisable(true);
		button.setOpacity(0.6);

		Task<Void> task = new Task<>() {
			@Override
			protected Void call() {
				try {
					// Tạo user object
					User user = new User();

					user.setAccountNumber(accountNumber.getText());
					user.setPassword(PasswordUtil.hashPassword(password.getText()));
					user.setBalance(0.0);
					user.setUserName(userName.getText());

					IUser userRmi = (IUser) java.rmi.Naming.lookup("rmi://192.168.1.243:2020/BankA");

					Platform.runLater(() -> {
						setDefault();

						try {
							if (userRmi.insertUser(user)) {
								FadeTransition fade = new FadeTransition(Duration.millis(300), button);
								fade.setFromValue(0);
								fade.setToValue(1);
								fade.play();

								commonController.alertInfo(Alert.AlertType.CONFIRMATION, "Thành công!",
										"Bạn đã tạo tài khoản thành công!");

								try {
									commonController.loaderToResource(event, "LoginAndRegister/login");
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								clean();

							} else {
								Platform.runLater(() -> {
									setDefault();
									commonController.alertInfo(AlertType.WARNING, "Không thể tạo tài khoản!!!!",
											"Fail");
								});
							}
						} catch (RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					});

				} catch (Exception ex) {
					Platform.runLater(() -> {
						setDefault();

						commonController.alertInfo(Alert.AlertType.ERROR, "Lỗi hệ thống!",
								"Đã xảy ra lỗi không mong muốn.");
					});
					ex.printStackTrace();
				}
				return null;
			}
		};

		new Thread(task).start();
	}

	private void setDefault() {
		progress.setVisible(false);
		button.setText("Create a account");
		button.setOpacity(1);
		button.setDisable(false);
	}

	private void clean() {
		accountNumber.setText(null);
		userName.setText(null);
		password.setText(null);
		retype.setText(null);
	}

	@FXML
	void openLogin(MouseEvent event) throws IOException {
		commonController.loaderToResource(event, "LoginAndRegister/login");
	}

	@FXML
	public void exit(MouseEvent event) {
		Stage stage = (Stage) img.getScene().getWindow();
		stage.close();
	}
}
