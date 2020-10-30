package org.openmrs.eip.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.UserLight;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

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
}
