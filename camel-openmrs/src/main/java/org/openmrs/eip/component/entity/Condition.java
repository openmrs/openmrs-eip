package org.openmrs.eip.component.entity;

import java.time.LocalDateTime;

import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.ConceptNameLight;
import org.openmrs.eip.component.entity.light.ConditionLight;
import org.openmrs.eip.component.entity.light.EncounterLight;
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
@Table(name = "conditions")
@AttributeOverride(name = "id", column = @Column(name = "condition_id"))
public class Condition extends BaseChangeableDataEntity {
	
	@Column(name = "additional_detail")
	private String additionalDetail;
	
	@ManyToOne
	@JoinColumn(name = "previous_version")
	private ConditionLight previousVersion;
	
	@ManyToOne
	@JoinColumn(name = "condition_coded")
	private ConceptLight conditionCoded;
	
	@Column(name = "condition_non_coded")
	private String conditionNonCoded;
	
	@ManyToOne
	@JoinColumn(name = "condition_coded_name")
	private ConceptNameLight conditionCodedName;
	
	@NotNull
	@Column(name = "clinical_status")
	private String clinicalStatus;
	
	@Column(name = "verification_status")
	private String verificationStatus;
	
	@Column(name = "onset_date")
	private LocalDateTime onsetDate;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private PatientLight patient;
	
	@Column(name = "end_date")
	private LocalDateTime endDate;
	
	@ManyToOne
	@JoinColumn(name = "encounter_id")
	private EncounterLight encounter;
	
	@Column(name = "form_namespace_and_path")
	private String formNamespaceAndPath;
	
}
