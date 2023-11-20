package org.openmrs.eip.component.entity.light;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient")
@PrimaryKeyJoinColumn(name = "patient_id")
public class PatientLight extends PersonLight {
	
	@NotNull
	@Column(name = "allergy_status")
	private String allergyStatus;
	
	@NotNull
	@Column(name = "creator")
	private Long patientCreator;
	
	@NotNull
	@Column(name = "date_created")
	private LocalDateTime patientDateCreated;
	
	@NotNull
	@Column(name = "voided")
	private boolean patientVoided;
	
	@Column(name = "voided_by")
	private Long patientVoidedBy;
	
	@Column(name = "date_voided")
	private LocalDateTime patientDateVoided;
	
	@Column(name = "void_reason")
	private String patientVoidReason;
	
}
