package org.openmrs.sync.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
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
}
