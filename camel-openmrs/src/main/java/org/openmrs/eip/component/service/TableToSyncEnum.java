package org.openmrs.eip.component.service;

import org.openmrs.eip.component.entity.Allergy;
import org.openmrs.eip.component.entity.BaseEntity;
import org.openmrs.eip.component.entity.ClinicalSummaryUsageReport;
import org.openmrs.eip.component.entity.Concept;
import org.openmrs.eip.component.entity.ConceptAttribute;
import org.openmrs.eip.component.entity.Condition;
import org.openmrs.eip.component.entity.DrugOrder;
import org.openmrs.eip.component.entity.Encounter;
import org.openmrs.eip.component.entity.EncounterDiagnosis;
import org.openmrs.eip.component.entity.EncounterProvider;
import org.openmrs.eip.component.entity.Gaac;
import org.openmrs.eip.component.entity.GaacFamily;
import org.openmrs.eip.component.entity.GaacFamilyMember;
import org.openmrs.eip.component.entity.GaacMember;
import org.openmrs.eip.component.entity.Location;
import org.openmrs.eip.component.entity.LocationAttribute;
import org.openmrs.eip.component.entity.Observation;
import org.openmrs.eip.component.entity.Order;
import org.openmrs.eip.component.entity.Patient;
import org.openmrs.eip.component.entity.PatientIdentifier;
import org.openmrs.eip.component.entity.PatientProgram;
import org.openmrs.eip.component.entity.PatientState;
import org.openmrs.eip.component.entity.Person;
import org.openmrs.eip.component.entity.PersonAddress;
import org.openmrs.eip.component.entity.PersonAttribute;
import org.openmrs.eip.component.entity.PersonName;
import org.openmrs.eip.component.entity.Provider;
import org.openmrs.eip.component.entity.ProviderAttribute;
import org.openmrs.eip.component.entity.Relationship;
import org.openmrs.eip.component.entity.TestOrder;
import org.openmrs.eip.component.entity.User;
import org.openmrs.eip.component.entity.Visit;
import org.openmrs.eip.component.entity.VisitAttribute;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.management.hash.entity.AllergyHash;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.management.hash.entity.ClinicalSummaryUsageReportHash;
import org.openmrs.eip.component.management.hash.entity.ConditionHash;
import org.openmrs.eip.component.management.hash.entity.DrugOrderHash;
import org.openmrs.eip.component.management.hash.entity.EncounterDiagnosisHash;
import org.openmrs.eip.component.management.hash.entity.EncounterHash;
import org.openmrs.eip.component.management.hash.entity.EncounterProviderHash;
import org.openmrs.eip.component.management.hash.entity.GaacFamilyHash;
import org.openmrs.eip.component.management.hash.entity.GaacFamilyMemberHash;
import org.openmrs.eip.component.management.hash.entity.GaacHash;
import org.openmrs.eip.component.management.hash.entity.GaacMemberHash;
import org.openmrs.eip.component.management.hash.entity.ObsHash;
import org.openmrs.eip.component.management.hash.entity.OrderHash;
import org.openmrs.eip.component.management.hash.entity.PatientHash;
import org.openmrs.eip.component.management.hash.entity.PatientIdentifierHash;
import org.openmrs.eip.component.management.hash.entity.PatientProgramHash;
import org.openmrs.eip.component.management.hash.entity.PatientStateHash;
import org.openmrs.eip.component.management.hash.entity.PersonAddressHash;
import org.openmrs.eip.component.management.hash.entity.PersonAttributeHash;
import org.openmrs.eip.component.management.hash.entity.PersonHash;
import org.openmrs.eip.component.management.hash.entity.PersonNameHash;
import org.openmrs.eip.component.management.hash.entity.ProviderHash;
import org.openmrs.eip.component.management.hash.entity.RelationshipHash;
import org.openmrs.eip.component.management.hash.entity.TestOrderHash;
import org.openmrs.eip.component.management.hash.entity.UserHash;
import org.openmrs.eip.component.management.hash.entity.VisitAttributeHash;
import org.openmrs.eip.component.management.hash.entity.VisitHash;
import org.openmrs.eip.component.model.AllergyModel;
import org.openmrs.eip.component.model.AttributeModel;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.ClinicalSummaryUsageReportModel;
import org.openmrs.eip.component.model.ConceptAttributeModel;
import org.openmrs.eip.component.model.ConceptModel;
import org.openmrs.eip.component.model.ConditionModel;
import org.openmrs.eip.component.model.DrugOrderModel;
import org.openmrs.eip.component.model.EncounterDiagnosisModel;
import org.openmrs.eip.component.model.EncounterModel;
import org.openmrs.eip.component.model.EncounterProviderModel;
import org.openmrs.eip.component.model.GaacFamilyMemberModel;
import org.openmrs.eip.component.model.GaacFamilyModel;
import org.openmrs.eip.component.model.GaacMemberModel;
import org.openmrs.eip.component.model.GaacModel;
import org.openmrs.eip.component.model.LocationModel;
import org.openmrs.eip.component.model.ObservationModel;
import org.openmrs.eip.component.model.OrderModel;
import org.openmrs.eip.component.model.PatientIdentifierModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PatientProgramModel;
import org.openmrs.eip.component.model.PatientStateModel;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.PersonNameModel;
import org.openmrs.eip.component.model.ProviderModel;
import org.openmrs.eip.component.model.RelationshipModel;
import org.openmrs.eip.component.model.TestOrderModel;
import org.openmrs.eip.component.model.UserModel;
import org.openmrs.eip.component.model.VisitAttributeModel;
import org.openmrs.eip.component.model.VisitModel;

