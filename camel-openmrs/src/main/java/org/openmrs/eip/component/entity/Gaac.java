package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import org.openmrs.eip.component.entity.light.GaacAffinityTypeLight;
import org.openmrs.eip.component.entity.light.LocationLight;
import org.openmrs.eip.component.entity.light.PatientLight;

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
@Table(name = "gaac")
@AttributeOverride(name = "id", column = @Column(name = "gaac_id"))
public class Gaac extends BaseChangeableDataEntity {
	
	@NotNull
	@Column(name = "name")
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "gaac_identifier")
	private String gaac_identifier;
	
	@NotNull
	@Column(name = "start_date")
	protected LocalDateTime startDate;
	
	@Column(name = "end_date")
	protected LocalDateTime endDate;
	
	@ManyToOne
	@JoinColumn(name = "focal_patient_id")
	private PatientLight focalPatient;
	
	@ManyToOne
	@JoinColumn(name = "affinity_type")
	private GaacAffinityTypeLight affinityType;
	
	@ManyToOne
	@JoinColumn(name = "location_id")
	private LocationLight location;
	
	@Column(name = "crumbled")
	private Boolean crumbled;
	
	@Column(name = "reason_crumbled")
	private String reasonCrumbled;
	
	@Column(name = "date_crumbled")
	protected LocalDateTime dateCrumbled;
}
