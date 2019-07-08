package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.core.entity.light.ConceptClassLight;
import org.openmrs.sync.core.entity.light.ConceptDatatypeLight;

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
@AttributeOverride(name = "voidedBy", column = @Column(name = "retired_by"))
public class Concept extends AuditableEntity {

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
