package org.openmrs.sync.component.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.openmrs.sync.component.entity.light.ErpWorkOrderLight;
import org.openmrs.sync.component.entity.light.UserLight;
import org.openmrs.sync.component.utils.DateUtils;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Arrays;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "icrc_work_order_state")
@AttributeOverride(name = "id", column = @Column(name = "work_order_state_id"))
public class WorkOrderState extends BaseEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "erp_work_order_id")
    private ErpWorkOrderLight workOrder;

    @NotNull
    @Column(name = "wo_action")
    private String action;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "creator")
    protected UserLight creator;

    @NotNull
    @Column(name = "date_created")
    protected LocalDateTime dateCreated;

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
    public boolean wasModifiedAfter(BaseEntity entity) {
        WorkOrderState workOrderState = (WorkOrderState) entity;
        return DateUtils.isDateAfterAtLeastOneInList(getDateCreated(), Arrays.asList(workOrderState.getDateCreated()));
    }
}
