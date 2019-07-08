package org.openmrs.sync.core.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "program")
@AttributeOverride(name = "id", column = @Column(name = "program_id"))
@AttributeOverride(name = "voided", column = @Column(name = "retired"))
@AttributeOverride(name = "voidReason", column = @Column(name = "retire_reason"))
@AttributeOverride(name = "dateVoided", column = @Column(name = "date_retired"))
public class ProgramLight extends LightEntity {

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "concept_id")
    private ConceptLight concept;
}
