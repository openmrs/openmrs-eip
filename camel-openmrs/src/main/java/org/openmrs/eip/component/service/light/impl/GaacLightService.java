package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.GaacLite;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.openmrs.eip.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class GaacLightService extends AbstractLightService<GaacLite> {

    private LightService<PatientLight> patientService;

    private LightService<LocationLight> locationService;

    public GaacLightService(final OpenmrsRepository<GaacLite> repository,
                                   final LightService<PatientLight> patientService,
                                   final LightService<LocationLight> locationService) {
        super(repository);
        this.patientService = patientService;
        this.locationService = locationService;
    }

    @Override
    protected GaacLite createPlaceholderEntity(final String uuid) {
    	GaacLite gaac = new GaacLite();
        gaac.setDateCreated(DEFAULT_DATE);
        gaac.setCreator(DEFAULT_USER_ID);
        gaac.setName("");
        gaac.setFocalPatient(patientService.getOrInitPlaceholderEntity());
        gaac.setLocation(locationService.getOrInitPlaceholderEntity());
        return gaac;
    }
}
