package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import org.openmrs.eip.component.entity.light.EncounterTypeLight;
import org.openmrs.eip.component.entity.light.FormLight;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.VisitLight;

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
@Table(name = "encounter")
@AttributeOverride(name = "id", column = @Column(name = "encounter_id"))
public class Encounter extends BaseChangeableDataEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "encounter_type")
	private EncounterTypeLight encounterType;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private PatientLight patient;
	
	@ManyToOne
	@JoinColumn(name = "location_id")
	private LocationLight location;
	
	@ManyToOne
	@JoinColumn(name = "form_id")
	private FormLight form;
	
	@NotNull
	@Column(name = "encounter_datetime")
	private LocalDateTime encounterDatetime;
	
	@ManyToOne
	@JoinColumn(name = "visit_id")
	private VisitLight visit;
}