import java.util.Arrays;
import java.util.stream.Stream;

public enum TableToSyncEnum {
	
	PERSON(Person.class, PersonModel.class, PersonHash.class),
	
	PATIENT(Patient.class, PatientModel.class, PatientHash.class),
	
	VISIT(Visit.class, VisitModel.class, VisitHash.class),
	
	ENCOUNTER(Encounter.class, EncounterModel.class, EncounterHash.class),
	
	OBS(Observation.class, ObservationModel.class, ObsHash.class),
	
	PERSON_ATTRIBUTE(PersonAttribute.class, PersonAttributeModel.class, PersonAttributeHash.class),
	
	PATIENT_PROGRAM(PatientProgram.class, PatientProgramModel.class, PatientProgramHash.class),
	
	PATIENT_STATE(PatientState.class, PatientStateModel.class, PatientStateHash.class),
	
	CONCEPT_ATTRIBUTE(ConceptAttribute.class, ConceptAttributeModel.class, null),
	
	LOCATION_ATTRIBUTE(LocationAttribute.class, AttributeModel.class, null),
	
	PROVIDER_ATTRIBUTE(ProviderAttribute.class, AttributeModel.class, null),
	
	VISIT_ATTRIBUTE(VisitAttribute.class, VisitAttributeModel.class, VisitAttributeHash.class),
	
	CONCEPT(Concept.class, ConceptModel.class, null),
	
	LOCATION(Location.class, LocationModel.class, null),
	
	ENCOUNTER_DIAGNOSIS(EncounterDiagnosis.class, EncounterDiagnosisModel.class, EncounterDiagnosisHash.class),
	
	CONDITION(Condition.class, ConditionModel.class, ConditionHash.class),
	
	PERSON_NAME(PersonName.class, PersonNameModel.class, PersonNameHash.class),
	
	ALLERGY(Allergy.class, AllergyModel.class, AllergyHash.class),
	
	PERSON_ADDRESS(PersonAddress.class, PersonAddressModel.class, PersonAddressHash.class),
	
	PATIENT_IDENTIFIER(PatientIdentifier.class, PatientIdentifierModel.class, PatientIdentifierHash.class),
	
	ORDERS(Order.class, OrderModel.class, OrderHash.class),
	
