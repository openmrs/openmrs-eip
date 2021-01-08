package org.openmrs.eip.component.service;

import java.util.Arrays;
import java.util.stream.Stream;

import org.openmrs.eip.component.entity.Allergy;
import org.openmrs.eip.component.entity.BaseEntity;
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
import org.openmrs.eip.component.entity.OrderFrequency;
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
import org.openmrs.eip.component.model.AllergyModel;
import org.openmrs.eip.component.model.AttributeModel;
import org.openmrs.eip.component.model.BaseModel;
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
import org.openmrs.eip.component.model.OrderFrequencyModel;
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

public enum TableToSyncEnum {
    PERSON(Person.class, PersonModel.class),
    PATIENT(Patient.class, PatientModel.class),
    VISIT(Visit.class, VisitModel.class),
    ENCOUNTER(Encounter.class, EncounterModel.class),
    OBS(Observation.class, ObservationModel.class),
    PERSON_ATTRIBUTE(PersonAttribute.class, PersonAttributeModel.class),
    PATIENT_PROGRAM(PatientProgram.class, PatientProgramModel.class),
    PATIENT_STATE(PatientState.class, PatientStateModel.class),
    CONCEPT_ATTRIBUTE(ConceptAttribute.class, ConceptAttributeModel.class),
    LOCATION_ATTRIBUTE(LocationAttribute.class, AttributeModel.class),
    PROVIDER_ATTRIBUTE(ProviderAttribute.class, AttributeModel.class),
    VISIT_ATTRIBUTE(VisitAttribute.class, VisitAttributeModel.class),
    CONCEPT(Concept.class, ConceptModel.class),
    LOCATION(Location.class, LocationModel.class),
    ENCOUNTER_DIAGNOSIS(EncounterDiagnosis.class, EncounterDiagnosisModel.class),
    CONDITION(Condition.class, ConditionModel.class),
    PERSON_NAME(PersonName.class, PersonNameModel.class),
    ALLERGY(Allergy.class, AllergyModel.class),
    PERSON_ADDRESS(PersonAddress.class, PersonAddressModel.class),
    PATIENT_IDENTIFIER(PatientIdentifier.class, PatientIdentifierModel.class),
    ORDERS(Order.class, OrderModel.class),
    DRUG_ORDER(DrugOrder.class, DrugOrderModel.class),
    TEST_ORDER(TestOrder.class, TestOrderModel.class),
    USERS(User.class, UserModel.class),
    RELATIONSHIP(Relationship.class, RelationshipModel.class),
	
    ORDER_FREQUENCY(OrderFrequency.class, OrderFrequencyModel.class), 
    PROVIDER(Provider.class, ProviderModel.class),
	ENCOUNTER_PROVIDER(EncounterProvider.class, EncounterProviderModel.class), 
	GAAC(Gaac.class, GaacModel.class),
	GAAC_MEMBER(GaacMember.class, GaacMemberModel.class),
	GAAC_FAMILY(GaacFamily.class, GaacFamilyModel.class),
	GAAC_FAMILY_MEMBER(GaacFamilyMember.class, GaacFamilyMemberModel.class);
	      
    //ICRC_ERP_WORK_ORDER_STATE(ErpWorkOrderState.class, ErpWorkOrderStateModel.class);

    private Class<? extends BaseEntity> entityClass;

    private Class<? extends BaseModel> modelClass;

    TableToSyncEnum(final Class<? extends BaseEntity> entityClass,
                    final Class<? extends BaseModel> modelClass) {
        this.entityClass = entityClass;
        this.modelClass = modelClass;
    }

    public Class<? extends BaseEntity> getEntityClass() {
        return entityClass;
    }

    public Class<? extends BaseModel> getModelClass() {
        return modelClass;
    }

    public static TableToSyncEnum getTableToSyncEnum(final String tableToSync) {
        return valueOf(tableToSync.toUpperCase());
    }

    public static TableToSyncEnum getTableToSyncEnum(final Class<? extends BaseModel> tableToSyncClass) {
        return Arrays.stream(values())
                .filter(e -> e.getModelClass().equals(tableToSyncClass))
                .findFirst()
                .orElseThrow(() -> new EIPException("No enum found for model class " + tableToSyncClass));
    }

    public static Class<? extends BaseModel> getModelClass(final BaseEntity entity) {
        return Stream.of(values())
                .filter(e -> e.getEntityClass().equals(entity.getClass()))
                .findFirst()
                .map(TableToSyncEnum::getModelClass)
                .orElseThrow(() -> new EIPException("No model class found corresponding to entity class " + entity.getClass()));
    }

    public static Class<? extends BaseEntity> getEntityClass(final BaseModel model) {
        return Stream.of(values())
                .filter(e -> e.getModelClass().equals(model.getClass()))
                .findFirst()
                .map(TableToSyncEnum::getEntityClass)
                .orElseThrow(() -> new EIPException("No entity class found corresponding to model class " + model.getClass()));
    }
}
