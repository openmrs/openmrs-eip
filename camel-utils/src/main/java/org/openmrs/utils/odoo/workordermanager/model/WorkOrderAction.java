package org.openmrs.utils.odoo.workordermanager.model;

import lombok.Builder;
import lombok.Value;
import org.openmrs.utils.odoo.WorkOrderActionEnum;

@Value
@Builder
public final class WorkOrderAction {

    private WorkOrderActionEnum action;

    private WorkOrder workOrder;
}