	DRUG_ORDER(DrugOrder.class, DrugOrderModel.class, DrugOrderHash.class),
	
	TEST_ORDER(TestOrder.class, TestOrderModel.class, TestOrderHash.class),
	
	USERS(User.class, UserModel.class, UserHash.class),
	
	RELATIONSHIP(Relationship.class, RelationshipModel.class, RelationshipHash.class),
	
	PROVIDER(Provider.class, ProviderModel.class, ProviderHash.class),
	
	ENCOUNTER_PROVIDER(EncounterProvider.class, EncounterProviderModel.class, EncounterProviderHash.class),
	
	GAAC(Gaac.class, GaacModel.class, GaacHash.class),
	
	GAAC_MEMBER(GaacMember.class, GaacMemberModel.class, GaacMemberHash.class),
	
	GAAC_FAMILY(GaacFamily.class, GaacFamilyModel.class, GaacFamilyHash.class),
	
	GAAC_FAMILY_MEMBER(GaacFamilyMember.class, GaacFamilyMemberModel.class, GaacFamilyMemberHash.class),
	
	CLINICAL_SUMMARY_USAGE_REPORT(ClinicalSummaryUsageReport.class, ClinicalSummaryUsageReportModel.class,
	        ClinicalSummaryUsageReportHash.class);
	
	private Class<? extends BaseEntity> entityClass;
	
	private Class<? extends BaseModel> modelClass;
	
	private Class<? extends BaseHashEntity> hashClass;
	
	TableToSyncEnum(final Class<? extends BaseEntity> entityClass, final Class<? extends BaseModel> modelClass,
	    Class<? extends BaseHashEntity> hashClass) {
		this.entityClass = entityClass;
		this.modelClass = modelClass;
		this.hashClass = hashClass;
	}
	
	public Class<? extends BaseEntity> getEntityClass() {
		return entityClass;
	}
	
	public Class<? extends BaseModel> getModelClass() {
		return modelClass;
	}
	
	public Class<? extends BaseHashEntity> getHashClass() {
		return hashClass;
	}
	
	public static TableToSyncEnum getTableToSyncEnum(final String tableToSync) {
		return valueOf(tableToSync.toUpperCase());
	}
	
	public static TableToSyncEnum getTableToSyncEnum(final Class<? extends BaseModel> tableToSyncClass) {
		return Arrays.stream(values()).filter(e -> e.getModelClass().equals(tableToSyncClass)).findFirst()
		        .orElseThrow(() -> new EIPException("No enum found for model class " + tableToSyncClass));
	}
	
	public static Class<? extends BaseModel> getModelClass(final BaseEntity entity) {
		return Stream.of(values()).filter(e -> e.getEntityClass().equals(entity.getClass())).findFirst()
		        .map(TableToSyncEnum::getModelClass).orElseThrow(
		            () -> new EIPException("No model class found corresponding to entity class " + entity.getClass()));
	}
	
	public static Class<? extends BaseEntity> getEntityClass(final BaseModel model) {
		return Stream.of(values()).filter(e -> e.getModelClass().equals(model.getClass())).findFirst()
		        .map(TableToSyncEnum::getEntityClass).orElseThrow(
		            () -> new EIPException("No entity class found corresponding to model class " + model.getClass()));
	}
	
	public static Class<? extends BaseHashEntity> getHashClass(BaseModel model) {
		return Stream.of(values()).filter(e -> e.getModelClass().equals(model.getClass())).findFirst()
		        .map(TableToSyncEnum::getHashClass)
		        .orElseThrow(() -> new EIPException("No hash class found corresponding to has class " + model.getClass()));
	}
	
	public static TableToSyncEnum getTableToSyncEnumByModelClassName(String modelClassName) {
		return Arrays.stream(values()).filter(e -> e.getModelClass().getName().equals(modelClassName)).findFirst()
		        .orElseThrow(() -> new EIPException("No enum found for model class name " + modelClassName));
	}
}
