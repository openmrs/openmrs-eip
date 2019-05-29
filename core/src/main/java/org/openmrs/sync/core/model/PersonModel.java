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

    private Boolean birthdateEstimated;

    private Boolean dead;

    private LocalDate deathDate;

    private String causeOfDeathUuid;

    private String personCreatorUuid;

    private LocalDateTime personDateCreated;

    private String personChangedByUuid;

    private LocalDateTime personDateChanged;

    private Boolean personVoided;

    private String personVoidedByUuid;

    private LocalDateTime personDateVoided;

    private String personVoidReason;

    private Boolean deathdateEstimated;

    private LocalTime birthtime;
}
