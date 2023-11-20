package org.openmrs.eip.component.entity.light;

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
public class PatientProgramLight extends VoidableLightEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private PatientLight patient;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "program_id")
	private ProgramLight program;
}
