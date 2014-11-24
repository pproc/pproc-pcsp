package es.danielrusa.TFG_crawler;

public class Spider {

	public static void main(String[] args) {

		Estadisticas estadisticas = new Estadisticas();
		Database bd = new Database();
		String url = "https://contrataciondelestado.es/wps/portal/";
		ExtraerLicitaciones el = new ExtraerLicitaciones();
		el.start(bd, url, estadisticas);
	}
}
