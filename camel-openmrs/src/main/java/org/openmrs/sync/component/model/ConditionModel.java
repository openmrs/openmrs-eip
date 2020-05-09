package org.openmrs.sync.component.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionModel extends BaseChangeableDataModel {

    private String additionalDetail;

    private String previousVersionUuid;

    private String conditionCodedUuid;

    private String conditionNonCoded;

    private String conditionCodedNameUuid;

    private String clinicalStatus;

    private String verificationStatus;

    private LocalDateTime onsetDate;

    private String patientUuid;

    private LocalDateTime endDate;
}
