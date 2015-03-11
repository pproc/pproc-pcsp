package es.unizar.contsem.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class SKOS {

	protected static final String uri = "http://www.w3.org/2004/02/skos/core#";

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

    public static final Resource Concept = resource("Concept");

    public static final Property prefLabel = property("prefLabel");
    public static final Property notation = property("notation");

}
