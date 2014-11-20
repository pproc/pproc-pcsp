package es.unizar.contsem.vocabulary;

import com.hp.hpl.jena.graph.Node;
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

		public static final Property name = property("name");
		public static final Property address = property("address");
		public static final Property streetAddress = property("streetAddress");
		public static final Property postalCode = property("postalCode");
		public static final Property addressLocality = property("addressLocality");
		public static final Property addressCountry = property("addressCountry");
		public static final Property telephone = property("telephone");
		public static final Property faxNumber = property("faxNumber");
		public static final Property email = property("email");
		

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