package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
@Table(name = "concept")
@AttributeOverride(name = "id", column = @Column(name = "concept_id"))
public class ConceptLight extends RetireableLightEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "datatype_id")
	private ConceptDatatypeLight datatype;
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "class_id")
	private ConceptClassLight conceptClass;
	
	@NotNull
	@Column(name = "is_set")
	private boolean set;
	
}
