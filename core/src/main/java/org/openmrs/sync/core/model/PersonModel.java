package org.openmrs.sync.core.model;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonModel extends OpenMrsModel {

    private String gender;

    private LocalDate birthdate;

    private Boolean birthdateEstimated;

    private Boolean dead;

    private LocalDate deathDate;

    private String causeOfDeathUUID;

    private String creatorUUID;

    private LocalDateTime dateCreated;

    private String changedByUUID;

    private LocalDateTime dateChanged;

    private Boolean voided;

    private String voidedByUUID;

    private LocalDateTime dateVoided;

    private String voidReason;

    private Boolean deathdateEstimated;

    private LocalTime birthtime;
}
