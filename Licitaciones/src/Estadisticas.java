import java.util.HashMap;


public class Estadisticas {

	private int paginasVisitadas;
	private int enlacesCapturados;
	
	HashMap<String,String> xml = new HashMap<String,String>();  // Enlace XML y contenido XML

	public int getPaginasVisitadas() {
		return paginasVisitadas;
	}

	public void setPaginasVisitadas(int paginasVisitadas) {
		this.paginasVisitadas = paginasVisitadas;
	}

	public int getEnlacesCapturados() {
		return enlacesCapturados;
	}

	public void setEnlacesCapturados(int enlacesCapturados) {
		this.enlacesCapturados = enlacesCapturados;
	}

	public HashMap<String, String> getXml() {
		return xml;
	}

	public void setXml(HashMap<String, String> xml) {
		this.xml = xml;
	}
	
	
	public int incrementarPaginasVisitadas(){
		return ++paginasVisitadas;
	}
	
	public int incrementarEnlacesCapturados(){
		return ++enlacesCapturados;
	}
	
	public void añadirEnlace(String link,String descripcion){
		this.xml.put(link, descripcion);
	}
	
}
