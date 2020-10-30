package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.ConceptLight;
import org.openmrs.eip.component.entity.light.ConceptNameLight;
import org.openmrs.eip.component.entity.light.ConditionLight;
import org.openmrs.eip.component.entity.light.PatientLight;
import org.openmrs.eip.component.entity.light.UserLight;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "conditions")
@AttributeOverride(name = "id", column = @Column(name = "condition_id"))
public class Condition extends BaseEntity {

    @Column(name = "additional_detail")
    private String additionalDetail;

    @ManyToOne
    @JoinColumn(name = "previous_version")
    private ConditionLight previousVersion;

    @ManyToOne
    @JoinColumn(name = "condition_coded")
    private ConceptLight conditionCoded;

    @Column(name = "condition_non_coded")
    private String conditionNonCoded;

    @ManyToOne
    @JoinColumn(name = "condition_coded_name")
    private ConceptNameLight conditionCodedName;

    @NotNull
    @Column(name = "clinical_status")
    private String clinicalStatus;

    @Column(name = "verification_status")
    private String verificationStatus;

    @Column(name = "onset_date")
    private LocalDateTime onsetDate;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private PatientLight patient;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @ManyToOne
    @JoinColumn(name = "creator")
    protected UserLight creator;

    @NotNull
    @Column(name = "date_created")
    protected LocalDateTime dateCreated;

    @ManyToOne
    @JoinColumn(name = "changed_by")
    protected UserLight changedBy;

    @Column(name = "date_changed")
    protected LocalDateTime dateChanged;

    @NotNull
    @Column(name = "voided")
    protected boolean voided;

    @ManyToOne
    @JoinColumn(name = "voided_by")
    protected UserLight voidedBy;

    @Column(name = "date_voided")
    protected LocalDateTime dateVoided;

    @Column(name = "void_reason")
    protected String voidReason;
}
