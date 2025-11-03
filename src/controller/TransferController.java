package controller;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import controller.Common.CommonController;
import controller.rmi.IBank;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import model.HistoryTransaction;
import model.User;
import service.HistoryService;
import service.UserService;

public class TransferController implements Initializable {

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
	private ListView<String> recentList;

	@FXML
	private TextField tfAmount;

	@FXML
	private TextField tfNote;

	@FXML
	private TextField tfTo;

	private Double totalSpending = 0.0;

	private CommonController commonController = new CommonController();

	private User user;

	private String rmiName;

	private IBank bank;

	private List<HistoryTransaction> historyRender = new ArrayList<HistoryTransaction>();

	private HistoryService historyService = new HistoryService();
	private UserService userService = new UserService();

	public void setUser(User user, String rmiName) throws MalformedURLException, RemoteException, NotBoundException {
		this.user = user;
		this.rmiName = rmiName;

		myAccountNumber.setText(user.getAccountNumber());
		String formattedCurrency = commonController.formatCurrency(user.getBalance());
		myBanlance.setText(formattedCurrency);

		lblBalance.setText("Số dư :" + formattedCurrency);

		bank = (IBank) java.rmi.Naming.lookup("rmi://" + rmiName);
	}

	@FXML
	void onTransferClicked(ActionEvent event) {
		try {
			String to = tfTo.getText().trim();
			String note = tfNote.getText().trim();
			String amoutString = tfAmount.getText().trim();

			if (to == null || to.isEmpty() || note == null || note.isEmpty() || amoutString == null
					|| amoutString.isEmpty()) {
				commonController.alertInfo(AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin!");
			} else if (!commonController.isValidNumber(amoutString)) {
				commonController.alertInfo(AlertType.WARNING, "SỐ TIỀN INVALID",
						"Số tiền không hợp lệ. Vui lòng điền lại!");
			} else if (userService.getUserById(to) == null) {
				commonController.alertInfo(AlertType.WARNING, "INVALID!!!",
						"Tài khoản không tồn tại. Vui lòng điền lại!");
			} else {
				double amount = Double.parseDouble(amoutString);

				boolean ok = bank.transfer(user.getAccountNumber(), to, amount, note);

				if (ok) {
					totalSpending += amount;

					commonController.alertInfo(AlertType.INFORMATION, "Successfull", "✅ Chuyển tiền thành công!");

					clean();
					double newBal = bank.getBalance(user.getAccountNumber());
					user.setBalance(newBal);

					myBanlance.setText(commonController.formatCurrency(newBal));
					lblBalance.setText("Số dư: " + commonController.formatCurrency(newBal));
					recentList.getItems().add("Chuyển " + commonController.formatCurrency(amount) + " đến " + to
							+ " NOTE: (" + note + ")");
					lblTodaySpending.setText("Đã chi hôm nay: " + commonController.formatCurrency(totalSpending));
				} else {
					commonController.alertInfo(AlertType.ERROR, "Fail!!!",
							"❌ Giao dịch thất bại! Kiểm tra lại tài khoản hoặc số dư.");
				}
			}
		} catch (NumberFormatException e) {
			commonController.alertInfo(AlertType.WARNING, "SỐ TIỀN INVALID",
					"Số tiền không hợp lệ. Vui lòng điền lại!");
		} catch (Exception e) {
			commonController.alertInfo(AlertType.ERROR, "Lỗi", e.getMessage());
			e.printStackTrace();
		}
	}

	private void clean() {
		tfTo.setText(null);
		tfAmount.setText(null);
		tfNote.setText(null);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		lblTodaySpending.setText("Đã chi hôm nay: 0 đ");
	}

	@FXML
	void load(MouseEvent event) throws RemoteException {

		List<HistoryTransaction> historyTransactions = historyService.getByFromOrTo(user.getAccountNumber(), false);

		if (historyTransactions != null && !historyTransactions.isEmpty()) {

			double newBal = bank.getBalance(user.getAccountNumber());
			user.setBalance(newBal);

			List<String> renders = new ArrayList<String>();

			for (HistoryTransaction historyTransaction : historyTransactions) {
				if (!historyRender.contains(historyTransaction)) {
					historyRender.add(historyTransaction);

					renders.add("Nhận " + commonController.formatCurrency(historyTransaction.getAmount()) + " từ "
							+ historyTransaction.getFrom() + " NOTE: (" + historyTransaction.getNote() + ")");
				}
			}

			Platform.runLater(() -> {
				myBanlance.setText(commonController.formatCurrency(newBal));
				lblBalance.setText("Số dư: " + commonController.formatCurrency(newBal));

				recentList.getItems().addAll(renders);
			});
		}
	}

}
