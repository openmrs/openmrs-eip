package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "person")
@AttributeOverrides(
        {
                @AttributeOverride(name = "id", column = @Column(name = "person_id"))
        }
)
public class PersonEty extends TimestampedEty {

    private String gender;

    private LocalDate birthdate;

    @NotNull
    private boolean birthdateEstimated;

    @NotNull
    private boolean dead;

    private LocalDate deathDate;

    @JoinColumn(name = "cause_of_death")
    @ManyToOne
    private ConceptEty causeOfDeath;

    @NotNull
    private boolean deathdateEstimated;

    private LocalTime birthtime;

    public PersonEty() {
    }

}
