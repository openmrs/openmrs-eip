package org.openmrs.sync.core.model;

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
}
