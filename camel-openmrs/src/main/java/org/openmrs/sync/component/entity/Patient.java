package org.openmrs.sync.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.entity.light.UserLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient")
@PrimaryKeyJoinColumn(name = "patient_id")
public class Patient extends Person {

    @NotNull
    @Column(name = "allergy_status")
    private String allergyStatus;

    @NotNull
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

    @Override
    public UserLight getCreator() {
        return creator;
    }

    @Override
    public void setCreator(final UserLight creator) {
        this.creator = creator;
        setPatientCreator(creator);
    }

    @Override
    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    @Override
    public void setDateCreated(final LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
        setPatientDateCreated(dateCreated);
    }

    @Override
    public UserLight getChangedBy() {
        return changedBy;
    }

    @Override
    public void setChangedBy(final UserLight changedBy) {
        this.changedBy = changedBy;
        setPatientChangedBy(changedBy);
    }

    @Override
    public LocalDateTime getDateChanged() {
        return dateChanged;
    }

    @Override
    public void setDateChanged(final LocalDateTime dateChanged) {
        this.dateChanged = dateChanged;
        setPatientDateChanged(dateChanged);
    }

    @Override
    public boolean isVoided() {
        return voided;
    }

    @Override
    public void setVoided(final boolean voided) {
        this.voided = voided;
        setPatientVoided(voided);
    }

    @Override
    public UserLight getVoidedBy() {
        return voidedBy;
    }

    @Override
    public void setVoidedBy(final UserLight voidedBy) {
        this.voidedBy = voidedBy;
        setPatientVoidedBy(voidedBy);
    }

    @Override
    public LocalDateTime getDateVoided() {
        return dateVoided;
    }

    @Override
    public void setDateVoided(final LocalDateTime dateVoided) {
        this.dateVoided = dateVoided;
        setPatientDateVoided(dateVoided);
    }

    @Override
    public String getVoidReason() {
        return voidReason;
    }

    @Override
    public void setVoidReason(final String voidReason) {
        this.voidReason = voidReason;
        setPatientVoidReason(voidReason);
    }
}
