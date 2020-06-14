package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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

    @Column(name = "retired")
    private boolean retired;

    @Override
    public void setMuted(final boolean muted) {
        this.retired = muted;
    }

    @Override
    public void setDateMuted(final LocalDateTime dateMuted) {
        // Not applicable
    }

    @Override
    public void setMuteReason(final String muteReason) {
        // Not applicable
    }

    @Override
    public void setMutedBy(final Long mutedBy) {
        // Not applicable
    }
}
