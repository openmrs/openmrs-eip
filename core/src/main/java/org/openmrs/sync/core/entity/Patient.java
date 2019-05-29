package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.core.entity.light.UserLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient")
@PrimaryKeyJoinColumn(name = "patient_id", referencedColumnName = "person_id")
@AttributeOverride(name = "id", column = @Column(name = "patient_id"))
public class Patient extends Person {

    @NotNull
    @Column(name = "allergy_status")
    private String allergyStatus;

    @ManyToOne
    @JoinColumn(name = "creator")
    private UserLight creator;

    @NotNull
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    @ManyToOne
    @JoinColumn(name = "changed_by")
    private UserLight changedBy;

    @Column(name = "date_changed")
    private LocalDateTime dateChanged;

    @NotNull
    @Column(name = "voided")
    private boolean voided;

    @ManyToOne
    @JoinColumn(name = "voided_by")
    private UserLight voidedBy;

    @Column(name = "date_voided")
    private LocalDateTime dateVoided;

    @Column(name = "void_reason")
    private String voidReason;

    public Patient() {}

    public UserLight getCreator() {
        return creator;
    }

    public void setCreator(final UserLight creator) {
        this.creator = creator;
        setPersonCreator(creator);
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
        setPersonDateCreated(dateCreated);
    }

    public UserLight getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(final UserLight changedBy) {
        this.changedBy = changedBy;
        setPersonChangedBy(changedBy);
    }

    public LocalDateTime getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(final LocalDateTime dateChanged) {
        this.dateChanged = dateChanged;
        setPersonDateChanged(dateChanged);
    }

    public boolean isVoided() {
        return voided;
    }

    public void setVoided(final boolean voided) {
        this.voided = voided;
        setPersonVoided(voided);
    }

    public UserLight getVoidedBy() {
        return voidedBy;
    }

    public void setVoidedBy(final UserLight voidedBy) {
        this.voidedBy = voidedBy;
        setPersonVoidedBy(voidedBy);
    }

    public LocalDateTime getDateVoided() {
        return dateVoided;
    }

    public void setDateVoided(final LocalDateTime dateVoided) {
        this.dateVoided = dateVoided;
        setPersonDateVoided(dateVoided);
    }

    public String getVoidReason() {
        return voidReason;
    }

    public void setVoidReason(final String voidReason) {
        this.voidReason = voidReason;
        setPersonVoidReason(voidReason);
    }
}
