package org.openmrs.utils.odoo;

import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderStateEnum;

public enum ErpWorkOrderActionEnum {

    START(ErpWorkOrderStateEnum.PROGRESS),
    CLOSE(ErpWorkOrderStateEnum.DONE),
    PAUSE(ErpWorkOrderStateEnum.PROGRESS),
    CANCEL(ErpWorkOrderStateEnum.READY);

    private ErpWorkOrderStateEnum resultingWorkOrderState;

    ErpWorkOrderActionEnum(final ErpWorkOrderStateEnum resultingWorkOrderState) {
        this.resultingWorkOrderState = resultingWorkOrderState;
    }

    public ErpWorkOrderStateEnum getResultingWorkOrderState() {
        return resultingWorkOrderState;
    }
}
