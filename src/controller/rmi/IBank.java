package controller.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBank extends Remote {
    boolean transfer(String fromAcc, String toAcc, double amount) throws RemoteException;
    boolean updateMirror(String fromAcc, String toAcc, double amount) throws RemoteException; // để đồng bộ
    double getBalance(String acc) throws RemoteException;
}
