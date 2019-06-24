package org.openmrs.sync.core.service;

import org.openmrs.sync.core.model.*;

public enum EntityNameEnum {
    PERSON(PersonModel.class),
    PATIENT(PatientModel.class),
    VISIT(VisitModel.class),
    ENCOUNTER(EncounterModel.class),
    OBSERVATION(ObservationModel.class),
    PERSON_ATTRIBUTE(PersonAttributeModel.class),
    PATIENT_PROGRAM(PatientProgramModel.class),
    PATIENT_STATE(PatientStateModel.class),
    CONCEPT_ATTRIBUTE(ConceptAttributeModel.class),
    LOCATION_ATTRIBUTE(AttributeModel.class),
    PROVIDER_ATTRIBUTE(AttributeModel.class),
    VISIT_ATTRIBUTE(VisitAttributeModel.class);

    private Class<? extends BaseModel> modelClass;

    EntityNameEnum(final Class<? extends BaseModel> modelClass) {
        this.modelClass = modelClass;
    }

    public Class<? extends BaseModel> getModelClass() {
        return modelClass;
    }

    public static EntityNameEnum getEntityNameEnum(String entityName) {
        return valueOf(entityName.toUpperCase());
    }
}
