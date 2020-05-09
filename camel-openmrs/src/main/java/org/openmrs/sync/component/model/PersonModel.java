package org.openmrs.sync.component.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonModel extends BaseChangeableDataModel {

    private String gender;

    private LocalDate birthdate;

    private boolean birthdateEstimated;

    private boolean dead;

    private LocalDate deathDate;

    private String causeOfDeathUuid;

    private boolean deathdateEstimated;

    private LocalTime birthtime;
}
