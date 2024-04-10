package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.ConceptLight;
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
	
	@ManyToOne
	@JoinColumn(name = "encounter_id")
	private EncounterLight encounter;
	
	@Column(name = "form_namespace_and_path")
	private String formNamespaceAndPath;
}
