package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.ConceptLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
@AttributeOverride(name = "id", column = @Column(name = "person_id"))
public class Person extends BaseChangeableDataEntity {

    @Column(name = "gender")
    private String gender;

    @Column(name = "birthdate")
    private LocalDate birthdate;

    @NotNull
    @Column(name = "birthdate_estimated")
    private boolean birthdateEstimated;

    @NotNull
    @Column(name = "dead")
    private boolean dead;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @ManyToOne
    @JoinColumn(name = "cause_of_death")
    private ConceptLight causeOfDeath;

    @NotNull
    @Column(name = "deathdate_estimated")
    private boolean deathdateEstimated;

    @Column(name = "birthtime")
    private LocalTime birthtime;
}
