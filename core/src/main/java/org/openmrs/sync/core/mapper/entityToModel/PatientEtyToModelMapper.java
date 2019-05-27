package org.openmrs.sync.core.mapper.entityToModel;

import org.openmrs.sync.core.entity.Patient;
import org.openmrs.sync.core.model.PatientModel;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PatientEtyToModelMapper implements Function<Patient, PatientModel> {

    @Override
    public PatientModel apply(final Patient patient) {
        return null;
    }
}
