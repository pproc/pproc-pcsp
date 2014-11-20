package es.unizar.contsem.codice.parser;

import java.util.Iterator;

import org.apache.jena.riot.RDFDataMgr;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;

import es.unizar.contsem.vocabulary.ORG;
import es.unizar.contsem.vocabulary.PC;
import es.unizar.contsem.vocabulary.PPROC;
import es.unizar.contsem.vocabulary.S;

public class CodiceToPprocParser {

	private static String BASE_URI_CONTRATO = "http://contsem.unizar.es/datos/sector-publico/contrato/";
	private static String BASE_URI_ORGANIZATION = "http://contsem.unizar.es/datos/sector-publico/organization/";

	/**
	 * 
	 * @param model
	 *            RDF Jena Model
	 * @param document
	 *            CODICE XML document
	 */
	public static void parseCodiceXML(Model model, Document document) {

		String altString = null, altString2 = null;
		Resource altResource = null;
		Element altElement = null;

		// pproc:Contract rdf:type (1)
		String contractInstanceURI = BASE_URI_CONTRATO + document.getRootElement().elementText("UUID");
		Resource contractInstance = model.createResource(contractInstanceURI);
		contractInstance.addProperty(RDF.type, PPROC.Contract);

		// pproc:Contract dcterms:title
		if ((altString = document.getRootElement().element("ProcurementProject").elementText("Name")) != null)
			contractInstance.addProperty(DCTerms.title, altString);

		// pproc:Contract dcterms:description
		if ((altString = document.getRootElement().element("ProcurementProject").elementText("Description")) != null)
			contractInstance.addProperty(DCTerms.description, altString);

		// pproc:Contract rdf:type (2)
		// TODO parseo independiente de versión (parseo del .gc)
		if ((altString = document.getRootElement().element("ProcurementProject").elementText("TypeCode")) != null)
			switch (altString) {
			case "1":
				contractInstance.addProperty(RDF.type, PPROC.SuppliesContract);
				break;
			case "2":
				contractInstance.addProperty(RDF.type, PPROC.ServicesContract);
				break;
			case "3":
				contractInstance.addProperty(RDF.type, PPROC.WorksContract);
				break;
			case "21":
				contractInstance.addProperty(RDF.type, PPROC.PublicServicesManagementContract);
				break;
			case "31":
				contractInstance.addProperty(RDF.type, PPROC.PublicWorksConcessionContract);
				break;
			case "40":
				contractInstance.addProperty(RDF.type, PPROC.PublicPrivatePartnershipContract);
				break;
			case "7":
				contractInstance.addProperty(RDF.type, PPROC.SpecialAdministrativeContract);
				break;
			case "8":
				contractInstance.addProperty(RDF.type, PPROC.PrivateContract);
				break;
			case "50":
				// Contrato patrimonial, no hay equivalencia en PPROC
			}

		// pproc:Contract rdf:type (3)
		// TODO parseo independiente de versión (parseo del .gc)
		if (model.containsResource(PPROC.SuppliesContract))
			if ((altString = document.getRootElement().element("ProcurementProject").elementText("SubTypeCode")) != null)
				switch (altString) {
				case "1":
					contractInstance.addProperty(RDF.type, PPROC.RentContract);
					break;
				case "2":
					contractInstance.addProperty(RDF.type, PPROC.BuyContract);
				}

		// pproc:Contract dcterms:identifier
		if ((altString = document.getRootElement().elementText("ContractFolderID")) != null)
			contractInstance.addProperty(DCTerms.identifier, altString);

		// pproc:Contract pc:contractingAuthority
		for (Iterator iter = document.getRootElement().element("ContractingParty").element("Party")
				.elementIterator("PartyIdentification"); iter.hasNext();) {
			altElement = (Element) iter.next();
			if (altElement.element("ID").attributeValue("schemeName").equals("ID_PLATAFORMA"))
				altString = altElement.elementText("ID");
			else if (altElement.element("ID").attributeValue("schemeName").equals("NIF"))
				altString2 = altElement.elementText("ID");

		}
		String organizationInstanceURI = BASE_URI_ORGANIZATION + altString;
		if (!model.containsResource(ResourceFactory.createResource(organizationInstanceURI))) {

			// org:Organization rdf:type
			Resource organizationInstance = model.createResource(organizationInstanceURI);
			organizationInstance.addProperty(RDF.type, ORG.Organization);

			// org:Organization dcterms:title
			if ((altString = document.getRootElement().element("ContractingParty").element("Party")
					.element("PartyName").elementText("Name")) != null)
				organizationInstance.addProperty(DCTerms.title, altString);

			// org:Organization dcterms:identifier
			if (altString2 != null)
				organizationInstance.addProperty(DCTerms.identifier, altString2);

			// org:Organization org:hasSite
			Resource placeInstance = model.createResource(organizationInstanceURI + "/Site");
			placeInstance.addProperty(RDF.type, S.Place);
			organizationInstance.addProperty(ORG.hasSite, placeInstance);

			// s:address s:PostalAddress
			Resource postalAddressInstance = model.createResource(organizationInstanceURI + "/PostalAddress");
			postalAddressInstance.addProperty(RDF.type, S.PostalAddress);
			placeInstance.addProperty(S.address, postalAddressInstance);
			if ((altString = document.getRootElement().element("ContractingParty").element("Party")
					.element("PostalAddress").elementText("CityName")) != null)
				postalAddressInstance.addProperty(S.addressLocality, altString);
			if ((altString = document.getRootElement().element("ContractingParty").element("Party")
					.element("PostalAddress").elementText("PostalZone")) != null)
				postalAddressInstance.addProperty(S.postalCode, altString);
			if ((altString = document.getRootElement().element("ContractingParty").element("Party")
					.element("PostalAddress").element("AddressLine").elementText("Line")) != null)
				postalAddressInstance.addProperty(S.streetAddress, altString);
			if ((altString = document.getRootElement().element("ContractingParty").element("Party")
					.element("PostalAddress").element("Country").elementText("IdentificationCode")) != null)
				postalAddressInstance.addProperty(S.addressCountry, altString);

			// org:Organization s:telephone
			if ((altString = document.getRootElement().element("ContractingParty").element("Party").element("Contact")
					.elementText("Telephone")) != null)
				postalAddressInstance.addProperty(S.telephone, altString);

			// org:Organization s:faxNumber
			if ((altString = document.getRootElement().element("ContractingParty").element("Party").element("Contact")
					.elementText("Telefax")) != null)
				postalAddressInstance.addProperty(S.faxNumber, altString);

			// org:Organization s:email
			if ((altString = document.getRootElement().element("ContractingParty").element("Party").element("Contact")
					.elementText("ElectronicMail")) != null)
				postalAddressInstance.addProperty(S.email, altString);

		}
		contractInstance.addProperty(PC.contractingAuthority, organizationInstanceURI);

		// pproc:Contract pproc:delegatingAuthority
		altString2 = null;
		if (document.getRootElement().element("OriginatorCustomerParty") != null) {
			for (Iterator iter = document.getRootElement().element("OriginatorCustomerParty").element("Party")
					.elementIterator("PartyIdentification"); iter.hasNext();) {
				altElement = (Element) iter.next();
				if (altElement.element("ID").attributeValue("schemeName").equals("ID_PLATAFORMA"))
					altString = altElement.elementText("ID");
				else if (altElement.element("ID").attributeValue("schemeName").equals("NIF"))
					altString2 = altElement.elementText("ID");

			}
			organizationInstanceURI = BASE_URI_ORGANIZATION + altString;
			if (!model.containsResource(ResourceFactory.createResource(organizationInstanceURI))) {

				// org:Organization rdf:type
				Resource organizationInstance = model.createResource(organizationInstanceURI);
				organizationInstance.addProperty(RDF.type, ORG.Organization);

				// org:Organization dcterms:title
				if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
						.element("PartyName").elementText("Name")) != null)
					organizationInstance.addProperty(DCTerms.title, altString);

				// org:Organization dcterms:identifier
				if (altString2 != null)
					organizationInstance.addProperty(DCTerms.identifier, altString2);

				// org:Organization org:hasSite
				Resource placeInstance = model.createResource(organizationInstanceURI + "/Site");
				placeInstance.addProperty(RDF.type, S.Place);
				organizationInstance.addProperty(ORG.hasSite, placeInstance);

				// s:address s:PostalAddress
				Resource postalAddressInstance = model.createResource(organizationInstanceURI + "/PostalAddress");
				postalAddressInstance.addProperty(RDF.type, S.PostalAddress);
				placeInstance.addProperty(S.address, postalAddressInstance);
				if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
						.element("PostalAddress").elementText("CityName")) != null)
					postalAddressInstance.addProperty(S.addressLocality, altString);
				if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
						.element("PostalAddress").elementText("PostalZone")) != null)
					postalAddressInstance.addProperty(S.postalCode, altString);
				if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
						.element("PostalAddress").element("AddressLine").elementText("Line")) != null)
					postalAddressInstance.addProperty(S.streetAddress, altString);
				if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
						.element("PostalAddress").element("Country").elementText("IdentificationCode")) != null)
					postalAddressInstance.addProperty(S.addressCountry, altString);

				// org:Organization s:telephone
				if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
						.element("Contact").elementText("Telephone")) != null)
					postalAddressInstance.addProperty(S.telephone, altString);

				// org:Organization s:faxNumber
				if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
						.element("Contact").elementText("Telefax")) != null)
					postalAddressInstance.addProperty(S.faxNumber, altString);

				// org:Organization s:email
				if ((altString = document.getRootElement().element("OriginatorCustomerParty").element("Party")
						.element("Contact").elementText("ElectronicMail")) != null)
					postalAddressInstance.addProperty(PPROC.delegatingAuthority, altString);

			}
			contractInstance.addProperty(PC.contractingAuthority, organizationInstanceURI);
		}

		// pproc:Contract pproc:contractTemporalConditions
		Resource ctcInstance = model.createResource(contractInstanceURI + "/ContractTemporalConditions");
		ctcInstance.addProperty(RDF.type, PPROC.ContractTemporalConditions);
		contractInstance.addProperty(PPROC.contractTemporalConditions, ctcInstance);

		// pproc:ContractTemporalConditions pproc:estimatedDuration
		if ((altString = document.getRootElement().element("ProcurementProject").element("PlannedPeriod")
				.elementText("DurationMeasure")) != null
				&& (altString2 = document.getRootElement().element("ProcurementProject").element("PlannedPeriod")
						.element("DurationMeasure").attributeValue("unitCode")) != null) {
			switch (altString2) {
			case "DAY":
				ctcInstance.addProperty(PPROC.estimatedDuration, "P" + altString + "D");
				break;
			case "MON":
				ctcInstance.addProperty(PPROC.estimatedDuration, "P" + altString + "M");
				break;
			case "ANN":
				ctcInstance.addProperty(PPROC.estimatedDuration, "P" + altString + "Y");
				break;
			}
		}
		
		// TODO pproc:ContractTemporalConditions pproc:estimatedEndDate (falta mirar como es udt:DateType)
		
		// pproc:Contract pproc:contractEconomicConditions
		

		model.write(System.out, "Turtle");
	}
	
	// TODO añadir los datatype a las propiedades data

}
