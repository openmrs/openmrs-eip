package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "drug")
@AttributeOverride(name = "id", column = @Column(name = "drug_id"))
public class DrugLight extends RetireableLightEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "concept_id")
    private ConceptLight concept;
}
