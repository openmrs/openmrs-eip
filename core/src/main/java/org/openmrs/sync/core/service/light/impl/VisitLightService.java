package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.PatientLight;
import org.openmrs.sync.core.entity.light.VisitLight;
import org.openmrs.sync.core.entity.light.VisitTypeLight;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.VisitContext;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class VisitLightService extends AbstractLightService<VisitLight, VisitContext> {

    private LightServiceNoContext<PatientLight> patientService;

    private LightServiceNoContext<VisitTypeLight> visitTypeService;

    public VisitLightService(final OpenMrsRepository<VisitLight> repository,
                             final LightServiceNoContext<PatientLight> patientService,
                             final LightServiceNoContext<VisitTypeLight> visitTypeService) {
        super(repository);
        this.patientService = patientService;
        this.visitTypeService = visitTypeService;
    }

    @Override
    protected VisitLight getShadowEntity(final String uuid,
                                         final VisitContext context) {
        VisitLight visit = new VisitLight();
        visit.setUuid(uuid);
        visit.setPatient(patientService.getOrInit(context.getPatientUuid()));
        visit.setDateStarted(DEFAULT_DATE);
        visit.setVisitType(visitTypeService.getOrInit(context.getVisitTypeUuid()));
        visit.setCreator(DEFAULT_USER_ID);
        visit.setDateCreated(DEFAULT_DATE);
        return visit;
    }
}
