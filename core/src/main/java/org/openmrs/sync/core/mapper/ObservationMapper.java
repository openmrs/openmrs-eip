package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.openmrs.sync.core.entity.Observation;
import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.model.ObservationModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.*;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ObservationMapper implements EntityMapper<Observation, ObservationModel> {

    @Autowired
    protected LightServiceNoContext<PersonLight> personService;

    @Autowired
    protected LightService<ConceptLight, ConceptContext> conceptService;

    @Autowired
    protected LightService<EncounterLight, EncounterContext> encounterService;

    @Autowired
    protected LightService<OrderLight, OrderContext> orderService;

    @Autowired
    protected LightServiceNoContext<LocationLight> locationService;

    @Autowired
    protected LightService<ObservationLight, ObservationContext> observationService;

    @Autowired
    protected LightServiceNoContext<ConceptNameLight> conceptNameService;

    @Autowired
    protected LightService<DrugLight, DrugContext> drugService;

    @Autowired
    protected LightServiceNoContext<UserLight> userService;

    @Override
    @Mapping(source = "person.uuid", target = "personUuid")
    @Mapping(source = "concept.uuid", target = "conceptUuid")
    @Mapping(source = "concept.conceptClass.uuid", target = "conceptClassUuid")
    @Mapping(source = "concept.datatype.uuid", target = "conceptDatatypeUuid")
    @Mapping(source = "encounter.uuid", target = "encounterUuid")
    @Mapping(source = "encounter.patient.uuid", target = "encounterPatientUuid")
    @Mapping(source = "encounter.encounterType.uuid", target = "encounterEncounterTypeUuid")
    @Mapping(source = "order.uuid", target = "orderUuid")
    @Mapping(source = "order.orderType.uuid", target = "orderOrderTypeUuid")
    @Mapping(source = "order.concept.uuid", target = "orderConceptUuid")
    @Mapping(source = "order.concept.datatype.uuid", target = "orderConceptClassUuid")
    @Mapping(source = "order.concept.conceptClass.uuid", target = "orderConceptDatatypeUuid")
    @Mapping(source = "order.orderer.uuid", target = "orderOrdererUuid")
    @Mapping(source = "order.encounter.uuid", target = "orderEncounterUuid")
    @Mapping(source = "order.encounter.patient.uuid", target = "orderEncounterPatientUuid")
    @Mapping(source = "order.encounter.encounterType.uuid", target = "orderEncounterTypeUuid")
    @Mapping(source = "order.patient.uuid", target = "orderPatientUuid")
    @Mapping(source = "order.careSetting.uuid", target = "orderCareSettingUuid")
    @Mapping(source = "location.uuid", target = "locationUuid")
    @Mapping(source = "obsGroup.uuid", target = "obsGroupUuid")
    @Mapping(source = "obsGroup.concept.uuid", target = "obsGroupConceptUuid")
    @Mapping(source = "obsGroup.concept.datatype.uuid", target = "obsGroupConceptDatatypeUuid")
    @Mapping(source = "obsGroup.concept.conceptClass.uuid", target = "obsGroupConceptClassUuid")
    @Mapping(source = "obsGroup.person.uuid", target = "obsGroupPersonUuid")
    @Mapping(source = "valueCoded.uuid", target = "valueCodedUuid")
    @Mapping(source = "valueCoded.conceptClass.uuid", target = "valueCodedClassUuid")
    @Mapping(source = "valueCoded.datatype.uuid", target = "valueCodedDatatypeUuid")
    @Mapping(source = "valueCodedName.uuid", target = "valueCodedNameUuid")
    @Mapping(source = "valueDrug.uuid", target = "valueDrugUuid")
    @Mapping(source = "valueDrug.concept.uuid", target = "valueDrugConceptUuid")
    @Mapping(source = "valueDrug.concept.datatype.uuid", target = "valueDrugConceptDatatypeUuid")
    @Mapping(source = "valueDrug.concept.conceptClass.uuid", target = "valueDrugConceptClassUuid")
    @Mapping(source = "previousVersion.uuid", target = "previousVersionUuid")
    @Mapping(source = "previousVersion.concept.uuid", target = "previousVersionConceptUuid")
    @Mapping(source = "previousVersion.concept.datatype.uuid", target = "previousVersionConceptDatatypeUuid")
    @Mapping(source = "previousVersion.concept.conceptClass.uuid", target = "previousVersionConceptClassUuid")
    @Mapping(source = "previousVersion.person.uuid", target = "previousVersionPersonUuid")
    @Mapping(source = "creator.uuid", target = "creatorUuid")
    @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    public abstract ObservationModel entityToModel(final Observation entity);

    @Override
    @Mapping(expression = "java(personService.getOrInit(model.getPersonUuid()))", target ="person")
    @Mapping(expression = "java(getOrInitConcept(model))", target ="concept")
    @Mapping(expression = "java(getOrInitEncounter(model))", target ="encounter")
    @Mapping(expression = "java(getOrInitOrder(model))", target ="order")
    @Mapping(expression = "java(locationService.getOrInit(model.getLocationUuid()))", target ="location")
    @Mapping(expression = "java(getOrInitObsGroup(model))", target ="obsGroup")
    @Mapping(expression = "java(getOrInitValueCoded(model))", target ="valueCoded")
    @Mapping(expression = "java(conceptNameService.getOrInit(model.getValueCodedNameUuid()))", target ="valueCodedName")
    @Mapping(expression = "java(getOrInitValueDrug(model))", target ="valueDrug")
    @Mapping(expression = "java(getOrInitPreviousVersion(model))", target ="previousVersion")
    @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target ="creator")
    @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target ="voidedBy")
    @Mapping(ignore = true, target = "id")
    public abstract Observation modelToEntity(final ObservationModel model);

    protected ConceptLight getOrInitConcept(final ObservationModel model) {
        ConceptContext context = ConceptContext.builder()
                .conceptClassUuid(model.getConceptClassUuid())
                .conceptDatatypeUuid(model.getConceptDatatypeUuid())
                .build();

        return conceptService.getOrInit(model.getConceptUuid(), context);
    }

    protected ConceptLight getOrInitValueCoded(final ObservationModel model) {
        ConceptContext context = ConceptContext.builder()
                .conceptClassUuid(model.getValueCodedClassUuid())
                .conceptDatatypeUuid(model.getValueCodedDatatypeUuid())
                .build();

        return conceptService.getOrInit(model.getValueCodedUuid(), context);
    }

    protected DrugLight getOrInitValueDrug(final ObservationModel model) {
        DrugContext context = DrugContext.builder()
                .conceptUuid(model.getValueDrugConceptUuid())
                .conceptClassUuid(model.getValueDrugConceptClassUuid())
                .conceptDatatypeUuid(model.getValueDrugConceptDatatypeUuid())
                .build();

        return drugService.getOrInit(model.getValueDrugUuid(), context);
    }

    protected EncounterLight getOrInitEncounter(final ObservationModel model) {
        EncounterContext context = EncounterContext.builder()
                .patientUuid(model.getEncounterPatientUuid())
                .encounterTypeUuid(model.getEncounterEncounterTypeUuid())
                .build();
        return encounterService.getOrInit(model.getEncounterUuid(), context);
    }

    protected OrderLight getOrInitOrder(final ObservationModel model) {
        OrderContext context = OrderContext.builder()
                .orderTypeUuid(model.getOrderOrderTypeUuid())
                .conceptUuid(model.getOrderConceptUuid())
                .conceptClassUuid(model.getOrderConceptClassUuid())
                .conceptDatatypeUuid(model.getOrderConceptDatatypeUuid())
                .providerUuid(model.getOrderOrdererUuid())
                .encounterUuid(model.getOrderEncounterUuid())
                .encounterPatientUuid(model.getOrderEncounterPatientUuid())
                .encounterEncounterTypeUuid(model.getOrderEncounterTypeUuid())
                .patientUuid(model.getOrderPatientUuid())
                .careSettingUuid(model.getOrderCareSettingUuid())
                .build();

        return orderService.getOrInit(model.getOrderUuid(), context);
    }

    protected ObservationLight getOrInitObsGroup(final ObservationModel model) {
        ObservationContext context = ObservationContext.builder()
                .conceptUuid(model.getObsGroupConceptUuid())
                .conceptClassUuid(model.getObsGroupConceptClassUuid())
                .conceptDatatypeUuid(model.getObsGroupConceptDatatypeUuid())
                .personUuid(model.getObsGroupPersonUuid())
                .build();

        return observationService.getOrInit(model.getObsGroupUuid(), context);
    }

    protected ObservationLight getOrInitPreviousVersion(final ObservationModel model) {
        ObservationContext context = ObservationContext.builder()
                .conceptUuid(model.getPreviousVersionConceptUuid())
                .conceptClassUuid(model.getPreviousVersionConceptClassUuid())
                .conceptDatatypeUuid(model.getPreviousVersionConceptDatatypeUuid())
                .personUuid(model.getPreviousVersionPersonUuid())
                .build();

        return observationService.getOrInit(model.getPreviousVersionUuid(), context);
    }
}
