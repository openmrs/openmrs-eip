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
    private UserLight patientCreator;

    @NotNull
    @Column(name = "date_created")
    private LocalDateTime patientDateCreated;

    @ManyToOne
    @JoinColumn(name = "changed_by")
    private UserLight patientChangedBy;

    @Column(name = "date_changed")
    private LocalDateTime patientDateChanged;

    @NotNull
    @Column(name = "voided")
    private boolean patientVoided;

    @ManyToOne
    @JoinColumn(name = "voided_by")
    private UserLight patientVoidedBy;

    @Column(name = "date_voided")
    private LocalDateTime patientDateVoided;

    @Column(name = "void_reason")
    private String patientVoidReason;

    public Patient() {}

    public UserLight getCreator() {
        return creator;
    }

    public void setCreator(final UserLight creator) {
        this.creator = creator;
        setPatientCreator(creator);
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(final LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
        setPatientDateCreated(dateCreated);
    }

    public UserLight getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(final UserLight changedBy) {
        this.changedBy = changedBy;
        setPatientChangedBy(changedBy);
    }

    public LocalDateTime getDateChanged() {
        return dateChanged;
    }

    public void setDateChanged(final LocalDateTime dateChanged) {
        this.dateChanged = dateChanged;
        setPatientDateChanged(dateChanged);
    }

    public boolean isVoided() {
        return voided;
    }

    public void setVoided(final boolean voided) {
        this.voided = voided;
        setPatientVoided(voided);
    }

    public UserLight getVoidedBy() {
        return voidedBy;
    }

    public void setVoidedBy(final UserLight voidedBy) {
        this.voidedBy = voidedBy;
        setPatientVoidedBy(voidedBy);
    }

    public LocalDateTime getDateVoided() {
        return dateVoided;
    }

    public void setDateVoided(final LocalDateTime dateVoided) {
        this.dateVoided = dateVoided;
        setPatientDateVoided(dateVoided);
    }

    public String getVoidReason() {
        return voidReason;
    }

    public void setVoidReason(final String voidReason) {
        this.voidReason = voidReason;
        setPatientVoidReason(voidReason);
    }
}
