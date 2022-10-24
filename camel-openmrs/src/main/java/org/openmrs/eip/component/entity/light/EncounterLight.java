package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "encounter")
@AttributeOverride(name = "id", column = @Column(name = "encounter_id"))
public class EncounterLight extends VoidableLightEntity {
	
	@NotNull
	@Column(name = "encounter_datetime")
	private LocalDateTime encounterDatetime;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private PatientLight patient;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "encounter_type")
	private EncounterTypeLight encounterType;
}
