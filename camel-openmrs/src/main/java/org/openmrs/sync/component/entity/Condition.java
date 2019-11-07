package org.openmrs.sync.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.entity.light.*;
import org.openmrs.sync.component.utils.DateUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

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

    @Override
    public boolean wasModifiedAfter(final BaseEntity entity) {
        Condition condition = (Condition) entity;
        List<LocalDateTime> datesToCheck = Arrays.asList(
                condition.getDateCreated(),
                condition.getDateVoided());
        boolean dateCreatedAfter = DateUtils.isDateAfterAtLeastOneInList(getDateCreated(), datesToCheck);
        boolean dateVoidedAfter = DateUtils.isDateAfterAtLeastOneInList(getDateVoided(), datesToCheck);
        return dateCreatedAfter || dateVoidedAfter;
    }
}
