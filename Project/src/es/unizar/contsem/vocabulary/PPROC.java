package es.unizar.contsem.vocabulary;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class PPROC {

	protected static final String uri = "http://contsem.unizar.es/def/sector-publico/pproc#";

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

	public static final Resource Contract = resource("Contract");
	public static final Resource SuppliesContract = resource("SuppliesContract");
	public static final Resource ServicesContract = resource("ServicesContract");
	public static final Resource WorksContract = resource("WorksContract");
	public static final Resource PublicServicesManagementContract = resource("PublicServicesManagementContract");
	public static final Resource PublicWorksConcessionContract = resource("PublicWorksConcessionContract");
	public static final Resource PublicPrivatePartnershipContract = resource("PublicPrivatePartnershipContract");
	public static final Resource SpecialAdministrativeContract = resource("SpecialAdministrativeContract");
	public static final Resource PrivateContract = resource("PrivateContract");
	public static final Resource RentContract = resource("RentContract");
	public static final Resource BuyContract = resource("BuyContract");
	public static final Resource ContractTemporalConditions = resource("ContractTemporalConditions");

	public static final Property delegatingAuthority = property("delegatingAuthority");
	public static final Property contractTemporalConditions = property("contractTemporalConditions");
	public static final Property estimatedDuration = property("estimatedDuration");

	/**
	 * The same items of vocabulary, but at the Node level, parked inside a
	 * nested class so that there's a simple way to refer to them.
	 */
	@SuppressWarnings("hiding")
	public static final class Nodes {
		public static final Node Contract = PPROC.Contract.asNode();
		public static final Node SuppliesContract = PPROC.SuppliesContract.asNode();
		public static final Node ServicesContract = PPROC.ServicesContract.asNode();
		public static final Node WorksContract = PPROC.WorksContract.asNode();
		public static final Node PublicServicesManagementContract = PPROC.PublicServicesManagementContract.asNode();
		public static final Node PublicWorksConcessionContract = PPROC.PublicWorksConcessionContract.asNode();
		public static final Node PublicPrivatePartnershipContract = PPROC.PublicPrivatePartnershipContract.asNode();
		public static final Node SpecialAdministrativeContract = PPROC.SpecialAdministrativeContract.asNode();
		public static final Node PrivateContract = PPROC.PrivateContract.asNode();
		public static final Node RentContract = PPROC.RentContract.asNode();
		public static final Node BuyContract = PPROC.BuyContract.asNode();
		public static final Node ContractTemporalConditions = PPROC.ContractTemporalConditions.asNode();

		public static final Node delegatingAuthority = PPROC.delegatingAuthority.asNode();
		public static final Node contractTemporalConditions = PPROC.contractTemporalConditions.asNode();
		public static final Node estimatedDuration = PPROC.estimatedDuration.asNode();
	}

}
