package org.openmrs.eip.component.entity.light;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "order_frequency")
@AttributeOverride(name = "id", column = @Column(name = "order_frequency_id"))
@EqualsAndHashCode(callSuper = true)
public class OrderFrequencyLight extends RetireableLightEntity {
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "concept_id", nullable = false)
	private ConceptLight concept;
	
	/**
	 * Gets the concept
	 *
	 * @return the concept
	 */
	public ConceptLight getConcept() {
		return concept;
	}
	
	/**
	 * Sets the concept
	 *
	 * @param concept the concept to set
	 */
	public void setConcept(ConceptLight concept) {
		this.concept = concept;
	}
	
}
