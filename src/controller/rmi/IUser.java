package controller.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

import model.User;

public interface IUser extends Remote {
    boolean insertUser(User user) throws RemoteException;
    boolean mirrorInsert(User user) throws RemoteException;
	void setRemoteBank(String mirrolUrl) throws RemoteException;
}
