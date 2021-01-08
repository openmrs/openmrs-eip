package org.openmrs.eip.component.service.light.impl;

import org.openmrs.eip.component.entity.light.GaacFamilyLite;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.openmrs.eip.component.service.light.AbstractLightService;
import org.openmrs.eip.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class GaacFamilyLightService extends AbstractLightService<GaacFamilyLite> {

    private LightService<PatientLight> patientService;

    private LightService<LocationLight> locationService;

    public GaacFamilyLightService(final OpenmrsRepository<GaacFamilyLite> repository,
                                   final LightService<PatientLight> patientService,
                                   final LightService<LocationLight> locationService) {
        super(repository);
        this.patientService = patientService;
        this.locationService = locationService;
    }

    @Override
    protected GaacFamilyLite createPlaceholderEntity(final String uuid) {
    	GaacFamilyLite gaac = new GaacFamilyLite();
        gaac.setDateCreated(DEFAULT_DATE);
        gaac.setCreator(DEFAULT_USER_ID);
        gaac.setFocalPatient(patientService.getOrInitPlaceholderEntity());
        gaac.setLocation(locationService.getOrInitPlaceholderEntity());
        return gaac;
    }
}
