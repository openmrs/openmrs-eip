package org.openmrs.sync.core.mapper.modelToEntity;

import org.openmrs.sync.core.entity.Patient;
import org.openmrs.sync.core.model.PatientModel;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PatientModelToEtyMapper implements Function<PatientModel, Patient> {

    @Override
    public Patient apply(final PatientModel patientModel) {
        return null;
    }
}
