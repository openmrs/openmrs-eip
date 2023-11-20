package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.ProgramLight;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient_program")
@AttributeOverride(name = "id", column = @Column(name = "patient_program_id"))
public class PatientProgram extends BaseChangeableDataEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private PatientLight patient;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "program_id")
	private ProgramLight program;
	
	@Column(name = "date_enrolled")
	private LocalDateTime dateEnrolled;
	
	@Column(name = "date_completed")
	private LocalDateTime dateCompleted;
	
	@ManyToOne
	@JoinColumn(name = "location_id")
	private LocationLight location;
	
	@ManyToOne
	@JoinColumn(name = "outcome_concept_id")
	private ConceptLight outcomeConcept;
}
