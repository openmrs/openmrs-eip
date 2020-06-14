package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "obs")
@AttributeOverride(name = "id", column = @Column(name = "obs_id"))
public class ObservationLight extends VoidableLightEntity {

    @NotNull
    @Column(name = "obs_datetime")
    private LocalDateTime obsDatetime;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "concept_id")
    private ConceptLight concept;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "person_id")
    private PersonLight person;
}
