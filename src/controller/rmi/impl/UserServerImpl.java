package controller.rmi.impl;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import controller.rmi.IUser;
import model.User;
import service.UserService;

public class UserServerImpl extends UnicastRemoteObject implements IUser {
	private String dbName;
	private String mirrorUrl;
	private UserService userService;

	public UserServerImpl(String dbName) throws RemoteException {
		this.dbName = dbName;
		this.userService = new UserService(dbName);
	}

	@Override
	public void setRemoteBank(String mirrorUrl) throws RemoteException {
		this.mirrorUrl = mirrorUrl;
		System.out.println("🔗 " + mirrorUrl + " linked to remote user service.");
	}

	@Override
	public boolean insertUser(User user) throws RemoteException {
		System.out.println("🟢 Inserting user into " + dbName);
		boolean localSuccess = userService.register(user);

		if (localSuccess && mirrorUrl != null) {
			try {
				IUser mirror = (IUser) Naming.lookup(mirrorUrl);
				System.out.println("🔄 Syncing user to mirror server...");
				mirror.mirrorInsert(user); // chỉ đồng bộ, không gọi ngược lại
			} catch (Exception e) {
				System.err.println("⚠️ Mirror sync failed: " + e.getMessage());
				
				return false;
			}
		}

		return localSuccess;
	}

	@Override
	public boolean mirrorInsert(User user) throws RemoteException {
		System.out.println("📥 Mirror insert user into " + dbName);
		return userService.register(user);
	}
}
