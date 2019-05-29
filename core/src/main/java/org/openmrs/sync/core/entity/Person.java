package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.UserLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "person")
@Inheritance(strategy = InheritanceType.JOINED)
@AttributeOverride(name = "id", column = @Column(name = "person_id"))
public class Person extends BaseEntity implements AuditableEntity {

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

    @ManyToOne
    @JoinColumn(name = "creator")
    private UserLight personCreator;

    @NotNull
    @Column(name = "date_created")
    private LocalDateTime personDateCreated;

    @ManyToOne
    @JoinColumn(name = "changed_by")
    private UserLight personChangedBy;

    @Column(name = "date_changed")
    private LocalDateTime personDateChanged;

    @NotNull
    @Column(name = "voided")
    private boolean personVoided;

    @ManyToOne
    @JoinColumn(name = "voided_by")
    private UserLight personVoidedBy;

    @Column(name = "date_voided")
    private LocalDateTime personDateVoided;

    @Column(name = "void_reason")
    private String personVoidReason;

    public Person() {}

    @Override
    public UserLight getCreator() {
        return getPersonCreator();
    }

    @Override
    public void setCreator(final UserLight creator) {
        setPersonCreator(creator);
    }

    @Override
    public LocalDateTime getDateCreated() {
        return getPersonDateCreated();
    }

    @Override
    public void setDateCreated(final LocalDateTime dateCreated) {
        setPersonDateCreated(dateCreated);
    }

    @Override
    public UserLight getChangedBy() {
        return getPersonChangedBy();
    }

    @Override
    public void setChangedBy(final UserLight changedBy) {
        setPersonChangedBy(changedBy);
    }

    @Override
    public LocalDateTime getDateChanged() {
        return getPersonDateChanged();
    }

    @Override
    public void setDateChanged(final LocalDateTime dateChanged) {
        setPersonDateChanged(dateChanged);
    }

    @Override
    public boolean isVoided() {
        return isPersonVoided();
    }

    @Override
    public void setVoided(final boolean voided) {
        setPersonVoided(voided);
    }

    @Override
    public UserLight getVoidedBy() {
        return getPersonVoidedBy();
    }

    @Override
    public void setVoidedBy(final UserLight voidedBy) {
        setPersonVoidedBy(voidedBy);
    }

    @Override
    public LocalDateTime getDateVoided() {
        return getPersonDateVoided();
    }

    @Override
    public void setDateVoided(final LocalDateTime dateVoided) {
        setPersonDateVoided(dateVoided);
    }

    @Override
    public String getVoidReason() {
        return getPersonVoidReason();
    }

    @Override
    public void setVoidReason(final String voidReason) {
        setPersonVoidReason(voidReason);
    }
}
