package org.cicr.camel.remote.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "person")
public class PersonEty {

    @Id
    @GeneratedValue
    private int personId;

    private String gender;

    private String birthdate;

    private Boolean birthdateEstimated;

    private Boolean dead;

    private String deathDate;

    @JoinColumn(name = "cause_of_death")
    @ManyToOne
    private ConceptEty causeOfDeath;

    @JoinColumn(name = "creator")
    @ManyToOne
    private UserEty creator;

    private String dateCreated;

    @JoinColumn(name = "changed_by")
    @ManyToOne
    private UserEty changedBy;

    private String dateChanged;

    private Boolean voided;

    @JoinColumn(name = "voided_by")
    @ManyToOne
    private UserEty voidedBy;

    private String dateVoided;

    private String voidReason;

    @NotNull
    private String uuid;

    private Boolean deathdateEstimated;

    private String birthtime;
}
