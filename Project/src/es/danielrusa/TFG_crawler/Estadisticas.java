package es.danielrusa.TFG_crawler;

import java.util.HashMap;
import java.util.Map;

public class Estadisticas {

	private int paginasVisitadas = 0;
	private int enlacesCapturados = 0;

	// XML link and content
	private Map<String, String> xml = new HashMap<String, String>();

	public int getPaginasVisitadas() {
		return paginasVisitadas;
	}

	public int getEnlacesCapturados() {
		return enlacesCapturados;
	}

	public Map<String, String> getXml() {
		return xml;
	}

	public void setXml(Map<String, String> xml) {
		this.xml = xml;
	}

	public void incrementarPaginasVisitadas() {
		paginasVisitadas++;
	}

	public void incrementarEnlacesCapturados() {
		enlacesCapturados++;
	}

	public void añadirEnlace(String link, String descripcion) {
		this.xml.put(link, descripcion);
	}

}
