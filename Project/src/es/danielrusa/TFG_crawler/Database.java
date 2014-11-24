package es.danielrusa.TFG_crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;

import javax.swing.JOptionPane;

public class Database {

	private Connection miConexion;

	public Database() {
		super();
		this.miConexion = getConnection();
		if (miConexion != null)
			System.out.printf("\n%s - Connected to database", ExtraerLicitaciones.getNow());
	}

	public void insertarLinkLicitacion(String enlace, String expediente, String xml, String post, String id) {
		String query = null;
		try {
			if (!xmlYaInsertado(enlace)) {
				Statement st = (Statement) miConexion.createStatement();
				query = "INSERT INTO newxml (link,expediente,xml,peticion,idplataforma) VALUES ('" + enlace.trim()
						+ "','" + expediente.replaceAll("'", "_").trim() + "','" + xml.trim() + "','" + post.trim()
						+ "','" + id + "')";
				st.executeUpdate(query);
			}
		} catch (SQLException e) {
			System.out.printf("\n%s - ERROR error en insertarLinkLicitacion", ExtraerLicitaciones.getNow());
			e.printStackTrace();
		}

	}

	private boolean xmlYaInsertado(String link) throws SQLException {

		Statement stmt = null;
		ResultSet rs = null;
		int rowCount = -1;
		try {
			stmt = miConexion.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(id) FROM newxml where link like '" + link.trim() + "'");
			rs.next();
			rowCount = rs.getInt(1);
		} catch (SQLException e) {
			System.out.printf("\n%s - ERROR error en xmlYaInsertado", ExtraerLicitaciones.getNow());
			e.printStackTrace();
		} finally {
			rs.close();
			stmt.close();
		}
		if (rowCount > 0)
			return true;
		else
			return false;
	}

	public boolean existeIdPlataforma(String id) throws SQLException {
		Statement stmt = null;
		ResultSet rs = null;
		int rowCount = -1;
		try {
			stmt = miConexion.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(id) FROM newxml where idplataforma like '" + id.trim() + "'");
			rs.next();
			rowCount = rs.getInt(1);
		} catch (SQLException e) {
			System.out.printf("\n%s - ERROR error en existeIdPlataforma", ExtraerLicitaciones.getNow());
			e.printStackTrace();
		} finally {
			rs.close();
			stmt.close();
		}
		if (rowCount > 0)
			return true;
		else
			return false;
	}

	public static Connection getConnection() {
		Connection conexion = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String servidor = "jdbc:mysql://155.210.104.14:3306/licitaciones";
			String usuarioDB = "carlos";
			String passwordDB = "020202";
			conexion = DriverManager.getConnection(servidor, usuarioDB, passwordDB);
		} catch (ClassNotFoundException ex) {
			JOptionPane.showMessageDialog(null, ex, "Error1 en la Conexión con la BD " + ex.getMessage(),
					JOptionPane.ERROR_MESSAGE);
			conexion = null;
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, ex, "Error2 en la Conexión con la BD " + ex.getMessage(),
					JOptionPane.ERROR_MESSAGE);
			conexion = null;
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex, "Error3 en la Conexión con la BD " + ex.getMessage(),
					JOptionPane.ERROR_MESSAGE);
			conexion = null;
		}
		return conexion;
	}

}
