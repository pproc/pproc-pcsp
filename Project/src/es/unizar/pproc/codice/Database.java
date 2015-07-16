package es.unizar.pproc.codice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import es.unizar.pproc.Log;
import es.unizar.pproc.Utils;

/**
 * Simple database class. This class assume the existence of a table named
 * {@value #TABLE_NAME} with two columns:
 * <ul>
 * <li><b>id</b>: <i>auto-increment primary_key integer</i>. Database
 * identifier.</li>
 * <li><b>link</b>: <i>medium_text</i>. URL with the xml content.</li>
 * <li><b>flag</b>: <i>integer</i>. Row entry status.</li>
 * <li><b>xml</b>: <i>longtext</i>. XML/CODICE content.</li>
 * <li><b>uuid</b>: <i>varchar(129)</i>. UUID of the XML/CODICE document.</li>
 * </ul>
 * 
 * @author gesteban
 *
 */
public class Database {

	public static final String TAG = Database.class.getSimpleName();

	/**
	 * Entry without any content at 'xml' column
	 */
	public static final int FLAG_ONLY_LINK = 0;
	/**
	 * Entry with 'xml' content, not yet checked
	 */
	public static final int FLAG_XML_UNCHECKED = 1;
	/**
	 * Entry with invalid 'xml' content
	 */
	public static final int FLAG_XML_INCORRECT = -1;
	/**
	 * Valid entry, not yet endorsement check
	 */
	public static final int FLAG_UUID_UNCHECKED = 2;
	/**
	 * Valid entry, not yet endorsement check, deprecated
	 */
	public static final int FLAG_UUID_UNCHECKED_DEPRECATED = -2;
	/**
	 * Valid entry, endorsement checked
	 */
	public static final int FLAG_CHECKED_VALID = 3;
	/**
	 * Valid entry, endorsement checked, deprecated
	 */
	public static final int FLAG_CHECKED_DEPRECATED = -3;
	/**
	 * Valid entry, already parsed
	 */
	public static final int FLAG_CHECKED_PARSED = 4;

	private String TABLE_NAME = "XmlLinks2";
	private String server, user, pass;
	private Connection myConnection;
	private int MAX_TRIES = 2;
	private int numberOfInsertErrors = 0;

