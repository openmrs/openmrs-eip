package org.cicr.camel.central.entity;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "person")
public class PersonEty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer personId;

    private String gender;

    private String birthdate;

    private Boolean birthdateEstimated;

    private Boolean dead;

    private String deathDate;

    @JoinColumn(name = "cause_of_death")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private ConceptEty causeOfDeath;

    @JoinColumn(name = "creator")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private UserEty creator;

    private String dateCreated;

    @JoinColumn(name = "changed_by")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private UserEty changedBy;

    private String dateChanged;

    private Boolean voided;

    @JoinColumn(name = "voided_by")
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private UserEty voidedBy;

    private String dateVoided;

    private String voidReason;

    @NotNull
    private String uuid;

    private Boolean deathdateEstimated;

    private String birthtime;

    public PersonEty() {}

}
