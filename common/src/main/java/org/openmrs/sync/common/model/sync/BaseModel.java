package org.openmrs.sync.common.model.sync;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public abstract class BaseModel {

    private String uuid;

    private String creatorUuid;

    private LocalDateTime dateCreated;

    private String changedByUuid;

    private LocalDateTime dateChanged;

    private boolean voided;

    private String voidedByUuid;

    private LocalDateTime dateVoided;

    private String voidReason;

    private boolean retired;

    private String retiredByUuid;

    private LocalDateTime dateRetired;

    private String retiredReason;
}
