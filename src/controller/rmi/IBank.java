package controller.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IBank extends Remote {
    boolean transfer(String fromAcc, String toAcc, double amount,String note) throws RemoteException;
    boolean updateMirror(String fromAcc, String toAcc, double amount,String note) throws RemoteException; // để đồng bộ
    double getBalance(String acc) throws RemoteException;
	void setRemoteBank(String mirrolUrl) throws RemoteException;
}
