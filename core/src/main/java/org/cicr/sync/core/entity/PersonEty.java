package org.cicr.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "person")
@AttributeOverrides(
        {
                @AttributeOverride(name = "id", column = @Column(name = "person_id"))
        }
)
public class PersonEty extends OpenMrsEty {

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

    private Boolean deathdateEstimated;

    private String birthtime;

    public PersonEty() {
    }

}
