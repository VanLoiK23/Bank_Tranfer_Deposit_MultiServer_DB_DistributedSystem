package service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.HistoryTransaction;
import util.DBUtil;

public class HistoryService {

	private String dbName = "bank_a";

	public HistoryService(String dbName) {
		this.dbName = dbName;
	}

	public HistoryService() {
	}

	public boolean addHistory(HistoryTransaction h) {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection(dbName);
			conn.setAutoCommit(false);

			PreparedStatement ps = conn.prepareStatement(
					"INSERT INTO history_transaction(from_acc, to_acc, amount, note) VALUES (?, ?, ?, ?)");
			ps.setString(1, h.getFrom());
			ps.setString(2, h.getTo());
			ps.setDouble(3, h.getAmount());
			ps.setString(4, h.getNote());
			ps.executeUpdate();

			conn.commit();
			System.out.println("✅ Giao dịch đã được lưu vào " + dbName);
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

	public List<HistoryTransaction> getByFromOrTo(String fromAcc,Boolean isFrom) {
		List<HistoryTransaction> list = new ArrayList<>();

		try (Connection conn = DBUtil.getConnection(dbName)) {
			PreparedStatement ps = conn.prepareStatement(
					"SELECT from_acc, to_acc, amount, note, created_at FROM history_transaction WHERE from_acc = ? ORDER BY created_at DESC");
			
			if(!isFrom) {
				ps = conn.prepareStatement(
						"SELECT from_acc, to_acc, amount, note, created_at FROM history_transaction WHERE to_acc = ? ORDER BY created_at DESC");
			}
			ps.setString(1, fromAcc);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				HistoryTransaction h = new HistoryTransaction();
				h.setFrom(rs.getString("from_acc"));
				h.setTo(rs.getString("to_acc"));
				h.setAmount(rs.getDouble("amount"));
				h.setNote(rs.getString("note"));
				list.add(h);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	
}
