package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.utils.DateUtils;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class AuditableEntity extends BaseEntity {

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
        AuditableEntity auditableEntity = (AuditableEntity) entity;
        List<LocalDateTime> datesToCheck = Arrays.asList(
                auditableEntity.getDateCreated(),
                auditableEntity.getDateChanged(),
                auditableEntity.getDateVoided());
        boolean dateCreatedAfter = DateUtils.isDateAfterAtLeastOneInList(getDateCreated(), datesToCheck);
        boolean dateChangedAfter = DateUtils.isDateAfterAtLeastOneInList(getDateChanged(), datesToCheck);
        boolean dateVoidedAfter = DateUtils.isDateAfterAtLeastOneInList(getDateVoided(), datesToCheck);
        return dateCreatedAfter || dateChangedAfter || dateVoidedAfter;
    }
}
