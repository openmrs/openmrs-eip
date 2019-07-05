package org.openmrs.sync.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ObservationModel extends BaseModel {

    private String personUuid;

    private String conceptUuid;

    private String encounterUuid;

    private String orderUuid;

    private LocalDateTime obsDatetime;

    private String locationUuid;

    private String obsGroupUuid;

    private String accessionNumber;

    private Long valueGroupId;

    private String valueCodedUuid;

    private String valueCodedNameUuid;

    private String valueDrugUuid;

    private LocalDateTime valueDatetime;

    private Double valueNumeric;

    private Integer valueModifier;

    private String valueText;

    private String valueComplex;

    private String comments;

    private String previousVersionUuid;

    private String formNamespaceAndPath;

    private String status;

    private String interpretation;
}
