package controller.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import controller.rmi.IBank;
import controller.rmi.impl.BankServerImpl;

public class ServerB {
	public static void main(String[] args) {
		try {
			System.setProperty("java.rmi.server.hostname", "192.168.1.122"); // IP máy ảo hoặc máy B

			Registry reg = LocateRegistry.createRegistry(2025);
			IBank bank = new BankServerImpl("bank_b");
			reg.rebind("BankB", bank);
			System.out.println("✅ Server B running on port 2025");

			Thread.sleep(10000);
			String mirrolUrl = "rmi://192.168.1.243:2020/BankA";
			bank.setRemoteBank(mirrolUrl);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}