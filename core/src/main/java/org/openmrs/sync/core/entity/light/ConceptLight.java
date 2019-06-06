package org.openmrs.sync.core.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.core.entity.BaseEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "concept")
@AttributeOverride(name = "id", column = @Column(name = "concept_id"))
public class ConceptLight extends BaseEntity {

    @NotNull
    @Column(name = "datatype_id")
    private Long datatypeId;

    @NotNull
    @Column(name = "class_id")
    private Long classId;

    @NotNull
    @Column(name = "creator")
    private Long creator;

    @NotNull
    @Column(name = "date_created")
    private LocalDateTime dateCreated;

    public ConceptLight() {}
}
