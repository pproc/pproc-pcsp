package es.danielrusa.TFG_crawler;

public class Spider {

	public static void main(String[] args) {

		Estadisticas estadisticas = new Estadisticas();
		Database bd = new Database();
		ExtraerLicitaciones el = new ExtraerLicitaciones();
		el.start(bd, "https://contrataciondelestado.es/wps/portal/", estadisticas);
	}
}
