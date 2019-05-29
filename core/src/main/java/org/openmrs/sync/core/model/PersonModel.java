package org.openmrs.sync.core.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonModel extends BaseModel {

    private String gender;

    private LocalDate birthdate;

    private boolean birthdateEstimated;

    private boolean dead;

    private LocalDate deathDate;

    private String causeOfDeathUuid;

    private boolean deathdateEstimated;

    private String creatorUuid;

    private LocalDateTime dateCreated;

    private String changedByUuid;

    private LocalDateTime dateChanged;

    private boolean voided;

    private String voidedByUuid;

    private LocalDateTime dateVoided;

    private String voidReason;

    private LocalTime birthtime;
}
