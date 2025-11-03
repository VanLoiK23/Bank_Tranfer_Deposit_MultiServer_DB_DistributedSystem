package controller;

import controller.rmi.IBank;

public class Client {
	public static void main(String[] args) {
		try {
			IBank bankA = (IBank) java.rmi.Naming.lookup("rmi://192.168.1.243:2020/BankA");

			System.out.println("Before transfer:");
			System.out.println("1001 = " + bankA.getBalance("1001"));
			System.out.println("1002 = " + bankA.getBalance("1002"));

			boolean ok = bankA.transfer("1001", "1002", 1000);

			System.out.println(ok ? "✅ Transfer success!" : "❌ Transfer failed!");

			System.out.println("After transfer:");
			System.out.println("1001 = " + bankA.getBalance("1001"));
			System.out.println("1002 = " + bankA.getBalance("1002"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
