package controller.rmi.impl;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import controller.rmi.IBank;
import model.HistoryTransaction;
import service.HistoryService;
import util.DBUtil;

public class BankServerImpl extends UnicastRemoteObject implements IBank {
	private String dbName;
	private String mirrorUrl;
	private HistoryService historyService;

	public BankServerImpl(String dbName) throws RemoteException {
		this.dbName = dbName;
		this.historyService = new HistoryService(dbName);
	}

	public void setRemoteBank(String mirrorUrl) throws RemoteException {
		this.mirrorUrl = mirrorUrl;
		System.out.println("🔗 " + mirrorUrl + " linked to remote bank.");
	}

	@Override
	public boolean transfer(String fromAcc, String toAcc, double amount, String note) throws RemoteException {
		System.out.println("[MAIN][" + dbName + "] Transfer " + amount + " from " + fromAcc + " to " + toAcc);
		Connection conn = null;
		try {
			conn = DBUtil.getConnection(dbName);
			conn.setAutoCommit(false); // Bắt đầu transaction

			if (!updateBalance(conn, fromAcc, -amount)) {
				System.out.println("❌ Not enough balance or invalid sender");
				conn.rollback();
				return false;
			}
			if (!updateBalance(conn, toAcc, +amount)) {
				System.out.println("❌ Invalid receiver");
				conn.rollback();
				return false;
			}

			// Gọi sang server mirror
			IBank mirror = (IBank) java.rmi.Naming.lookup(mirrorUrl);
			boolean ok = mirror.updateMirror(fromAcc, toAcc, amount, note);
			if (!ok) {
				System.out.println("❌ Mirror update failed → rollback local");
				conn.rollback();
				return false;
			}

			conn.commit();
			System.out.println("✅ Transaction committed in " + dbName);
			updateHistory(fromAcc, toAcc, amount, note);
			return true;
		} catch (Exception e) {
			try {
				if (conn != null)
					conn.rollback();
			} catch (Exception ignored) {
			}
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ignored) {
			}
		}
	}

	@Override
	public boolean updateMirror(String fromAcc, String toAcc, double amount, String note) throws RemoteException {
		System.out.println("[MIRROR][" + dbName + "] Updating mirror...");
		Connection conn = null;
		try {
			conn = DBUtil.getConnection(dbName);
			conn.setAutoCommit(false);

			if (!updateBalance(conn, fromAcc, -amount)) {
				conn.rollback();
				return false;
			}
			if (!updateBalance(conn, toAcc, +amount)) {
				conn.rollback();
				return false;
			}

			conn.commit();
			System.out.println("✅ Mirror update committed in " + dbName);
			updateHistory(fromAcc, toAcc, amount, note);
			return true;
		} catch (Exception e) {
			try {
				if (conn != null)
					conn.rollback();
			} catch (Exception ignored) {
			}
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (conn != null)
					conn.close();
			} catch (Exception ignored) {
			}
		}
	}

	private void updateHistory(String fromAcc, String toAcc, double amount, String note) {
		HistoryTransaction historyTransaction = new HistoryTransaction();
		historyTransaction.setAmount(amount);
		historyTransaction.setFrom(fromAcc);
		historyTransaction.setTo(toAcc);
		historyTransaction.setNote(note);

		historyService.addHistory(historyTransaction);
	}

	private boolean updateBalance(Connection conn, String acc, double delta) throws Exception {
		PreparedStatement ps = conn.prepareStatement("SELECT balance FROM accounts WHERE id=?");
		ps.setString(1, acc);
		ResultSet rs = ps.executeQuery();
		if (!rs.next())
			return false;
		double bal = rs.getDouble(1);
		if (delta < 0 && bal + delta < 0)
			return false;

		PreparedStatement update = conn.prepareStatement("UPDATE accounts SET balance=? WHERE id=?");
		update.setDouble(1, bal + delta);
		update.setString(2, acc);
		update.executeUpdate();
		return true;
	}

	@Override
	public double getBalance(String acc) throws RemoteException {
		try (Connection conn = DBUtil.getConnection(dbName)) {
			PreparedStatement ps = conn.prepareStatement("SELECT balance FROM accounts WHERE id=?");
			ps.setString(1, acc);
			ResultSet rs = ps.executeQuery();
			return rs.next() ? rs.getDouble(1) : -1;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

}
