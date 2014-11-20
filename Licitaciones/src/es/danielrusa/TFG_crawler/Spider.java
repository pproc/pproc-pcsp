package es.danielrusa.TFG_crawler;
import java.util.HashMap;
import java.util.Map;

public class Spider {

	public static void main(String[] args) {
		
		// Licitacion y descripcion
		HashMap<String, String> licitaciones = new HashMap<String, String>();
		// Licitacion y enlace XML
		HashMap<String, String> enlaceXml = new HashMap<String, String>();
		// Enlace XML y contenido XML
		HashMap<String, String> xml = new HashMap<String, String>();
		Map<String, String> cookies = null;

		Estadisticas estadisticas = new Estadisticas();

		BaseDatos bd = new BaseDatos();

		String base = "https://contrataciondelestado.es";
		String url = "https://contrataciondelestado.es/wps/portal/";

		ExtraerLicitaciones el = new ExtraerLicitaciones();

		System.out.println(el.accederPrimeraPaginaLicitaciones(bd, base, url,
				cookies, estadisticas));

	}

}
