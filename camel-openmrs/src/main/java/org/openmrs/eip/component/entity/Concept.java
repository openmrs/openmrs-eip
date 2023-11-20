package org.openmrs.eip.component.entity;

import org.openmrs.eip.component.entity.light.ConceptClassLight;
import org.openmrs.eip.component.entity.light.ConceptDatatypeLight;

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
@Table(name = "concept")
@AttributeOverride(name = "id", column = @Column(name = "concept_id"))
public class Concept extends BaseChangeableMetaDataEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "datatype_id")
	private ConceptDatatypeLight datatype;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "class_id")
	private ConceptClassLight conceptClass;
	
	@Column(name = "short_name")
	private String shortName;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "form_text")
	private String formText;
	
	@Column(name = "version")
	private String version;
	
	@NotNull
	@Column(name = "is_set")
	private boolean isSet;
}
