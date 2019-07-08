package org.openmrs.sync.core.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "concept")
@AttributeOverride(name = "id", column = @Column(name = "concept_id"))
@AttributeOverride(name = "voided", column = @Column(name = "retired"))
@AttributeOverride(name = "voidReason", column = @Column(name = "retire_reason"))
@AttributeOverride(name = "dateVoided", column = @Column(name = "date_retired"))
public class ConceptLight extends LightEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "datatype_id")
    private ConceptDatatypeLight datatype;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "class_id")
    private ConceptClassLight conceptClass;
}
