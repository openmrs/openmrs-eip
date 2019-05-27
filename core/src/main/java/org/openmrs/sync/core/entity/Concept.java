package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "concept")
@AttributeOverrides(
        {
                @AttributeOverride(name = "id", column = @Column(name = "concept_id"))
        }
)
public class Concept extends BaseEntity {

    @NotNull
    private int datatypeId;

    @NotNull
    private int classId;

    @NotNull
    private Integer creator;

    @NotNull
    private LocalDateTime dateCreated;

    public Concept() {}
}
