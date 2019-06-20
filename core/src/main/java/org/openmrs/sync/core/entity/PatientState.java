package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.core.entity.light.PatientProgramLight;
import org.openmrs.sync.core.entity.light.ProgramWorkflowStateLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patient_state")
@AttributeOverride(name = "id", column = @Column(name = "patient_state_id"))
public class PatientState extends AuditableEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "patient_program_id")
    private PatientProgramLight patientProgram;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "state")
    private ProgramWorkflowStateLight state;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;
}
