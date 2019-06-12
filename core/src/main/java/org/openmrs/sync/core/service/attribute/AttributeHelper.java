package org.openmrs.sync.core.service.attribute;

import java.util.List;

public final class AttributeHelper {

    private static final String PATIENT = "patient";
    private static final String PERSON = "person";
    private static final String VISIT_TYPE = "visitType";
    private static final String CONCEPT = "concept";
    private static final String CONCEPT_CLASS = "conceptClass";
    private static final String CONCEPT_DATATYPE = "conceptDatatype";
    private static final String ENCOUNTER_TYPE = "encounterType";
    private static final String ORDER_ENCOUNTER = "orderEncounter";
    private static final String ORDER_ENCOUNTER_TYPE = "orderEncounterType";
    private static final String ORDER_PATIENT = "orderPatient";
    private static final String ORDER_TYPE = "orderType";
    private static final String PROVIDER = "provider";
    private static final String CARE_SETTING = "careSetting";

    private AttributeHelper() {}

    public static AttributeUuid buildPatientAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, PATIENT);
    }

    public static AttributeUuid buildPersonAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, PERSON);
    }

    public static AttributeUuid buildVisitTypeAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, VISIT_TYPE);
    }

    public static AttributeUuid buildConceptAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, CONCEPT);
    }

    public static AttributeUuid buildConceptDatatypeAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, CONCEPT_DATATYPE);
    }

    public static AttributeUuid buildConceptClassAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, CONCEPT_CLASS);
    }

    public static AttributeUuid buildEncounterTypeAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, ENCOUNTER_TYPE);
    }

    public static AttributeUuid buildOrderEncounterAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, ORDER_ENCOUNTER);
    }

    public static AttributeUuid buildOrderEncounterTypeAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, ORDER_ENCOUNTER_TYPE);
    }

    public static AttributeUuid buildOrderPatientAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, ORDER_PATIENT);
    }

    public static AttributeUuid buildOrderTypeAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, ORDER_TYPE);
    }

    public static AttributeUuid buildProviderAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, PROVIDER);
    }

    public static AttributeUuid buildCareSettingTypeAttributeUuid(final String uuid) {
        return getAttributeUuid(uuid, CARE_SETTING);
    }

    private static AttributeUuid getAttributeUuid(final String uuid, final String visitType) {
        return AttributeUuid.builder()
                .key(visitType)
                .uuid(uuid)
                .build();
    }

    public static String getPatientUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, PATIENT);
    }

    public static String getPersonUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, PERSON);
    }

    public static String getVisitTypeUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, VISIT_TYPE);
    }

    public static String getConceptUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, CONCEPT);
    }

    public static String getConceptDatatypeUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, CONCEPT_DATATYPE);
    }

    public static String getConceptClassUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, CONCEPT_CLASS);
    }

    public static String getEncounterTypeUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, ENCOUNTER_TYPE);
    }

    public static String getOrderEncounterTypeUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, ORDER_ENCOUNTER_TYPE);
    }

    public static String getOrderPatientUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, ORDER_PATIENT);
    }

    public static String getOrderTypeUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, ORDER_TYPE);
    }

    public static String getProviderUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, PROVIDER);
    }

    public static String getCareSettingUuid(final List<AttributeUuid> uuids) {
        return getUuid(uuids, CARE_SETTING);
    }

    private static String getUuid(final List<AttributeUuid> uuids,
                                 final String key) {
        return uuids.stream()
                .filter(attributeUuid -> attributeUuid.getKey().equals(key))
                .findFirst().map(AttributeUuid::getUuid)
                .orElseThrow(() -> new IllegalStateException("No attribute uuid set for key " + key));
    }
}
