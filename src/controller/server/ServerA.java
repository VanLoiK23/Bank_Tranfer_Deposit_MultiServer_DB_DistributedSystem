package controller.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import controller.rmi.IBank;
import controller.rmi.impl.BankServerImpl;

public class ServerA {
	public static void main(String[] args) {
		try {
			System.setProperty("java.rmi.server.hostname", "192.168.1.243"); // IP thật của máy A

			Registry reg = LocateRegistry.createRegistry(2020);
			IBank bank = new BankServerImpl("bank_a");
			reg.rebind("BankA", bank);
			System.out.println("✅ Server A running on port 2020");

			// resolve 2 server can't not run parallel
			Thread.sleep(10000);
			String mirrolUrl = "rmi://192.168.1.122:2025/BankB";
			bank.setRemoteBank(mirrolUrl);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
