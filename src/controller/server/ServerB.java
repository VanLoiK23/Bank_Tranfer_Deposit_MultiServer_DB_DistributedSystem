package controller.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import controller.rmi.IBank;
import controller.rmi.IUser;
import controller.rmi.impl.BankServerImpl;
import controller.rmi.impl.UserServerImpl;

public class ServerB {
	public static void main(String[] args) {
		try {
			System.setProperty("java.rmi.server.hostname", "192.168.1.122"); // IP máy ảo hoặc máy B

			Registry reg = LocateRegistry.createRegistry(2025);
			IBank bank = new BankServerImpl("bank_b");
			reg.rebind("BankB", bank);
			IUser userBank = new UserServerImpl("bank_b");
			reg.rebind("UserBankB", userBank);
			System.out.println("✅ Server B running on port 2025");

			Thread.sleep(10000);
			String mirrolUrlForBank = "rmi://192.168.1.247:2020/BankA";
			bank.setRemoteBank(mirrolUrlForBank);
			
			String mirrolUrlForUser = "rmi://192.168.1.247:2020/UserBankA";
			userBank.setRemoteBank(mirrolUrlForUser);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}