package es.danielrusa.crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import es.unizar.contsem.Log;

public class Database {

	private String server, user, pass;
	public boolean exhaustiveSearch = false;
	private Connection myConnection;
	private Set<Row> rowSet = new HashSet<Row>();
	private Set<String> platformIdSet = null;
	private Set<String> linkSet = null;

	private int numberOfInsertErrors = 0;

	public Database(String server, String user, String pass) {
		this.server = server;
		this.user = user;
		this.pass = pass;
	}

	public boolean connect() {
		try {
			if (myConnection != null)
				myConnection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		myConnection = getMySQLConnection(server, user, pass);
		if (myConnection != null)
			Log.info(this.getClass(), "connected to database");
		else
			Log.error(this.getClass(), "could not provide connection");
		return myConnection != null;
	}

	public void disconnect() {
		if (myConnection != null)
			try {
				myConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	public void insertRow(String link, String expediente, String xml, String post, String idplataforma) {
		rowSet.add(new Row(link, expediente, xml, post, idplataforma));
		if (platformIdSet != null)
			platformIdSet.add(idplataforma);
		linkSet.add(xml);
		if (rowSet.size() >= 10)
			if (insertRowSet() > 0)
				rowSet = new HashSet<Row>();
			else
				Log.error(this.getClass(), "error at insertRow");
	}

	public int insertRow(Row row) {
		int numberOfRowsInserted = 0;
		rowSet.add(row);
		if (platformIdSet != null)
			platformIdSet.add(row.idplataforma);
		linkSet.add(row.xml);
		if (rowSet.size() >= 10) {
			if ((numberOfRowsInserted = insertRowSet()) > 0)
				rowSet = new HashSet<Row>();
			else
				Log.error(this.getClass(), "error at insertRow");
		}
		return numberOfRowsInserted;
	}

	public boolean linkExists(String link) {
		if (linkSet == null) {
			long startTime = System.currentTimeMillis();
			linkSet = new HashSet<String>();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				Log.info(this.getClass(), "retrieving links from database ...");
				stmt = myConnection.createStatement();
				rs = stmt.executeQuery("SELECT link FROM licitaciones");
				while (rs.next()) {
					linkSet.add(rs.getString("link"));
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				Log.error(this.getClass(), "error at linkExists");
				e.printStackTrace();
			} finally {
			}
			Log.info(this.getClass(), "init of linkExists takes %f seconds",
					(double) (System.currentTimeMillis() - startTime) / 1000);
			Log.info(this.getClass(), "found %d links in database", linkSet.size());
		}
		return linkSet.contains(link);
	}

	public boolean platformIdExists(String idplataforma) {
		if (exhaustiveSearch)
			return false;
		if (platformIdSet == null) {
			long startTime = System.currentTimeMillis();
			platformIdSet = new HashSet<String>();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				Log.info(this.getClass(), "retrieving platformIds from database ...");
				stmt = myConnection.createStatement();
				rs = stmt.executeQuery("SELECT idplataforma FROM licitaciones");
				while (rs.next()) {
					platformIdSet.add(rs.getString("idplataforma"));
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				Log.error(this.getClass(), "error at platformIdExists");
				e.printStackTrace();
			}
			Log.info(this.getClass(), "init of platformIdExists takes %f seconds",
					(double) (System.currentTimeMillis() - startTime) / 1000);
			Log.info(this.getClass(), "found %d platformIds in database", platformIdSet.size());
		}
		return platformIdSet.contains(idplataforma);
	}

	public int flushRowSet() {
		return insertRowSet();
	}

	private int insertRowSet() {
		long startTime = System.currentTimeMillis();
		String query = "INSERT INTO licitaciones (link,expediente,xml,peticion,idplataforma) VALUES ";
		for (Row row : rowSet)
			if (!linkExists(row.link))
				query += "('" + row.link.trim() + "','" + row.expediente.replaceAll("'", "_").trim() + "','"
						+ row.xml.replaceAll("'", " ").trim() + "','" + row.post.trim() + "','" + row.idplataforma
						+ "'),";
			else
				Log.warning(this.getClass(), "existing link not expected");
		query = query.substring(0, query.length() - 1) + ";";
		if (tryInsertQueue(1, query)) {
			Log.debug(this.getClass(), "insertRowSet takes %f seconds",
					(double) (System.currentTimeMillis() - startTime) / 1000);
			return rowSet.size();
		} else
			return 0;
	}

	private boolean tryInsertQueue(int tryCount, String query) {
		if (tryCount < 4) {
			try {
				Statement st = (Statement) myConnection.createStatement();
				st.executeUpdate(query);
			} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException ex) {
				numberOfInsertErrors++;
				Log.error(this.getClass(), "MySQLSyntaxErrorException, ignoring insert, saved at query%03d.sql",
						numberOfInsertErrors);
				Log.writeInfile(String.format("query%03d.sql", numberOfInsertErrors), query);
			} catch (SQLException e) {
				Log.warning(this.getClass(), "try number %d at tryInsertQueue failed: %s", tryCount, e.getClass()
						.getSimpleName());
				connect();
				tryInsertQueue(tryCount + 1, query);
			}
			return true;
		} else {
			Log.warning(this.getClass(), "tryInsertQueue failed so many times, next insertRow will try again");
			return false;
		}
	}

	public String getXML(int id) {
		Statement stmt = null;
		ResultSet rs = null;
		String output = null;
		try {
			stmt = myConnection.createStatement();
			rs = stmt.executeQuery("SELECT xml FROM licitaciones where id='" + id + "'");
			rs.next();
			output = rs.getString("xml");
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return output;
	}

	public Set<Row> getRows(int startId, int endId) {
		long startTime = System.currentTimeMillis();
		Statement stmt = null;
		ResultSet rs = null;
		Set<Row> rowSet = new HashSet<Row>();
		try {
			stmt = myConnection.createStatement();
			rs = stmt.executeQuery("SELECT * FROM licitaciones WHERE id>=" + startId + " AND id<" + endId);
			while (rs.next())
				rowSet.add(new Row(rs.getInt("id"), rs.getString("link"), rs.getString("expediente"), rs
						.getString("xml"), rs.getString("peticion"), rs.getString("idplataforma")));
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Log.info(this.getClass(), "getRows takes %f seconds", (double) (System.currentTimeMillis() - startTime) / 1000);
		return rowSet;
	}

	public void updateRow(Row row) {
		long startTime = System.currentTimeMillis();
		Statement stmt = null;
		String query = "UPDATE licitaciones SET xml='" + row.xml.replace("'", " ") + "' WHERE id=" + row.id;
		try {
			stmt = myConnection.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
		} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException ex) {
			Log.error(this.getClass(), "syntax exception, check insert_error.sql");
			Log.writeInfile("insert_error.sql", query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Log.info(this.getClass(), "updateRow takes %f seconds",
				(double) (System.currentTimeMillis() - startTime) / 1000);
	}

	public static Connection getMySQLConnection(String server, String user, String pass) {
		Connection conexion = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conexion = DriverManager.getConnection(server, user, pass);
		} catch (Exception ex) {
			Log.error(Database.class, "error while connecting to database : %s", ex.getMessage());
			conexion = null;
		}
		return conexion;
	}

}
