package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.PatientLight;
import org.openmrs.sync.core.entity.light.PatientProgramLight;
import org.openmrs.sync.core.entity.light.ProgramLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.PatientProgramContext;
import org.openmrs.sync.core.service.light.impl.context.ProgramContext;
import org.springframework.stereotype.Service;

@Service
public class PatientProgramLightService extends AbstractLightService<PatientProgramLight, PatientProgramContext> {

    private LightServiceNoContext<PatientLight> patientService;

    private LightService<ProgramLight, ProgramContext> programService;

    public PatientProgramLightService(final OpenMrsRepository<PatientProgramLight> repository,
                                      final LightServiceNoContext<PatientLight> patientService,
                                      final LightService<ProgramLight, ProgramContext> programService) {
        super(repository);
        this.patientService = patientService;
        this.programService = programService;
    }

    @Override
    protected PatientProgramLight getShadowEntity(final String uuid, final PatientProgramContext context) {
        PatientProgramLight patientProgram = new PatientProgramLight();
        patientProgram.setUuid(uuid);
        patientProgram.setDateCreated(DEFAULT_DATE);
        patientProgram.setCreator(DEFAULT_USER_ID);
        patientProgram.setPatient(patientService.getOrInit(context.getPatientUuid()));
        patientProgram.setProgram(programService.getOrInit(context.getProgramUuid(), getProgramContext(context)));

        return patientProgram;
    }

    private ProgramContext getProgramContext(final PatientProgramContext context) {
        return ProgramContext.builder()
                .conceptUuid(context.getProgramConceptUuid())
                .conceptClassUuid(context.getProgramConceptClassUuid())
                .conceptDatatypeUuid(context.getProgramConceptDatatypeUuid())
                .build();
    }
}
