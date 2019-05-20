package org.cicr.camel.central.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Person {

    private int personId;

    private String gender;

    private String birthdate;

    private boolean birthdateEstimated;

    private boolean dead;

    private String deathDate;

    private String causeOfDeathUUID;

    private String creatorUUID;

    private String dateCreated;

    private String changedByUUID;

    private String dateChanged;

    private boolean voided;

    private String voidedByUUID;

    private String dateVoided;

    private String voidReason;

    private String uuid;

    private boolean deathdateEstimated;

    private String birthtime;
}
