package org.openmrs.sync.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class PatientModel extends PersonModel {

    private String allergyStatus;

    private String creatorUuid;

    private LocalDateTime dateCreated;

    private String changedByUuid;

    private LocalDateTime dateChanged;

    private Boolean voided;

    private String voidedByUuid;

    private LocalDateTime dateVoided;

    private String voidReason;

    private Boolean deathdateEstimated;

    private LocalTime birthtime;
}
