package org.openmrs.eip.component.entity;

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
@Table(name = "encounter_diagnosis")
@AttributeOverride(name = "id", column = @Column(name = "diagnosis_id"))
public class EncounterDiagnosis extends BaseChangeableDataEntity {
	
	@ManyToOne
	@JoinColumn(name = "diagnosis_coded")
	private ConceptLight diagnosisCoded;
	
	@Column(name = "diagnosis_non_coded")
	private String diagnosisNonCoded;
	
	@ManyToOne
	@JoinColumn(name = "diagnosis_coded_name")
	private ConceptNameLight diagnosisCodedName;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "encounter_id")
	private EncounterLight encounter;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private PatientLight patient;
	
	@ManyToOne
	@JoinColumn(name = "condition_id")
	private ConditionLight condition;
	
	@NotNull
	@Column(name = "certainty")
	private String certainty;
	
	@NotNull
	@Column(name = "rank")
	private int rank;
}
