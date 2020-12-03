package org.openmrs.eip.component.entity;

import lombok.EqualsAndHashCode;
import org.openmrs.eip.component.entity.light.ErpWorkOrderLight;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "icrc_erp_work_order_state")
@AttributeOverride(name = "id", column = @Column(name = "erp_work_order_state_id"))
public class ErpWorkOrderState extends BaseDataEntity {

    @NotNull
    @ManyToOne
    @JoinColumn(name = "erp_work_order_id")
    private ErpWorkOrderLight erpWorkOrder;

    @NotNull
    @Column(name = "wo_action")
    private String action;

    /**
     * Gets the erpWorkOrder
     *
     * @return the erpWorkOrder
     */
    public ErpWorkOrderLight getErpWorkOrder() {
        return erpWorkOrder;
    }

    /**
     * Sets the erpWorkOrder
     *
     * @param erpWorkOrder the erpWorkOrder to set
     */
    public void setErpWorkOrder(ErpWorkOrderLight erpWorkOrder) {
        this.erpWorkOrder = erpWorkOrder;
    }

    /**
     * Gets the action
     *
     * @return the action
     */
    public String getAction() {
        return action;
    }

    /**
     * Sets the action
     *
     * @param action the action to set
     */
    public void setAction(String action) {
        this.action = action;
    }

}
