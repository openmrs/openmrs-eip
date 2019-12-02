package org.openmrs.utils.odoo.model;

import lombok.Builder;
import lombok.Value;
import org.openmrs.utils.odoo.ObsActionEnum;

@Value
@Builder
public final class WorkOrderAction {

    private ObsActionEnum action;

    private WorkOrder workOrder;
}
