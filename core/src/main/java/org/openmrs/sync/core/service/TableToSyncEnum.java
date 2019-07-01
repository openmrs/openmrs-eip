package org.openmrs.sync.core.service;

import org.openmrs.sync.core.entity.*;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.openmrs.sync.core.model.*;

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
    LOCATION(Location.class, LocationModel.class);

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

    public static TableToSyncEnum getTableToSyncEnum(String tableToSync) {
        return valueOf(tableToSync.toUpperCase());
    }

    public static Class<? extends BaseModel> getModelClass(final BaseEntity baseEntity) {
        return Stream.of(values())
                .filter(e -> e.getEntityClass().equals(baseEntity.getClass()))
                .findFirst()
                .map(TableToSyncEnum::getModelClass)
                .orElseThrow(() -> new OpenMrsSyncException("No model class found corresponding to entity class " + baseEntity.getClass()));
    }
}
