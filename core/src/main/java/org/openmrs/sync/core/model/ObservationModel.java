package org.openmrs.sync.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ObservationModel extends BaseModel {

    private String personUuid;

    private String conceptUuid;

    private String conceptClassUuid;

    private String conceptDatatypeUuid;

    private String encounterUuid;

    private String encounterPatientUuid;

    private String encounterEncounterTypeUuid;

    private String orderUuid;

    private String orderOrderTypeUuid;

    private String orderConceptUuid;

    private String orderOrdererUuid;

    private String orderEncounterUuid;

    private String orderEncounterPatientUuid;

    private String orderEncounterTypeUuid;

    private String orderPatientUuid;

    private String orderCareSettingUuid;

    private LocalDateTime obsDatetime;

    private String locationUuid;

    private String obsGroupUuid;

    private String obsGroupConceptUuid;

    private String obsGroupConceptDatatypeUuid;

    private String obsGroupConceptClassUuid;

    private String obsGroupPersonUuid;

    private String accessionNumber;

    private Long valueGroupId;

    private String valueCodedUuid;

    private String valueCodedClassUuid;

    private String valueCodedDatatypeUuid;

    private String valueCodedNameUuid;

    private String valueDrugUuid;

    private LocalDateTime valueDatetime;

    private Double valueNumeric;

    private Integer valueModifier;

    private String valueText;

    private String valueComplex;

    private String comments;

    private String previousVersionUuid;

    private String previousVersionConceptUuid;

    private String previousVersionConceptDatatypeUuid;

    private String previousVersionConceptClassUuid;

    private String previousVersionPersonUuid;

    private String formNamespaceAndPath;

    private String status;

    private String interpretation;
}
