package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.VisitTypeLight;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "visit")
@AttributeOverride(name = "id", column = @Column(name = "visit_id"))
public class Visit extends BaseChangeableDataEntity {
	
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
	
	@Column(name = "date_stopped")
	private LocalDateTime dateStopped;
	
	@ManyToOne
	@JoinColumn(name = "indication_concept_id")
	private ConceptLight indicationConcept;
	
	@ManyToOne
	@JoinColumn(name = "location_id")
	private LocationLight location;
}
