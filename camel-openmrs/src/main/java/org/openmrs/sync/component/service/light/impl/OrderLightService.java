package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.*;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.openmrs.sync.component.service.light.AbstractLightService;
import org.openmrs.sync.component.service.light.LightService;
import org.springframework.stereotype.Service;

@Service
public class OrderLightService extends AbstractLightService<OrderLight> {

    private LightService<OrderTypeLight> orderTypeService;

    private LightService<ConceptLight> conceptService;

    private LightService<ProviderLight> providerService;

    private LightService<EncounterLight> encounterService;

    private LightService<PatientLight> patientService;

    private LightService<CareSettingLight> careSettingService;

    public OrderLightService(final OpenmrsRepository<OrderLight> repository,
                             final LightService<OrderTypeLight> orderTypeService,
                             final LightService<ConceptLight> conceptService,
                             final LightService<ProviderLight> providerService,
                             final LightService<EncounterLight> encounterService,
                             final LightService<PatientLight> patientService,
                             final LightService<CareSettingLight> careSettingService) {
        super(repository);
        this.orderTypeService = orderTypeService;
        this.conceptService = conceptService;
        this.providerService = providerService;
        this.encounterService = encounterService;
        this.patientService = patientService;
        this.careSettingService = careSettingService;
    }

    @Override
    protected OrderLight createPlaceholderEntity(final String uuid) {
        OrderLight order = new OrderLight();
        order.setDateCreated(DEFAULT_DATE);
        order.setCreator(DEFAULT_USER_ID);
        order.setOrderType(orderTypeService.getOrInitPlaceholderEntity());
        order.setConcept(conceptService.getOrInitPlaceholderEntity());
        order.setOrderer(providerService.getOrInitPlaceholderEntity());
        order.setEncounter(encounterService.getOrInitPlaceholderEntity());
        order.setPatient(patientService.getOrInitPlaceholderEntity());
        order.setCareSetting(careSettingService.getOrInitPlaceholderEntity());
        return order;
    }
}
