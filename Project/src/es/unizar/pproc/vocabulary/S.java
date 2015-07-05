package es.unizar.pproc.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class S {

	protected static final String uri = "http://schema.org/";

	/**
	 * returns the URI for this schema
	 * 
	 * @return the URI for this schema
	 */
	public static String getURI() {
		return uri;
	}

	protected static final Resource resource(String local) {
		return ResourceFactory.createResource(uri + local);
	}

	protected static final Property property(String local) {
		return ResourceFactory.createProperty(uri, local);
	}

	public static final Resource Place = resource("Place");
	public static final Resource PostalAddress = resource("PostalAddress");
	public static final Resource Event = resource("Event");

	public static final Property name = property("name");
	public static final Property address = property("address");
	public static final Property streetAddress = property("streetAddress");
	public static final Property postalCode = property("postalCode");
	public static final Property addressLocality = property("addressLocality");
	public static final Property addressCountry = property("addressCountry");
	public static final Property telephone = property("telephone");
	public static final Property faxNumber = property("faxNumber");
	public static final Property email = property("email");
	public static final Property location = property("location");
	public static final Property startDate = property("startDate");

}