import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
	private static String db = "jdbc:oracle:thin:@ora3.elka.pw.edu.pl:1521:ora3inf";
	private static String user = "jwiniar1";
	private static String pass = user;

	public String database = "Oracle";

	private boolean connflag;
	private Connection connection;
	private Statement Statement;
	private PreparedStatement PrepStatement;
	private ResultSet ResultSet;

	public Database() {
		connection = null;
		connflag = false;
	}

	public void initialize() {

		try {
			if (!isDbConnected())
				connection();
		} catch (SQLException ex) {
			System.out.println("[" + database.toString() + " Database] Failed to connect: " + ex);
		} catch (ClassNotFoundException e) {
			System.out.println("[" + database.toString() + " Database] Connector not found: " + e);
		}
	}

	private Connection connection() throws ClassNotFoundException, SQLException {
		connection = DriverManager.getConnection(db, user, pass);
		return connection;
	}

	public ResultSet executeQuery(String query) {

		initialize();
		try {
			PrepStatement = connection.prepareStatement(query);
			return PrepStatement.executeQuery();
		} catch (SQLException ex) {
			System.out.println(database.toString() + " db unable to execute: " + query + " : " + ex);
			errorMsg();
		}
		return null;
	}

	public boolean executeQueryIfResult(String query) {
		ResultSet rs = executeQuery(query);
		boolean flag = false;
		try {
			if (rs != null)
				if (rs.next())
					flag = true;

		} catch (SQLException e) {
			System.out.println("Problem with query result");
		}
		return flag;
	}

	public int executeUpdate(String query) {
		initialize();
		try {
			Statement = connection.createStatement();
			return Statement.executeUpdate(query);
		} catch (SQLException ex) {
			System.out.println(database.toString() + " db unable to execute: " + query + " : " + ex);
			errorMsg();
		}
		return 0;
	}
	
	public boolean isDbConnected() {
		boolean ret = false;
		if (connflag)
			try {
				if (connection.isClosed() || connection != null)
					ret = true;
			} catch (SQLException e) {
				ret = false;
			}
		return ret;
	}

	public void close() {
		try {
			if (PrepStatement != null)
				PrepStatement.close();
			if (ResultSet != null)
				ResultSet.close();
			if (connection != null)
				connection.close();
			if (connflag)
				connflag = false;

		} catch (SQLException ex) {
			connection = null;
			PrepStatement = null;
			ResultSet = null;
			connflag = false;
		}
	}

	protected void finalize() {
		close();
	}

	private void errorMsg() {
		System.out.println("Severe error has occured. Restart database or contact support.");
		System.exit(0);
	}
}
