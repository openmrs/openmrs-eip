package org.openmrs.sync.core.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class AuditableEntity extends BaseEntity {

    @JoinColumn(name = "creator")
    @ManyToOne
    private User creator;

    @NotNull
    private LocalDateTime dateCreated;

    @JoinColumn(name = "changed_by")
    @ManyToOne
    private User changedBy;

    private LocalDateTime dateChanged;

    @NotNull
    private boolean voided;

    @JoinColumn(name = "voided_by")
    @ManyToOne
    private User voidedBy;

    private LocalDateTime dateVoided;

    private String voidReason;
}
