module Bank_MultiServer_DistributedSystem {
	requires javafx.controls;
	requires javafx.fxml;
	requires fontawesomefx;
	requires javafx.web;
	requires java.rmi;
	requires java.sql;
	requires com.rabbitmq.client;

	opens application to javafx.graphics, javafx.fxml;
	opens controller to javafx.fxml; // 👈 cho phép FXMLLoader truy cập
	opens controller.rmi to javafx.fxml;
	opens controller.rmi.impl to javafx.fxml;
	opens controller.server to javafx.fxml;

	exports model;
	exports controller; // nếu bạn muốn gói này public cho module khác
	exports controller.rmi; 
	exports controller.rmi.impl; 
	exports util;
}
