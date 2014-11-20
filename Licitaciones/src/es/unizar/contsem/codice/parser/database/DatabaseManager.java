package es.unizar.contsem.codice.parser.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

	private Connection db_connection;

	public DatabaseManager() {
		this.db_connection = DatabaseConnection.getConnection();
	}

	// testing purposes
	public String getFirstXML() throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		String output = null;
		try {
			stmt = db_connection.createStatement();
			rs = stmt.executeQuery("SELECT xml FROM xml where id=1");
			// get the number of rows from the result set
			rs.next();
			output = rs.getString("xml");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			rs.close();
			stmt.close();
		}
		return output;
	}

}
