package es.danielrusa.TFG_crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import es.unizar.contsem.codice.parser.Log;

public class Database {

	private Connection myConnection;
	private Set<Row> rowSet = new HashSet<Row>();
	private Set<String> idplataformaSet = null;
	private Set<String> linkSet = null;

	public void connect() {
		myConnection = getConnection();
		if (myConnection != null)
			Log.info("connected to database");
	}

	public void insertXML(String link, String expediente, String xml, String post, String idplataforma) {
		if (rowSet.size() > 10) {
			if (insertQueue()) {
				rowSet = new HashSet<Row>();
				idplataformaSet.add(idplataforma);
				linkSet.add(xml);
			} else {
				Log.error("error grave en insertXML");
			}
		} else {
			rowSet.add(new Row(link, expediente, xml, post, idplataforma));
		}
	}

	public boolean linkExists(String link) {
		if (linkSet == null) {
			long startTime = System.currentTimeMillis();
			linkSet = new HashSet<String>();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = myConnection.createStatement();
				rs = stmt.executeQuery("SELECT link FROM newxml");
				while (rs.next()) {
					linkSet.add(rs.getString("link"));
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				Log.error("error en linkExists");
				e.printStackTrace();
			} finally {
			}
			Log.info("inicialización de linkExists tarda %f", (double) (System.currentTimeMillis() - startTime) / 1000);
			Log.info("encontrados %d link en base de datos", linkSet.size());
		}
		return linkSet.contains(link);
	}

	public boolean idplataformExists(String idplataforma) {

		if (idplataformaSet == null) {
			long startTime = System.currentTimeMillis();
			idplataformaSet = new HashSet<String>();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = myConnection.createStatement();
				rs = stmt.executeQuery("SELECT idplataforma FROM newxml");
				while (rs.next()) {
					idplataformaSet.add(rs.getString("idplataforma"));
				}
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				Log.error("error en idplataformExists");
				e.printStackTrace();
			}
			Log.info("inicialización de idplataformExists tarda %f",
					(double) (System.currentTimeMillis() - startTime) / 1000);
			Log.info("encontrados %d idplataforma en base de datos", idplataformaSet.size());
		}

		return idplataformaSet.contains(idplataforma);
	}

	private boolean insertQueue() {
		long startTime = System.currentTimeMillis();
		String query = "INSERT INTO newxml (link,expediente,xml,peticion,idplataforma) VALUES ";
		for (Row row : rowSet)
			if (!linkExists(row.link))
				query += "('" + row.link.trim() + "','" + row.expediente.replaceAll("'", "_").trim() + "','"
						+ row.xml.trim() + "','" + row.post.trim() + "','" + row.idplataforma + "'),";
		query = query.substring(0, query.length() - 1) + ";";
		if (tryInsertQueue(3, query)) {
			Log.debug("insertQueue tarda %f", (double) (System.currentTimeMillis() - startTime) / 1000);
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
				Log.error("error en tryInsertQueue (%d)", numberOfTries);
				try {
					if (myConnection.isClosed()) {
						connect();
					}
				} catch (Exception ex) {
					Log.error("error INESPERADO en tryInsertQueue");
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
			rs = stmt.executeQuery("SELECT xml FROM newxml where id='" + id + "'");
			rs.next();
			output = rs.getString("xml");
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return output;
	}

	public static Connection getConnection() {
		Connection conexion = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String servidor = "jdbc:mysql://155.210.104.14:3306/licitaciones";
			String usuarioDB = "carlos";
			String passwordDB = "020202";
			conexion = DriverManager.getConnection(servidor, usuarioDB, passwordDB);
		} catch (Exception ex) {
			Log.error("error while connecting to database : %s", ex.getMessage());
			conexion = null;
		}
		return conexion;
	}

}
