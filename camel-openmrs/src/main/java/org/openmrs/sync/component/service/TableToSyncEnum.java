package org.openmrs.sync.component.service;

import org.openmrs.sync.component.entity.Allergy;
import org.openmrs.sync.component.entity.BaseEntity;
import org.openmrs.sync.component.entity.Concept;
import org.openmrs.sync.component.entity.ConceptAttribute;
import org.openmrs.sync.component.entity.Condition;
import org.openmrs.sync.component.entity.Encounter;
import org.openmrs.sync.component.entity.EncounterDiagnosis;
import org.openmrs.sync.component.entity.Location;
import org.openmrs.sync.component.entity.LocationAttribute;
import org.openmrs.sync.component.entity.Observation;
import org.openmrs.sync.component.entity.Patient;
import org.openmrs.sync.component.entity.PatientIdentifier;
import org.openmrs.sync.component.entity.PatientProgram;
import org.openmrs.sync.component.entity.PatientState;
import org.openmrs.sync.component.entity.Person;
import org.openmrs.sync.component.entity.PersonAddress;
import org.openmrs.sync.component.entity.PersonAttribute;
import org.openmrs.sync.component.entity.PersonName;
import org.openmrs.sync.component.entity.ProviderAttribute;
import org.openmrs.sync.component.entity.Visit;
import org.openmrs.sync.component.entity.VisitAttribute;
import org.openmrs.sync.component.entity.ErpWorkOrderState;
import org.openmrs.sync.component.exception.OpenmrsSyncException;
import org.openmrs.sync.component.model.AllergyModel;
import org.openmrs.sync.component.model.AttributeModel;
import org.openmrs.sync.component.model.BaseModel;
import org.openmrs.sync.component.model.ConceptAttributeModel;
import org.openmrs.sync.component.model.ConceptModel;
import org.openmrs.sync.component.model.ConditionModel;
import org.openmrs.sync.component.model.EncounterDiagnosisModel;
import org.openmrs.sync.component.model.EncounterModel;
import org.openmrs.sync.component.model.LocationModel;
import org.openmrs.sync.component.model.ObservationModel;
import org.openmrs.sync.component.model.PatientIdentifierModel;
import org.openmrs.sync.component.model.PatientModel;
import org.openmrs.sync.component.model.PatientProgramModel;
import org.openmrs.sync.component.model.PatientStateModel;
import org.openmrs.sync.component.model.PersonAddressModel;
import org.openmrs.sync.component.model.PersonAttributeModel;
import org.openmrs.sync.component.model.PersonModel;
import org.openmrs.sync.component.model.PersonNameModel;
import org.openmrs.sync.component.model.VisitAttributeModel;
import org.openmrs.sync.component.model.VisitModel;
import org.openmrs.sync.component.model.ErpWorkOrderStateModel;

import java.util.Arrays;
import java.util.stream.Stream;

public enum TableToSyncEnum {
    PERSON(Person.class, PersonModel.class),
    PATIENT(Patient.class, PatientModel.class),
    VISIT(Visit.class, VisitModel.class),
    ENCOUNTER(Encounter.class, EncounterModel.class),
    OBSERVATION(Observation.class, ObservationModel.class),
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
    PATIENT_IDENTIFIER(PatientIdentifier.class, PatientIdentifierModel.class);
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
                .orElseThrow(() -> new OpenmrsSyncException("No enum found for model class " + tableToSyncClass));
    }

    public static Class<? extends BaseModel> getModelClass(final BaseEntity entity) {
        return Stream.of(values())
                .filter(e -> e.getEntityClass().equals(entity.getClass()))
                .findFirst()
                .map(TableToSyncEnum::getModelClass)
                .orElseThrow(() -> new OpenmrsSyncException("No model class found corresponding to entity class " + entity.getClass()));
    }

    public static Class<? extends BaseEntity> getEntityClass(final BaseModel model) {
        return Stream.of(values())
                .filter(e -> e.getModelClass().equals(model.getClass()))
                .findFirst()
                .map(TableToSyncEnum::getEntityClass)
                .orElseThrow(() -> new OpenmrsSyncException("No entity class found corresponding to model class " + model.getClass()));
    }
}
