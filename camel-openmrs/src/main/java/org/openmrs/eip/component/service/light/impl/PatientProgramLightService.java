package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.PatientProgramLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.ProgramLight;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.openmrs.eip.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class PatientProgramLightService extends AbstractLightService<PatientProgramLight> {

    private LightService<PatientLight> patientService;

    private LightService<ProgramLight> programService;

    public PatientProgramLightService(final OpenmrsRepository<PatientProgramLight> repository,
                                      final LightService<PatientLight> patientService,
                                      final LightService<ProgramLight> programService) {
        super(repository);
        this.patientService = patientService;
        this.programService = programService;
    }

    @Override
    protected PatientProgramLight createPlaceholderEntity(final String uuid) {
        PatientProgramLight patientProgram = new PatientProgramLight();
        patientProgram.setDateCreated(DEFAULT_DATE);
        patientProgram.setCreator(DEFAULT_USER_ID);
        patientProgram.setPatient(patientService.getOrInitPlaceholderEntity());
        patientProgram.setProgram(programService.getOrInitPlaceholderEntity());

        return patientProgram;
    }
}
