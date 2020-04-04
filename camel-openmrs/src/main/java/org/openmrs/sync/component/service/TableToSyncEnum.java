package org.openmrs.sync.component.service;

import org.openmrs.sync.component.entity.*;
import org.openmrs.sync.component.exception.OpenmrsSyncException;
import org.openmrs.sync.component.model.*;

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
    PATIENT_IDENTIFIER(PatientIdentifier.class, PatientIdentifierModel.class),
    WORK_ORDER_STATE(WorkOrderState.class, WorkOrderStateModel.class);

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