	private static Connection getMySQLConnection(String server, String user, String pass) {
		Connection conexion = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conexion = DriverManager.getConnection(server, user, pass);
		} catch (Exception ex) {
			Log.e(TAG, "[getMySQLConnection] error while connecting to database : %s", ex.getMessage());
			conexion = null;
		}
		return conexion;
	}

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
			Log.d(TAG, "[connect] connected to database");
		else
			Log.e(TAG, "[connect] could not provide connection");
		return myConnection != null;
	}

	public boolean disconnect() {
		if (myConnection != null)
			try {
				myConnection.close();
				myConnection = null;
			} catch (SQLException e) {
				e.printStackTrace();
				return false;
			}
		return true;
	}

	public Set<XmlLink> selectByLimit(boolean wantXml, int limit, int... flag) {
		if (myConnection == null) {
			Log.e(TAG, "[selectByLimit] database connection not established");
			return null;
		}
		long startTime = System.currentTimeMillis();
		Statement stmt = null;
		Set<XmlLink> xmlLinks = new HashSet<XmlLink>();
		try {
			stmt = myConnection.createStatement();
			String query = "SELECT id, link, flag," + (wantXml ? " xml," : "") + " uuid FROM " + TABLE_NAME + " WHERE ";
			for (int i = 0; i < flag.length; i++)
				query += "flag = " + flag[i] + " or ";
			if (flag.length > 0)
				query = query.substring(0, query.length() - 4);
			query += (limit > 0 ? " limit " + limit : "");
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next())
				if (wantXml)
					xmlLinks.add(new XmlLink(rs.getInt("id"), rs.getString("link"), rs.getInt("flag"), rs
							.getString("xml"), rs.getString("uuid")));
				else
					xmlLinks.add(new XmlLink(rs.getInt("id"), rs.getString("link"), rs.getInt("flag"), rs
							.getString("uuid")));
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "[selectByLimit] takes %f seconds", (double) (System.currentTimeMillis() - startTime) / 1000);
		return xmlLinks;
	}

	public Set<XmlLink> selectByUuids(boolean wantXml, Set<String> uuids, int... flag) throws SQLException {
		if (myConnection == null) {
			Log.e(TAG, "[selectByUuids] database connection not established");
			return null;
		}
		if (uuids.size() == 0)
			return new HashSet<XmlLink>();
		long startTime = System.currentTimeMillis();
		Statement stmt = null;
		Set<XmlLink> xmlLinks = new HashSet<XmlLink>();
		String query = "";
		try {
			stmt = myConnection.createStatement();
			query = "SELECT id, link, flag," + (wantXml ? " xml," : "") + " uuid FROM " + TABLE_NAME + " WHERE ";
			for (int i = 0; i < flag.length; i++)
				query += "flag = " + flag[i] + " or ";
			if (flag.length > 0)
				query = query.substring(0, query.length() - 4) + " and ";
			for (String uuid : uuids)
				query += "uuid = '" + uuid.replace("-", "").trim() + "' or ";
			if (flag.length > 0 || uuids.size() > 0)
				query = query.substring(0, query.length() - 4);
			ResultSet rs = stmt.executeQuery(query);
			while (rs.next())
				if (wantXml)
					xmlLinks.add(new XmlLink(rs.getInt("id"), rs.getString("link"), rs.getInt("flag"), rs
							.getString("xml"), rs.getString("uuid")));
				else
					xmlLinks.add(new XmlLink(rs.getInt("id"), rs.getString("link"), rs.getInt("flag"), rs
							.getString("uuid")));
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			Utils.writeInfile("debug/query.sql", query);
			Log.e(TAG, "[selectByUuids] query saved");
			throw e;
		}
		Log.d(TAG, "[selectByUuids] takes %f seconds", (double) (System.currentTimeMillis() - startTime) / 1000);
		return xmlLinks;
	}

	public int insertLinks(Set<String> links) {
		if (links.size() == 0)
			return 1;
		if (myConnection == null) {
			Log.e(TAG, "[insertLinks] database connection not established");
			return 0;
		}
		long startTime = System.currentTimeMillis();
		String query = "INSERT INTO " + TABLE_NAME + " (link,flag) VALUES ";
		for (String link : links)
			query += "('" + link + "'," + FLAG_ONLY_LINK + "),";
		query = query.substring(0, query.length() - 1) + ";";
		if (tryUpdate(MAX_TRIES, query)) {
			Log.d(TAG, "[insertLinks] takes %f seconds to insert %d links",
					(double) (System.currentTimeMillis() - startTime) / 1000, links.size());
			return 1;
		} else
			return 0;
	}

	public int updateXml(XmlLink xmlLink) {
		if (myConnection == null) {
			Log.e(TAG, "[updateXml] database connection not established");
			return -1;
		}
		long startTime = System.currentTimeMillis();
		String query = "UPDATE " + TABLE_NAME + " SET xml = '" + xmlLink.xml.replace("'", "''") + "' WHERE id = "
				+ xmlLink.id + ";";
		if (tryUpdate(MAX_TRIES, query)) {
			Log.d(TAG, "[updateXml] takes %f seconds to update 1 xml",
					(double) (System.currentTimeMillis() - startTime) / 1000);
			return 1;
		} else
			return 0;
	}

	public int updateXmls(Set<XmlLink> xmlLinks) {
		if (xmlLinks.size() == 0)
			return 0;
		if (myConnection == null) {
			Log.e(TAG, "[updateXmls] database connection not established");
			return -1;
		}
		long startTime = System.currentTimeMillis();
		int updateCount = 0;
		for (XmlLink xmlLink : xmlLinks) {
			String query = "UPDATE " + TABLE_NAME + " SET xml = '" + xmlLink.xml.replace("'", "''") + "' WHERE id = "
					+ xmlLink.id + ";";
			if (tryUpdate(MAX_TRIES, query))
				updateCount++;
			else
				Log.e(TAG, "[updateXmls] xml %d extraction failed", xmlLink.id);
		}
		Log.d(TAG, "[updateXmls] takes %f seconds to update %d xmls",
				(double) (System.currentTimeMillis() - startTime) / 1000, xmlLinks.size());
		return updateCount;
	}

	public int updateFlag(XmlLink xmlLink, int flag) {
		if (myConnection == null) {
			Log.e(TAG, "[updateFlag] database connection not established");
			return -1;
		}
		long startTime = System.currentTimeMillis();
		String query = "UPDATE " + TABLE_NAME + " SET flag = " + flag + " WHERE id = '" + xmlLink.id + "';";
		if (tryUpdate(MAX_TRIES, query)) {
			Log.d(TAG, "[updateFlag] takes %f seconds to update 1 flag",
					(double) (System.currentTimeMillis() - startTime) / 1000);
			return 1;
		} else
			return -1;
	}

	public int updateFlags(Set<XmlLink> xmlLinks, int flag) {
		if (xmlLinks.size() == 0)
			return 0;
		if (myConnection == null) {
			Log.w(TAG, "[updateFlags] database connection not established");
			return -1;
		}
		long startTime = System.currentTimeMillis();
		String query = "UPDATE " + TABLE_NAME + " SET flag = " + flag + " WHERE ";
		for (XmlLink xmlLink : xmlLinks)
			query += "id = '" + xmlLink.id + "' or ";
		query = query.substring(0, query.length() - 4) + ";";
		if (tryUpdate(MAX_TRIES, query)) {
			Log.d(TAG, "[updateFlags] takes %f seconds to update %d flags",
					(double) (System.currentTimeMillis() - startTime) / 1000, xmlLinks.size());
			return 1;
		} else
			return -1;
	}

	public int updateUUID(XmlLink xmlLink) {
		if (myConnection == null) {
			Log.w(TAG, "[updateUUID] database connection not established");
			return -1;
		}
		long startTime = System.currentTimeMillis();
		String query = "UPDATE " + TABLE_NAME + " SET uuid = " + xmlLink.uuid + " WHERE id = '" + xmlLink.id + "';";
		if (tryUpdate(MAX_TRIES, query)) {
			Log.d(TAG, "[updateUUID] takes %f seconds to update 1 UUID",
					(double) (System.currentTimeMillis() - startTime) / 1000);
			return 1;
		} else
			return -1;
	}

	private boolean tryUpdate(int triesLeft, String query) {
		if (triesLeft > 0) {
			try {
				Statement st = (Statement) myConnection.createStatement();
				st.executeUpdate(query);
			} catch (com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException ex) {
				numberOfInsertErrors++;
				Log.e(TAG, "MySQLSyntaxErrorException, saved at query%03d.sql", numberOfInsertErrors);
				Utils.writeInfile(String.format("sql-queries/query%03d.sql", numberOfInsertErrors), query);
			} catch (SQLException e) {
				Log.w(TAG, "[tryUpdate] failed, %d tries left", triesLeft - 1);
				connect();
				tryUpdate(triesLeft - 1, query);
			}
			return true;
		} else {
			Log.e(TAG, "[tryUpdate] failed so many times");
			return false;
		}
	}

}
