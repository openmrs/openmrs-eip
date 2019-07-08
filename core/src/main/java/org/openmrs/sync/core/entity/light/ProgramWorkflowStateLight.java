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
@AttributeOverride(name = "voided", column = @Column(name = "retired"))
@AttributeOverride(name = "voidReason", column = @Column(name = "retire_reason"))
@AttributeOverride(name = "dateVoided", column = @Column(name = "date_retired"))
@AttributeOverride(name = "voidedBy", column = @Column(name = "retired_by"))
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
