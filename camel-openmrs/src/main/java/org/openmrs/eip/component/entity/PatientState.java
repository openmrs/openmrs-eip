package org.openmrs.eip.component.entity;

import java.time.LocalDate;

import org.openmrs.eip.component.entity.light.PatientProgramLight;
import org.openmrs.eip.component.entity.light.ProgramWorkflowStateLight;

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
@Table(name = "patient_state")
@AttributeOverride(name = "id", column = @Column(name = "patient_state_id"))
public class PatientState extends BaseChangeableDataEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "patient_program_id")
	private PatientProgramLight patientProgram;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "state")
	private ProgramWorkflowStateLight state;
	
	@Column(name = "start_date")
	private LocalDate startDate;
	
	@Column(name = "end_date")
	private LocalDate endDate;
}
