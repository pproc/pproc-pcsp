package es.unizar.contsem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import es.unizar.contsem.crawler.XmlLink;

/**
 * Simple database class. This class assume the existence of a table named {@value #TABLE_NAME} with two columns:
 * <ul>
 * <li><b>id</b>: <i>auto-increment primary_key</i> integer</li>
 * <li><b>link</b>: medium_text</li>
 * <li><b>flag</b>: integer</li>
 * <li><b>xml</b>: longtext</li>
 * </ul>
 * 
 * @author gesteban
 *
 */
public class Database {

    private String TABLE_NAME = "XmlLinks";
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
            Log.error(Database.class, "[getMySQLConnection] error while connecting to database : %s", ex.getMessage());
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
            Log.info(this.getClass(), "[connect] connected to database");
        else
            Log.error(this.getClass(), "[connect] could not provide connection");
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

    public Set<XmlLink> getLinksById(int flag, int minId, int maxId) {
        if (myConnection == null) {
            Log.error(this.getClass(), "[getLinksByFlag] database connection not established");
            return null;
        }
        long startTime = System.currentTimeMillis();
        Statement stmt = null;
        Set<XmlLink> xmlLinks = new HashSet<XmlLink>();
        try {
            stmt = myConnection.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT id, link, flag, xml FROM " + TABLE_NAME + " WHERE flag = " + flag
                    + " and id>=" + minId + " and id<" + maxId);
            while (rs.next())
                xmlLinks.add(new XmlLink(rs.getInt("id"), rs.getString("link"), rs.getInt("flag"), rs.getString("xml")));
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Log.info(this.getClass(), "[getLinksByFlag] takes %f seconds",
                (double) (System.currentTimeMillis() - startTime) / 1000);
        return xmlLinks;
    }

    public Set<XmlLink> getLinksByFlag(int flag, int limit) {
        if (myConnection == null) {
            Log.error(this.getClass(), "[getLinksByFlag] database connection not established");
            return null;
        }
        long startTime = System.currentTimeMillis();
        Statement stmt = null;
        Set<XmlLink> xmlLinks = new HashSet<XmlLink>();
        try {
            stmt = myConnection.createStatement();
            String limit_str = (limit > 0 ? " limit " + limit : "");
            ResultSet rs = stmt.executeQuery("SELECT id, link, flag, xml FROM " + TABLE_NAME + " WHERE flag = " + flag
                    + limit_str);
            while (rs.next())
                xmlLinks.add(new XmlLink(rs.getInt("id"), rs.getString("link"), rs.getInt("flag"), rs.getString("xml")));
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Log.info(this.getClass(), "[getLinksByFlag] takes %f seconds",
                (double) (System.currentTimeMillis() - startTime) / 1000);
        return xmlLinks;
    }

    public int insertLinks(Set<String> links) {
        if (links.size() == 0)
            return 1;
        if (myConnection == null) {
            Log.error(this.getClass(), "[insertLinks] database connection not established");
            return 0;
        }
        long startTime = System.currentTimeMillis();
        String query = "INSERT INTO " + TABLE_NAME + " (link,flag,xml) VALUES ";
        for (String link : links)
            query += "('" + link + "',0,''),";
        query = query.substring(0, query.length() - 1) + ";";
        if (tryUpdate(MAX_TRIES, query)) {
            Log.info(this.getClass(), "[insertLinks] takes %f seconds to insert %d links",
                    (double) (System.currentTimeMillis() - startTime) / 1000, links.size());
            return 1;
        } else
            return 0;
    }

    public int updateXml(XmlLink xmlLink) {
        if (myConnection == null) {
            Log.error(this.getClass(), "[updateXmls] database connection not established");
            return -1;
        }
        long startTime = System.currentTimeMillis();
        String query = "UPDATE " + TABLE_NAME + " SET xml = '" + xmlLink.xml.replace("'", "''") + "' WHERE id = "
                + xmlLink.id + ";";
        if (tryUpdate(MAX_TRIES, query)) {
            Log.info(this.getClass(), "[updateXmls] takes %f seconds to update 1 xml",
                    (double) (System.currentTimeMillis() - startTime) / 1000);
            return 1;
        } else
            return 0;
    }

    public int updateXmls(Set<XmlLink> xmlLinks) {
        if (xmlLinks.size() == 0)
            return 0;
        if (myConnection == null) {
            Log.error(this.getClass(), "[updateXmls] database connection not established");
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
                Log.error(this.getClass(), "[updateXmls] xml %d extraction failed", xmlLink.id);
        }
        Log.info(this.getClass(), "[updateXmls] takes %f seconds to update %d xmls",
                (double) (System.currentTimeMillis() - startTime) / 1000, xmlLinks.size());
        return updateCount;
    }

    public int updateFlags(Set<XmlLink> xmlLinks, int flag) {
        if (xmlLinks.size() == 0)
            return 0;
        if (myConnection == null) {
            Log.warning(this.getClass(), "[updateFlags] database connection not established");
            return -1;
        }
        long startTime = System.currentTimeMillis();
        String query = "UPDATE " + TABLE_NAME + " SET flag = " + flag + " WHERE ";
        for (XmlLink xmlLink : xmlLinks)
            query += "id='" + xmlLink.id + "' or ";
        query = query.substring(0, query.length() - 4) + ";";
        if (tryUpdate(MAX_TRIES, query)) {
            Log.info(this.getClass(), "[updateFlags] takes %f seconds to update %d flags",
                    (double) (System.currentTimeMillis() - startTime) / 1000, xmlLinks.size());
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
                Log.error(this.getClass(), "MySQLSyntaxErrorException, saved at query%03d.sql", numberOfInsertErrors);
                Utils.writeInfile(String.format("sql-queries/query%03d.sql", numberOfInsertErrors), query);
            } catch (SQLException e) {
                Log.warning(this.getClass(), "[tryUpdate] failed, %d tries left", triesLeft - 1);
                connect();
                tryUpdate(triesLeft - 1, query);
            }
            return true;
        } else {
            Log.error(this.getClass(), "[tryUpdate] failed so many times");
            return false;
        }
    }

}
