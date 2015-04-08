package es.unizar.pproc.codice;

public class XmlLink {

    public int id;
    public String link;
    public int flag;
    public String xml;
    public String uuid;

    public XmlLink(int id, String link, int flag, String xml, String uuid) {
        this.id = id;
        this.link = link;
        this.flag = flag;
        this.xml = xml;
        this.uuid = uuid;
    }

    public XmlLink(int id, String link, int flag, String uuid) {
        this.id = id;
        this.link = link;
        this.flag = flag;
        this.uuid = uuid;
    }

}
