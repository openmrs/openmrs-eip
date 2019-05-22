package org.cicr.sync.core.model;

import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class PersonModel extends OpenMrsModel {

    private String gender;

    private String birthdate;

    private Boolean birthdateEstimated;

    private Boolean dead;

    private String deathDate;

    private String causeOfDeathUUID;

    private String creatorUUID;

    private String dateCreated;

    private String changedByUUID;

    private String dateChanged;

    private Boolean voided;

    private String voidedByUUID;

    private String dateVoided;

    private String voidReason;

    private Boolean deathdateEstimated;

    private String birthtime;
}
