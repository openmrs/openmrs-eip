package org.openmrs.utils.odoo;

import org.openmrs.utils.odoo.workordermanager.model.WorkOrderStateEnum;

public enum ObsActionEnum {

    START(WorkOrderStateEnum.PROGRESS),
    CLOSE(WorkOrderStateEnum.DONE),
    PAUSE(WorkOrderStateEnum.PROGRESS),
    CANCEL(WorkOrderStateEnum.READY);

    private WorkOrderStateEnum resultingWorkOrderState;

    ObsActionEnum(final WorkOrderStateEnum resultingWorkOrderState) {
        this.resultingWorkOrderState = resultingWorkOrderState;
    }

    public WorkOrderStateEnum getResultingWorkOrderState() {
        return resultingWorkOrderState;
    }
}
