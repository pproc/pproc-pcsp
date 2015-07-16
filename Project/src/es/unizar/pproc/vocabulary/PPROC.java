package es.unizar.pproc.vocabulary;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

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
	public static final Resource ContractEconomicConditions = resource("ContractEconomicConditions");
	public static final Resource BundlePriceSpecification = resource("BundlePriceSpecification");
	public static final Resource ContractProcedureSpecifications = resource("ContractProcedureSpecifications");
	public static final Resource Regular = resource("Regular");
	public static final Resource Express = resource("Express");
	public static final Resource Emergency = resource("Emergency");
	public static final Resource RegularOpen = resource("RegularOpen");
	public static final Resource SimpleOpen = resource("SimpleOpen");
	public static final Resource Restricted = resource("Restricted");
	public static final Resource Negotiated = resource("Negotiated");
	public static final Resource Minor = resource("Minor");
	public static final Resource CompetitiveDialogue = resource("CompetitiveDialogue");
	public static final Resource NegotiatedWithoutPublicity = resource("NegotiatedWithoutPublicity");
	public static final Resource NegotiatedWithPublicity = resource("NegotiatedWithPublicity");
	public static final Resource FrameworkConclusionContract = resource("FrameworkConclusionContract");
	public static final Resource DynamicPurchasingSystemConclusionContract = resource("DynamicPurchasingSystemConclusionContract");
	public static final Resource FrameworkDerivativeContract = resource("FrameworkDerivativeContract");
	public static final Resource DynamicPurchasingSystemDerivativeContract = resource("DynamicPurchasingSystemDerivativeContract");
	public static final Resource ConclusionContract = resource("ConclusionContract");
	public static final Resource DerivativeContract = resource("DerivativeContract");
	public static final Resource FrameworkAgreement = resource("FrameworkAgreement");
	public static final Resource ContractObject = resource("ContractObject");
	public static final Resource TenderersRequirements = resource("TenderersRequirements");
	public static final Resource SubjectiveAwardCriterion = resource("SubjectiveAwardCriterion");
	public static final Resource ObjectiveAwardCriterion = resource("ObjectiveAwardCriterion");
	public static final Resource ContractAdditionalObligations = resource("ContractAdditionalObligations");
	public static final Resource InformationProvider = resource("InformationProvider");
	public static final Resource TenderRequirements = resource("TenderRequirements");
	public static final Resource ContractActivities = resource("ContractActivities");
	public static final Resource TenderMeeting = resource("TenderMeeting");
	public static final Resource AwardedTender = resource("AwardedTender");
	public static final Resource FormalizedTender = resource("FormalizedTender");
	public static final Resource ContractOrProcedureExtinction = resource("ContractOrProcedureExtinction");
	public static final Resource ProcedureVoid = resource("ProcedureVoid");
	public static final Resource ContractResolution = resource("ContractResolution");
	public static final Resource ProcedureResignation = resource("ProcedureResignation");
	public static final Resource ProcedureWaive = resource("ProcedureWaive");
	public static final Resource ContractWithLots = resource("ContractWithLots");
	public static final Resource ContractWithoutLots = resource("ContractWithoutLots");
	public static final Resource Lot = resource("Lot");

	public static final Property delegatingAuthority = property("delegatingAuthority");
	public static final Property contractTemporalConditions = property("contractTemporalConditions");
	public static final Property estimatedDuration = property("estimatedDuration");
	public static final Property contractEconomicConditions = property("contractEconomicConditions");
	public static final Property estimatedValue = property("estimatedValue");
	public static final Property budgetPrice = property("budgetPrice");
	public static final Property feePrice = property("feePrice");
	public static final Property contractProcedureSpecifications = property("contractProcedureSpecifications");
	public static final Property urgencyType = property("urgencyType");
	public static final Property procedureType = property("procedureType");
	public static final Property frameworkAgreement = property("frameworkAgreement");
	public static final Property estimatedEndDate = property("estimatedEndDate");
	public static final Property maxNumberOfOperators = property("maxNumberOfOperators");
	public static final Property minNumberOfOperators = property("minNumberOfOperators");
	public static final Property contractObject = property("contractObject");
	public static final Property mainObject = property("mainObject");
	public static final Property provision = property("provision");
	public static final Property tenderersRequirements = property("tenderersRequirements");
	public static final Property requiredClassification = property("requiredClassification");
	public static final Property requiredEconomicAndFinancialStanding = property("requiredEconomicAndFinancialStanding");
	public static final Property requiredTechnicalAndProfessionalAbility = property("requiredTechnicalAndProfessionalAbility");
	public static final Property otherAbilityRequisites = property("otherAbilityRequisites");
	public static final Property criterionEvaluationMode = property("criterionEvaluationMode");
	public static final Property criterionMaxAndMinScores = property("criterionMaxAndMinScores");
	public static final Property contractAdditionalObligations = property("contractAdditionalObligations");
	public static final Property finalFinancialGuarantee = property("finalFinancialGuarantee");
	public static final Property finalFinancialGuaranteeDuration = property("finalFinancialGuaranteeDuration");
	public static final Property provisionalFinancialGuarantee = property("provisionalFinancialGuarantee");
	public static final Property otherGuarantee = property("otherGuarantee");
	public static final Property advertisementAmount = property("advertisementAmount");
	public static final Property tenderInformationProvider = property("tenderInformationProvider");
	public static final Property tenderDeadline = property("tenderDeadline");
	public static final Property tenderRequirements = property("tenderRequirements");
	public static final Property tenderDocumentNeeds = property("tenderDocumentNeeds");
	public static final Property tenderManteinanceDuration = property("tenderManteinanceDuration");
	public static final Property tenderSubmissionLocation = property("tenderSubmissionLocation");
	public static final Property tenderMeeting = property("tenderMeeting");
	public static final Property tenderPurpose = property("tenderPurpose");
	public static final Property contractActivities = property("contractActivities");
	public static final Property contractOrProcedureExtinction = property("contractOrProcedureExtinction");
	public static final Property procedureVoid = property("procedureVoid");
	public static final Property contractResolution = property("contractResolution");
	public static final Property procedureResignation = property("procedureResignation");
	public static final Property procedureWaive = property("procedureWaive");
	public static final Property extinctionCause = property("extinctionCause");
	public static final Property legalDocumentReference = property("legalDocumentReference");
	public static final Property technicalDocumentReference = property("technicalDocumentReference");
	public static final Property additionalDocumentReference = property("additionalDocumentReference");
	public static final Property location = property("location");
	public static final Property lot = property("lot");

}
