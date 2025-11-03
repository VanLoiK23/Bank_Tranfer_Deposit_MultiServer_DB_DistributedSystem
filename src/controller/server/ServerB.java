package controller.server;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import controller.rmi.IBank;
import controller.rmi.impl.BankServerImpl;

public class ServerB {
    public static void main(String[] args) {
        try {
            Registry reg = LocateRegistry.createRegistry(2099);
            IBank bank = new BankServerImpl("bank_b", "rmi://192.168.1.241:1099/BankA");
            reg.rebind("BankB", bank);
            System.out.println("✅ Server B running on port 2099");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}