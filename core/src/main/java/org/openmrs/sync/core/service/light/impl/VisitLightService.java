package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.PatientLight;
import org.openmrs.sync.core.entity.light.VisitLight;
import org.openmrs.sync.core.entity.light.VisitTypeLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeHelper;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.LightService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitLightService extends AbstractLightService<VisitLight> {

    private LightService<PatientLight> patientService;

    private LightService<VisitTypeLight> visitTypeService;

    public VisitLightService(final OpenMrsRepository<VisitLight> repository,
                             final LightService<PatientLight> patientService,
                             final LightService<VisitTypeLight> visitTypeService) {
        super(repository);
        this.patientService = patientService;
        this.visitTypeService = visitTypeService;
    }

    @Override
    protected VisitLight getFakeEntity(final String uuid,
                                       final List<AttributeUuid> uuids) {
        VisitLight visit = new VisitLight();
        visit.setUuid(uuid);
        visit.setPatient(patientService.getOrInit(AttributeHelper.getPatientUuid(uuids)));
        visit.setDateStarted(DEFAULT_DATE);
        visit.setVisitType(visitTypeService.getOrInit(AttributeHelper.getVisitTypeUuid(uuids)));
        visit.setCreator(DEFAULT_USER_ID);
        visit.setDateCreated(DEFAULT_DATE);
        return visit;
    }
}
