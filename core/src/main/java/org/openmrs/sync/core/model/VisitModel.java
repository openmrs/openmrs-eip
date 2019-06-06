package org.openmrs.sync.core.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class VisitModel extends BaseModel {

    private String patientUuid;

    private String visitTypeUuid;

    private LocalDateTime dateStarted;

    private LocalDateTime dateStopped;

    private String indicationConceptUuid;

    private String locationUuid;

    private String creatorUuid;

    private LocalDateTime dateCreated;

    private String changedByUuid;

    private LocalDateTime dateChanged;

    private boolean voided;

    private String voidedByUuid;

    private LocalDateTime dateVoided;

    private String voidReason;
}
