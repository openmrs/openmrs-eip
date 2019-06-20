package org.openmrs.sync.core.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "program_workflow_state")
@AttributeOverride(name = "id", column = @Column(name = "program_workflow_state_id"))
public class ProgramWorkflowStateLight extends LightEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "program_workflow_id")
    private ProgramWorkflowLight programWorkflow;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "concept_id")
    private ConceptLight concept;
}
