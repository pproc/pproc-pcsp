package es.danielrusa.TFG_crawler;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseDatos {

	Connection miConexion;

	public BaseDatos() {
		super();
		this.miConexion = ConexionDB.GetConnection();

		if (miConexion != null) {
			System.out
					.println("Conectado a Base de datos correctamente...........");
			// JOptionPane.showMessageDialog(null,
			// "Conexión Realizada Correctamente");
		}
	} // fin constructor

	public void insertarLinkLicitacion(String link, String expediente,
			String xml, String post) {
		String query = null;
		try {
			if (!xmlYaInsertado(link)) {
				Statement st = (Statement) miConexion.createStatement();
				query = "INSERT INTO xml (link,expediente,xml,peticion) VALUES ('"
						+ link.trim()
						+ "','"
						+ expediente.replaceAll("'", "_").trim()
						+ "','"
						+ xml.trim() + "','" + post.trim() + "')";
				st.executeUpdate(query);
			} else {
				System.out
						.println("Licitacion ["
								+ link
								+ "].................................ya insertada en BBDD");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			query = "INSERT INTO noinsertadas (query) VALUES ('" + query + "')";
			System.out.println("\nNo se pudo ejecutar la consulta:");
			System.out.println(query + "\n");
			e.printStackTrace();
		}

	}

	private boolean xmlYaInsertado(String link) throws SQLException {

		Statement stmt = null;
		ResultSet rs = null;
		int rowCount = -1;
		try {
			stmt = miConexion.createStatement();
			rs = stmt
					.executeQuery("SELECT COUNT(id) FROM xml where link like '"
							+ link.trim() + "'");
			// get the number of rows from the result set
			rs.next();
			rowCount = rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
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

}
