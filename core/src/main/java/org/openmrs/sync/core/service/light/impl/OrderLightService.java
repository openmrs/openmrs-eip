package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.OrderLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.attribute.AttributeHelper;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class OrderLightService extends AbstractLightService<OrderLight> {

    private OrderTypeLightService orderTypeService;

    private ConceptLightService conceptService;

    private ProviderLightService providerService;

    private EncounterLightService encounterService;

    private PatientLightService patientService;

    private CareSettingLightService careSettingService;

    public OrderLightService(final OpenMrsRepository<OrderLight> repository,
                             final OrderTypeLightService orderTypeService,
                             final ConceptLightService conceptService,
                             final ProviderLightService providerService,
                             final EncounterLightService encounterService,
                             final PatientLightService patientService,
                             final CareSettingLightService careSettingService) {
        super(repository);
        this.orderTypeService = orderTypeService;
        this.conceptService = conceptService;
        this.providerService = providerService;
        this.encounterService = encounterService;
        this.patientService = patientService;
        this.careSettingService = careSettingService;
    }

    @Override
    protected OrderLight getFakeEntity(final String uuid, final List<AttributeUuid> uuids) {
        OrderLight order = new OrderLight();
        order.setUuid(uuid);
        order.setDateCreated(DEFAULT_DATE);
        order.setCreator(DEFAULT_USER_ID);
        order.setOrderType(orderTypeService.getOrInit(AttributeHelper.getOrderTypeUuid(uuids)));
        order.setConcept(conceptService.getOrInit(AttributeHelper.getConceptUuid(uuids)));
        order.setOrderer(providerService.getOrInit(AttributeHelper.getProviderUuid(uuids)));
        order.setEncounter(encounterService.getOrInit(AttributeHelper.getEncounterTypeUuid(uuids), transformAttributeUuids(uuids)));
        order.setPatient(patientService.getOrInit(AttributeHelper.getPatientUuid(uuids)));
        order.setCareSetting(careSettingService.getOrInit(AttributeHelper.getCareSettingUuid(uuids)));
        return order;
    }

    private List<AttributeUuid> transformAttributeUuids(final List<AttributeUuid> uuids) {
        AttributeUuid encounterTypeAttributeUuid = AttributeHelper.buildEncounterTypeAttributeUuid(AttributeHelper.getOrderEncounterTypeUuid(uuids));
        AttributeUuid patientTypeAttributeUuid = AttributeHelper.buildPatientAttributeUuid(AttributeHelper.getOrderPatientUuid(uuids));
        return Arrays.asList(encounterTypeAttributeUuid, patientTypeAttributeUuid);
    }
}
