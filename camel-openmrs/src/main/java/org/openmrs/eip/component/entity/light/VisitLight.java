package org.openmrs.eip.component.entity.light;

import java.time.LocalDateTime;

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
@Table(name = "visit")
@AttributeOverride(name = "id", column = @Column(name = "visit_id"))
public class VisitLight extends VoidableLightEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private PatientLight patient;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "visit_type_id")
	private VisitTypeLight visitType;
	
	@NotNull
	@Column(name = "date_started")
	private LocalDateTime dateStarted;
}
