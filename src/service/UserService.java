package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.User;
import util.DBUtil;

public class UserService {

	private String dbName = "bank_a";

	public UserService(String dbName) {
		this.dbName = dbName;
	}

	public UserService() {
	}

	public boolean register(User user) {
		Connection conn = null;
		try {
			conn = DBUtil.getConnection(dbName);
			conn.setAutoCommit(false);

			// Kiểm tra ID hoặc username đã tồn tại chưa
			if (existsById(user.getAccountNumber(), conn)) {
				System.out.println("❌ Account ID đã tồn tại trong " + dbName);
				return false;
			}
			if (existsByUsername(user.getUserName(), conn)) {
				System.out.println("❌ Username đã tồn tại trong " + dbName);
				return false;
			}

			// Thêm tài khoản mới
			PreparedStatement update = conn
					.prepareStatement("INSERT INTO accounts(id, username, balance, password) VALUES (?, ?, ?, ?)");
			update.setString(1, user.getAccountNumber());
			update.setString(2, user.getUserName());
			update.setDouble(3, user.getBalance());
			update.setString(4, user.getPassword());
			update.executeUpdate();

			conn.commit();
			System.out.println("✅ Account registered successfully in " + dbName);
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

	public boolean existsById(String id) {
		try (Connection conn = DBUtil.getConnection(dbName)) {
			return existsById(id, conn);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean existsById(String id, Connection conn) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM accounts WHERE id = ?");
		ps.setString(1, id);
		ResultSet rs = ps.executeQuery();
		rs.next();
		return rs.getInt(1) > 0;
	}

	public boolean existsByUsername(String username) {
		try (Connection conn = DBUtil.getConnection(dbName)) {
			return existsByUsername(username, conn);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private boolean existsByUsername(String username, Connection conn) throws SQLException {
		PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) FROM accounts WHERE username = ?");
		ps.setString(1, username);
		ResultSet rs = ps.executeQuery();
		rs.next();
		return rs.getInt(1) > 0;
	}

	public User getUserById(String id) {
		try (Connection conn = DBUtil.getConnection(dbName)) {
			PreparedStatement ps = conn
					.prepareStatement("SELECT id, username, balance, password FROM accounts WHERE id = ?");
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				User u = new User();
				u.setAccountNumber(rs.getString("id"));
				u.setUserName(rs.getString("username"));
				u.setBalance(rs.getDouble("balance"));
				u.setPassword(rs.getString("password"));
				return u;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null; // Không tìm thấy
	}

}
