package org.openmrs.eip.component.entity.light;

import lombok.EqualsAndHashCode;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

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
