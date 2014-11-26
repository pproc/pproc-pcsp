package es.danielrusa.TFG_crawler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

public class Database {

	private Connection miConexion;
	private Set<Row> rowSet = new HashSet<Row>();
	private Set<String> idplataformaSet = null;
	private Set<String> linkSet = null;

	public Database() {
		super();
		this.miConexion = getConnection();
		if (miConexion != null)
			System.out.printf("\n%s - INFO Connected to database", ExtraerLicitaciones.getNow());
	}

	public void insertXML(String link, String expediente, String xml, String post, String idplataforma) {
		if (rowSet.size() > 10) {
			insertQueue();
			rowSet = new HashSet<Row>();
			idplataformaSet.add(idplataforma);
			linkSet.add(xml);
		} else {
			rowSet.add(new Row(link, expediente, xml, post, idplataforma));
		}
	}

	public boolean linkExists(String link) throws SQLException {

		if (linkSet == null) {
			long startTime = System.currentTimeMillis();
			linkSet = new HashSet<String>();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = miConexion.createStatement();
				rs = stmt.executeQuery("SELECT link FROM newxml");
				while (rs.next()) {
					linkSet.add(rs.getString("link"));
				}
			} catch (SQLException e) {
				System.out.printf("\n%s - ERROR error en linkExists", ExtraerLicitaciones.getNow());
				e.printStackTrace();
			} finally {
				rs.close();
				stmt.close();
			}
			System.out.printf("\n%s - INFO inicialización de linkExists tarda %f", ExtraerLicitaciones.getNow(),
					(double) (System.currentTimeMillis() - startTime) / 1000);
			System.out.printf("\n%s - INFO encontrados %d link en base de datos", ExtraerLicitaciones.getNow(),
					linkSet.size());
		}

		return linkSet.contains(link);
	}

	public boolean idplataformExists(String idplataforma) throws SQLException {

		if (idplataformaSet == null) {
			long startTime = System.currentTimeMillis();
			idplataformaSet = new HashSet<String>();
			Statement stmt = null;
			ResultSet rs = null;
			try {
				stmt = miConexion.createStatement();
				rs = stmt.executeQuery("SELECT idplataforma FROM newxml");
				while (rs.next()) {
					idplataformaSet.add(rs.getString("idplataforma"));
				}
			} catch (SQLException e) {
				System.out.printf("\n%s - ERROR error en idplataformExists", ExtraerLicitaciones.getNow());
				e.printStackTrace();
			} finally {
				rs.close();
				stmt.close();
			}
			System.out.printf("\n%s - INFO inicialización de idplataformExists tarda %f", ExtraerLicitaciones.getNow(),
					(double) (System.currentTimeMillis() - startTime) / 1000);
			System.out.printf("\n%s - INFO encontrados %d idplataforma en base de datos", ExtraerLicitaciones.getNow(),
					idplataformaSet.size());
		}

		return idplataformaSet.contains(idplataforma);
	}

	private void insertQueue() {
		long startTime = System.currentTimeMillis();
		String query = "INSERT INTO newxml (link,expediente,xml,peticion,idplataforma) VALUES ";
		for (Row row : rowSet) {
			try {
				if (!linkExists(row.link)) {
					query += "('" + row.link.trim() + "','" + row.expediente.replaceAll("'", "_").trim() + "','"
							+ row.xml.trim() + "','" + row.post.trim() + "','" + row.idplataforma + "'),";
				}
			} catch (SQLException e) {
				System.out.printf("\n%s - ERROR error en insertQueue", ExtraerLicitaciones.getNow());
				e.printStackTrace();
			}
		}
		query = query.substring(0, query.length() - 1) + ";";
		try {
			Statement st = (Statement) miConexion.createStatement();
			st.executeUpdate(query);
		} catch (SQLException e) {
			System.out.printf("\n%s - ERROR error en insertQueue", ExtraerLicitaciones.getNow());
			e.printStackTrace();
		}
		System.out.printf("\n%s - DEBUG insertQueue tarda %f", ExtraerLicitaciones.getNow(),
				(double) (System.currentTimeMillis() - startTime) / 1000);
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
