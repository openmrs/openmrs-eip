package org.openmrs.sync.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.entity.light.ConceptLight;
import org.openmrs.sync.component.entity.light.LocationLight;
import org.openmrs.sync.component.entity.light.PatientLight;
import org.openmrs.sync.component.entity.light.ProgramLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient_program")
@AttributeOverride(name = "id", column = @Column(name = "patient_program_id"))
public class PatientProgram extends AuditableEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientLight patient;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "program_id")
    private ProgramLight program;

    @Column(name = "date_enrolled")
    private LocalDateTime dateEnrolled;

    @Column(name = "date_completed")
    private LocalDateTime dateCompleted;

    @ManyToOne
    @JoinColumn(name = "location_id")
    private LocationLight location;

    @ManyToOne
    @JoinColumn(name = "outcome_concept_id")
    private ConceptLight outcomeConcept;
}
