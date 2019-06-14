package org.openmrs.sync.core.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonModel extends BaseModel {

    private String gender;

    private LocalDate birthdate;

    private boolean birthdateEstimated;

    private boolean dead;

    private LocalDate deathDate;

    private String causeOfDeathUuid;

    private String causeOfDeathClassUuid;

    private String causeOfDeathDatatypeUuid;

    private boolean deathdateEstimated;

    private LocalTime birthtime;
}
