package es.danielrusa.TFG_crawler;

public class Row {

	public String link;
	public String expediente;
	public String xml;
	public String post;
	public String idplataforma;

	public Row(String link, String expediente, String xml, String post, String idplataforma) {
		super();
		this.link = link;
		this.expediente = expediente;
		this.xml = xml;
		this.post = post;
		this.idplataforma = idplataforma;
	}

}
