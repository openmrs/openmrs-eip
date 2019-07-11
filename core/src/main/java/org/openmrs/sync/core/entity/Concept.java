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
public class Concept extends MetaDataEntity {

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
