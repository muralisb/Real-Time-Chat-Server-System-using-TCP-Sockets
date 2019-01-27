package Server;
/**
 * @author: Murali S Badiger & Rakshita Vibhu
 * 
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Connect_db {
	static Connection con = null;
	static String URL = "jdbc:postgresql://localhost:5432/Chat";
	String id;
	String msg;
	String rid;
	String cid;

	// Database credentials
	static final String USER = "mb687";
	static final String PASS = "inclose37";

	public Connect_db(String cid) {
		this.cid = cid;
	}

	public Connect_db(String id, String msg, String rid) {
		try {
			Connection conn = getConnection();
			this.id = id;
			this.msg = msg;
			this.rid = rid;
			Statement stmt = conn.createStatement();
			String qry = "INSERT INTO CHAT VALUES(" + "\'" + this.id + "\'," + "\'" + this.msg + "\'," + "\'" + this.rid
					+ "\')";
			stmt.executeUpdate(qry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		try {
			Class.forName("org.postgresql.Driver");
			con = DriverManager.getConnection(URL, "Murali", "96869731");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return con;
	}

	public ResultSet retrive() {
		ResultSet rs = null;
		try {
			Connection conn1 = getConnection();
			Statement stmt2 = conn1.createStatement();
			String qry = "SELECT message, sent_to FROM CHAT WHERE clientid=" + "\'" + this.cid + "\'";
			rs = stmt2.executeQuery(qry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public ResultSet received() {
		ResultSet rs = null;
		try {
			Connection conn2 = getConnection();
			Statement stmt3 = conn2.createStatement();
			String qry = "SELECT message, clientid FROM CHAT where sent_to=" + "\'" + this.cid + "\'";
			rs = stmt3.executeQuery(qry);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

}
