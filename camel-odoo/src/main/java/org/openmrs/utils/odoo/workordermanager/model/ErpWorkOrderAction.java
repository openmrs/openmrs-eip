package org.openmrs.utils.odoo.workordermanager.model;

import lombok.Builder;
import lombok.Value;
import org.openmrs.utils.odoo.ErpWorkOrderActionEnum;

@Value
@Builder
public final class ErpWorkOrderAction {

    private ErpWorkOrderActionEnum action;

    private ErpWorkOrder workOrder;
}
