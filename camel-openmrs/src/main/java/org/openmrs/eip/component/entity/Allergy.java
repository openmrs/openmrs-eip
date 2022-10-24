package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.PatientLight;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "allergy")
@AttributeOverride(name = "id", column = @Column(name = "allergy_id"))
public class Allergy extends BaseChangeableDataEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "patient_id")
	private PatientLight patient;
	
	@ManyToOne
	@JoinColumn(name = "severity_concept_id")
	private ConceptLight severityConcept;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "coded_allergen")
	private ConceptLight codedAllergen;
	
	@Column(name = "non_coded_allergen")
	private String nonCodedAllergen;
	
	@NotNull
	@Column(name = "allergen_type")
	private String allergenType;
	
	@Column(name = "comments")
	private String comments;
}
