package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.openmrs.sync.core.entity.Observation;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.EncounterLight;
import org.openmrs.sync.core.entity.light.ObservationLight;
import org.openmrs.sync.core.entity.light.OrderLight;
import org.openmrs.sync.core.model.ObservationModel;
import org.openmrs.sync.core.service.attribute.AttributeHelper;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.openmrs.sync.core.service.light.impl.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ObservationMapper implements EntityMapper<Observation, ObservationModel> {

    @Autowired
    protected PersonLightService personService;

    @Autowired
    protected ConceptLightService conceptService;

    @Autowired
    protected EncounterLightService encounterService;

    @Autowired
    protected OrderLightService orderService;

    @Autowired
    protected LocationLightService locationService;

    @Autowired
    protected ObservationLightService observationService;

    @Autowired
    protected ConceptNameLightService conceptNameService;

    @Autowired
    protected DrugLightService drugService;

    @Autowired
    protected UserLightService userService;

    @Override
    @Mappings({
            @Mapping(source = "person.uuid", target = "personUuid"),
            @Mapping(source = "concept.uuid", target = "conceptUuid"),
            @Mapping(source = "concept.conceptClass.uuid", target = "conceptClassUuid"),
            @Mapping(source = "concept.datatype.uuid", target = "conceptDatatypeUuid"),
            @Mapping(source = "encounter.uuid", target = "encounterUuid"),
            @Mapping(source = "encounter.patient.uuid", target = "encounterPatientUuid"),
            @Mapping(source = "encounter.encounterType.uuid", target = "encounterEncounterTypeUuid"),
            @Mapping(source = "order.uuid", target = "orderUuid"),
            @Mapping(source = "order.orderType.uuid", target = "orderOrderTypeUuid"),
            @Mapping(source = "order.concept.uuid", target = "orderConceptUuid"),
            @Mapping(source = "order.orderer.uuid", target = "orderOrdererUuid"),
            @Mapping(source = "order.encounter.uuid", target = "orderEncounterUuid"),
            @Mapping(source = "order.encounter.patient.uuid", target = "orderEncounterPatientUuid"),
            @Mapping(source = "order.encounter.encounterType.uuid", target = "orderEncounterTypeUuid"),
            @Mapping(source = "order.patient.uuid", target = "orderPatientUuid"),
            @Mapping(source = "order.careSetting.uuid", target = "orderCareSettingUuid"),
            @Mapping(source = "location.uuid", target = "locationUuid"),
            @Mapping(source = "obsGroup.uuid", target = "obsGroupUuid"),
            @Mapping(source = "obsGroup.concept.uuid", target = "obsGroupConceptUuid"),
            @Mapping(source = "obsGroup.concept.datatype.uuid", target = "obsGroupConceptDatatypeUuid"),
            @Mapping(source = "obsGroup.concept.conceptClass.uuid", target = "obsGroupConceptClassUuid"),
            @Mapping(source = "obsGroup.person.uuid", target = "obsGroupPersonUuid"),
            @Mapping(source = "valueCoded.uuid", target = "valueCodedUuid"),
            @Mapping(source = "valueCoded.conceptClass.uuid", target = "valueCodedClassUuid"),
            @Mapping(source = "valueCoded.datatype.uuid", target = "valueCodedDatatypeUuid"),
            @Mapping(source = "valueCodedName.uuid", target = "valueCodedNameUuid"),
            @Mapping(source = "valueDrug.uuid", target = "valueDrugUuid"),
            @Mapping(source = "previousVersion.uuid", target = "previousVersionUuid"),
            @Mapping(source = "previousVersion.concept.uuid", target = "previousVersionConceptUuid"),
            @Mapping(source = "previousVersion.concept.datatype.uuid", target = "previousVersionConceptDatatypeUuid"),
            @Mapping(source = "previousVersion.concept.conceptClass.uuid", target = "previousVersionConceptClassUuid"),
            @Mapping(source = "previousVersion.person.uuid", target = "previousVersionPersonUuid"),
            @Mapping(source = "creator.uuid", target = "creatorUuid"),
            @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    })
    public abstract ObservationModel entityToModel(final Observation entity);

    @Override
    @Mappings({
            @Mapping(expression = "java(personService.getOrInit(model.getPersonUuid()))", target ="person"),
            @Mapping(expression = "java(getOrInitConcept(model))", target ="concept"),
            @Mapping(expression = "java(getOrInitEncounter(model))", target ="encounter"),
            @Mapping(expression = "java(getOrInitOrder(model))", target ="order"),
            @Mapping(expression = "java(locationService.getOrInit(model.getLocationUuid()))", target ="location"),
            @Mapping(expression = "java(getOrInitObsGroup(model))", target ="obsGroup"),
            @Mapping(expression = "java(getOrInitValueCoded(model))", target ="valueCoded"),
            @Mapping(expression = "java(conceptNameService.getOrInit(model.getValueCodedNameUuid()))", target ="valueCodedName"),
            @Mapping(expression = "java(drugService.getOrInit(model.getValueDrugUuid()))", target ="valueDrug"),
            @Mapping(expression = "java(getOrInitPreviousVersion(model))", target ="previousVersion"),
            @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target ="creator"),
            @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target ="voidedBy"),
            @Mapping(ignore = true, target = "id")
    })
    public abstract Observation modelToEntity(final ObservationModel model);

    protected ConceptLight getOrInitConcept(final ObservationModel model) {
        AttributeUuid conceptDatatypeAttributeUuid = AttributeHelper.buildConceptDatatypeAttributeUuid(model.getConceptDatatypeUuid());
        AttributeUuid conceptClassAttributeUuid = AttributeHelper.buildConceptClassAttributeUuid(model.getConceptClassUuid());

        return conceptService.getOrInit(model.getConceptUuid(), Arrays.asList(conceptDatatypeAttributeUuid, conceptClassAttributeUuid));
    }

    protected ConceptLight getOrInitValueCoded(final ObservationModel model) {
        AttributeUuid conceptDatatypeAttributeUuid = AttributeHelper.buildConceptDatatypeAttributeUuid(model.getValueCodedDatatypeUuid());
        AttributeUuid conceptClassAttributeUuid = AttributeHelper.buildConceptClassAttributeUuid(model.getValueCodedClassUuid());

        return conceptService.getOrInit(model.getValueCodedUuid(), Arrays.asList(conceptDatatypeAttributeUuid, conceptClassAttributeUuid));
    }

    protected EncounterLight getOrInitEncounter(final ObservationModel model) {
        AttributeUuid patientAttributeUuid = AttributeHelper.buildPatientAttributeUuid(model.getEncounterPatientUuid());
        AttributeUuid encounterTypeAttributeUuid = AttributeHelper.buildEncounterTypeAttributeUuid(model.getEncounterEncounterTypeUuid());
        return encounterService.getOrInit(model.getEncounterUuid(), Arrays.asList(patientAttributeUuid, encounterTypeAttributeUuid));
    }

    protected OrderLight getOrInitOrder(final ObservationModel model) {
        AttributeUuid orderTypeAttributeUuid = AttributeHelper.buildOrderTypeAttributeUuid(model.getOrderOrderTypeUuid());
        AttributeUuid conceptAttributeUuid = AttributeHelper.buildConceptAttributeUuid(model.getOrderConceptUuid());
        AttributeUuid providerAttributeUuid = AttributeHelper.buildProviderAttributeUuid(model.getOrderOrdererUuid());

        AttributeUuid encounterAttributeUuid = AttributeHelper.buildOrderEncounterAttributeUuid(model.getOrderEncounterUuid());
        AttributeUuid encounterTypeAttributeUuid = AttributeHelper.buildOrderEncounterTypeAttributeUuid(model.getOrderEncounterTypeUuid());
        AttributeUuid encounterPatientAttributeUuid = AttributeHelper.buildOrderPatientAttributeUuid(model.getOrderEncounterPatientUuid());

        AttributeUuid patientAttributeUuid = AttributeHelper.buildPatientAttributeUuid(model.getOrderPatientUuid());
        AttributeUuid careSettingAttributeUuid = AttributeHelper.buildCareSettingTypeAttributeUuid(model.getOrderCareSettingUuid());

        List<AttributeUuid> attributeUuids = Arrays.asList(
                orderTypeAttributeUuid,
                conceptAttributeUuid,
                providerAttributeUuid,
                encounterAttributeUuid,
                encounterTypeAttributeUuid,
                encounterPatientAttributeUuid,
                patientAttributeUuid,
                careSettingAttributeUuid
        );

        return orderService.getOrInit(model.getOrderUuid(), attributeUuids);
    }

    protected ObservationLight getOrInitObsGroup(final ObservationModel model) {
        AttributeUuid conceptAttributeUuid = AttributeHelper.buildConceptAttributeUuid(model.getObsGroupConceptUuid());
        AttributeUuid conceptDatatypeAttributeUuid = AttributeHelper.buildConceptDatatypeAttributeUuid(model.getObsGroupConceptDatatypeUuid());
        AttributeUuid conceptClassAttributeUuid = AttributeHelper.buildConceptClassAttributeUuid(model.getObsGroupConceptClassUuid());
        AttributeUuid personTypeAttributeUuid = AttributeHelper.buildPersonAttributeUuid(model.getObsGroupPersonUuid());

        List<AttributeUuid> attributeUuids = Arrays.asList(
                conceptAttributeUuid,
                conceptDatatypeAttributeUuid,
                conceptClassAttributeUuid,
                personTypeAttributeUuid
        );

        return observationService.getOrInit(model.getObsGroupUuid(), attributeUuids);
    }

    protected ObservationLight getOrInitPreviousVersion(final ObservationModel model) {
        AttributeUuid conceptAttributeUuid = AttributeHelper.buildConceptAttributeUuid(model.getPreviousVersionConceptUuid());
        AttributeUuid conceptDatatypeAttributeUuid = AttributeHelper.buildConceptDatatypeAttributeUuid(model.getPreviousVersionConceptDatatypeUuid());
        AttributeUuid conceptClassAttributeUuid = AttributeHelper.buildConceptClassAttributeUuid(model.getPreviousVersionConceptClassUuid());
        AttributeUuid personTypeAttributeUuid = AttributeHelper.buildPersonAttributeUuid(model.getPreviousVersionPersonUuid());

        List<AttributeUuid> attributeUuids = Arrays.asList(
                conceptAttributeUuid,
                conceptDatatypeAttributeUuid,
                conceptClassAttributeUuid,
                personTypeAttributeUuid
        );

        return observationService.getOrInit(model.getPreviousVersionUuid(), attributeUuids);
    }
}
