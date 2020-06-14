package org.openmrs.eip.component.entity.light;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "conditions")
@AttributeOverride(name = "id", column = @Column(name = "condition_id"))
public class ConditionLight extends VoidableLightEntity {

    @NotNull
    @Column(name = "clinical_status")
    private String clinicalStatus;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientLight patient;
}
