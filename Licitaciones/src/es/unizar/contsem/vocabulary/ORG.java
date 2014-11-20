package es.unizar.contsem.vocabulary;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class ORG {

	protected static final String uri = "http://www.w3.org/ns/org#";

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

	public static final Resource Organization = resource("Organization");

	public static final Property hasSite = property("hasSite");

	/**
	 * The same items of vocabulary, but at the Node level, parked inside a
	 * nested class so that there's a simple way to refer to them.
	 */
	@SuppressWarnings("hiding")
	public static final class Nodes {
		public static final Node Organization = ORG.Organization.asNode();
		public static final Node hasSite = ORG.hasSite.asNode();
	}

}
