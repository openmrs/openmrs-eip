package org.openmrs.eip.component.entity.light;

import lombok.EqualsAndHashCode;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "order_group")
@AttributeOverride(name = "id", column = @Column(name = "order_group_id"))
@EqualsAndHashCode(callSuper = true)
public class OrderGroupLight extends VoidableLightEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientLight patient;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "encounter_id")
    private EncounterLight encounter;

    /**
     * Gets the patient
     *
     * @return the patient
     */
    public PatientLight getPatient() {
        return patient;
    }

    /**
     * Sets the patient
     *
     * @param patient the patient to set
     */
    public void setPatient(PatientLight patient) {
        this.patient = patient;
    }

    /**
     * Gets the encounter
     *
     * @return the encounter
     */
    public EncounterLight getEncounter() {
        return encounter;
    }

    /**
     * Sets the encounter
     *
     * @param encounter the encounter to set
     */
    public void setEncounter(EncounterLight encounter) {
        this.encounter = encounter;
    }

}
