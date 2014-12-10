package es.danielrusa.crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import es.unizar.contsem.codice.parser.Log;

public class Database {

	public boolean exhaustiveSearch = false;
	private Connection myConnection;
	private Set<Row> rowSet = new HashSet<Row>();
	private Set<String> platformIdSet = null;
	private Set<String> linkSet = null;

	public void connect() {
		myConnection = getConnection();
		if (myConnection != null)
			Log.info(this.getClass(), "connected to database");
	}

	public void insertRow(String link, String expediente, String xml, String post, String idplataforma) {
		rowSet.add(new Row(link, expediente, xml, post, idplataforma));
		if (platformIdSet != null)
			platformIdSet.add(idplataforma);
		linkSet.add(xml);
		if (rowSet.size() >= 10)
			if (insertRowSet())
				rowSet = new HashSet<Row>();
			else
				Log.error(this.getClass(), "error at insertRow");
	}

	public void insertRow(Row row) {
		rowSet.add(row);
		if (platformIdSet != null)
			platformIdSet.add(row.idplataforma);
		linkSet.add(row.xml);
		if (rowSet.size() >= 10)
			if (insertRowSet())
				rowSet = new HashSet<Row>();
			else
				Log.error(this.getClass(), "error at insertRow");
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

	private boolean insertRowSet() {
		long startTime = System.currentTimeMillis();
		String query = "INSERT INTO licitaciones (link,expediente,xml,peticion,idplataforma) VALUES ";
		for (Row row : rowSet)
			if (!linkExists(row.link))
				query += "('" + row.link.trim() + "','" + row.expediente.replaceAll("'", "_").trim() + "','"
						+ row.xml.trim() + "','" + row.post.trim() + "','" + row.idplataforma + "'),";
			else
				Log.warning(this.getClass(), "existing link not expected");
		query = query.substring(0, query.length() - 1) + ";";
		if (tryInsertQueue(3, query)) {
			Log.debug(this.getClass(), "insertQueue takes %f seconds",
					(double) (System.currentTimeMillis() - startTime) / 1000);
			return true;
		} else
			return false;
	}

	private boolean tryInsertQueue(int numberOfTries, String query) {
		if (numberOfTries > 0) {
			try {
				Statement st = (Statement) myConnection.createStatement();
				st.executeUpdate(query);
			} catch (SQLException e) {
				Log.error(this.getClass(), "error connecting database, numberOfTries = %d)", numberOfTries);
				try {
					if (myConnection.isClosed()) {
						connect();
					}
				} catch (Exception ex) {
					Log.error(this.getClass(), "unexpected error in tryInsertQueue");
				}
				tryInsertQueue(numberOfTries - 1, query);
			}
			return true;
		} else
			return false;
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

	public Connection getConnection() {
		Connection conexion = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String servidor = "jdbc:mysql://155.210.104.14:3306/licitaciones";
			String usuarioDB = "carlos";
			String passwordDB = "020202";
			conexion = DriverManager.getConnection(servidor, usuarioDB, passwordDB);
		} catch (Exception ex) {
			Log.error(this.getClass(), "error while connecting to database : %s", ex.getMessage());
			conexion = null;
		}
		return conexion;
	}

}
