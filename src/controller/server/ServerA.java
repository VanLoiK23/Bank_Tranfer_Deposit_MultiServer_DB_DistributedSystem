package controller.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import controller.rmi.IBank;
import controller.rmi.IUser;
import controller.rmi.impl.BankServerImpl;
import controller.rmi.impl.UserServerImpl;

public class ServerA {
	public static void main(String[] args) {
		try {
			System.setProperty("java.rmi.server.hostname", "192.168.1.247"); // IP thật của máy A

			Registry reg = LocateRegistry.createRegistry(2020);
			IBank bank = new BankServerImpl("bank_a");
			reg.rebind("BankA", bank);
			IUser userBank = new UserServerImpl("bank_a");
			reg.rebind("UserBankA", userBank);
			System.out.println("✅ Server A running on port 2020");

			// resolve 2 server can't not run parallel
			Thread.sleep(10000);
			String mirrolUrlForBank = "rmi://192.168.1.122:2025/BankB";
			bank.setRemoteBank(mirrolUrlForBank);
			
			String mirrolUrlForUser = "rmi://192.168.1.122:2025/UserBankB";
			userBank.setRemoteBank(mirrolUrlForUser);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
