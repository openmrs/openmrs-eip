package org.openmrs.sync.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.entity.light.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "encounter_diagnosis")
@AttributeOverride(name = "id", column = @Column(name = "diagnosis_id"))
public class EncounterDiagnosis extends AuditableEntity {

    @ManyToOne
    @JoinColumn(name = "diagnosis_coded")
    private ConceptLight diagnosisCoded;

    @Column(name = "diagnosis_non_coded")
    private String diagnosisNonCoded;

    @ManyToOne
    @JoinColumn(name = "diagnosis_coded_name")
    private ConceptNameLight diagnosisCodedName;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "encounter_id")
    private EncounterLight encounter;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientLight patient;

    @ManyToOne
    @JoinColumn(name = "condition_id")
    private ConditionLight condition;

    @NotNull
    @Column(name = "certainty")
    private String certainty;

    @NotNull
    @Column(name = "rank")
    private int rank;
}
