package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;
import org.openmrs.sync.core.service.light.impl.context.EncounterContext;
import org.openmrs.sync.core.service.light.impl.context.OrderContext;
import org.springframework.stereotype.Service;

@Service
public class OrderLightService extends AbstractLightService<OrderLight, OrderContext> {

    private LightServiceNoContext<OrderTypeLight> orderTypeService;

    private LightService<ConceptLight, ConceptContext> conceptService;

    private LightServiceNoContext<ProviderLight> providerService;

    private LightService<EncounterLight, EncounterContext> encounterService;

    private LightServiceNoContext<PatientLight> patientService;

    private LightServiceNoContext<CareSettingLight> careSettingService;

    public OrderLightService(final OpenMrsRepository<OrderLight> repository,
                             final LightServiceNoContext<OrderTypeLight> orderTypeService,
                             final LightService<ConceptLight, ConceptContext> conceptService,
                             final LightServiceNoContext<ProviderLight> providerService,
                             final LightService<EncounterLight, EncounterContext> encounterService,
                             final LightServiceNoContext<PatientLight> patientService,
                             final LightServiceNoContext<CareSettingLight> careSettingService) {
        super(repository);
        this.orderTypeService = orderTypeService;
        this.conceptService = conceptService;
        this.providerService = providerService;
        this.encounterService = encounterService;
        this.patientService = patientService;
        this.careSettingService = careSettingService;
    }

    @Override
    protected OrderLight getShadowEntity(final String uuid, final OrderContext context) {
        OrderLight order = new OrderLight();
        order.setUuid(uuid);
        order.setDateCreated(DEFAULT_DATE);
        order.setCreator(DEFAULT_USER_ID);
        order.setOrderType(orderTypeService.getOrInit(context.getOrderTypeUuid()));
        order.setConcept(conceptService.getOrInit(context.getConceptUuid(), getConceptContext(context)));
        order.setOrderer(providerService.getOrInit(context.getProviderUuid()));
        order.setEncounter(encounterService.getOrInit(context.getEncounterUuid(), getEncounterContext(context)));
        order.setPatient(patientService.getOrInit(context.getPatientUuid()));
        order.setCareSetting(careSettingService.getOrInit(context.getCareSettingUuid()));
        return order;
    }

    private EncounterContext getEncounterContext(final OrderContext context) {
        return EncounterContext.builder()
                .encounterTypeUuid(context.getEncounterEncounterTypeUuid())
                .patientUuid(context.getEncounterPatientUuid())
                .build();
    }

    private ConceptContext getConceptContext(final OrderContext context) {
        return ConceptContext.builder()
                .conceptClassUuid(context.getConceptClassUuid())
                .conceptDatatypeUuid(context.getConceptDatatypeUuid())
                .build();
    }
}
