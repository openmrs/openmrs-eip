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
public abstract class MetaDataEntity extends BaseEntity {

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

    @ManyToOne
    @JoinColumn(name = "retired_by")
    private UserLight retiredBy;

    @Column(name = "date_retired")
    private LocalDateTime dateRetired;

    @Column(name = "retire_reason")
    private String retireReason;

    @NotNull
    @Column(name = "retired")
    private boolean retired;
}
