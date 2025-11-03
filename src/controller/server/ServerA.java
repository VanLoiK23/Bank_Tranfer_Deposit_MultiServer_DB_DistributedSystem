package controller.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import controller.rmi.IBank;
import controller.rmi.impl.BankServerImpl;

public class ServerA {
	public static void main(String[] args) {
		try {
			Registry reg = LocateRegistry.createRegistry(1099);
			IBank bank = new BankServerImpl("bank_a", "rmi://192.168.1.122:2099/BankB");
			reg.rebind("BankA", bank);
			System.out.println("✅ Server A running on port 1099");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
