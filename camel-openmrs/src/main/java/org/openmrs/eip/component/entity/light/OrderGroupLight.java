package org.openmrs.eip.component.entity.light;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

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
