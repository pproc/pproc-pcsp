package es.danielrusa.TFG_crawler;

public class Spider {

	public static void main(String[] args) {

		Estadisticas statistics = new Estadisticas();
		Database database = new Database();
		database.connect();
		ExtraerLicitaciones el = new ExtraerLicitaciones();
		el.start(database, "https://contrataciondelestado.es/wps/portal/", statistics);
	}
}
