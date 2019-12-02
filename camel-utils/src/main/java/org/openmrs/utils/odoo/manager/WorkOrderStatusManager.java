package org.openmrs.utils.odoo.manager;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.utils.odoo.ObsActionEnum;
import org.openmrs.utils.odoo.model.WorkOrder;
import org.openmrs.utils.odoo.manager.rule.WorkOrderStatusTransitionRule;
import org.openmrs.utils.odoo.model.WorkOrderAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class WorkOrderStatusManager {

    private ObsActionEnum action;

    private int sequenceNumber;

    private List<WorkOrderStatusTransitionRule> rules;

    public WorkOrderStatusManager(final ObsActionEnum action,
                                  final int sequenceNumber,
                                  final List<WorkOrderStatusTransitionRule> rules) {
        this.action = action;
        this.sequenceNumber = sequenceNumber;
        this.rules = rules;
    }

    public List<WorkOrderAction> manageStatus(final List<WorkOrder> workOrders) {
        List<WorkOrder> sortedWorkOrders = new WorkOrderSorter(workOrders).sort();

        WorkOrder workOrder = sortedWorkOrders.get(sequenceNumber - 1);
        workOrder.setState(this.action.getResultingWorkOrderState());
        WorkOrderAction workOrderAction = WorkOrderAction.builder()
                .action(this.action)
                .workOrder(workOrder)
                .build();

        List<WorkOrderAction> actions = new ArrayList<>();
        actions.add(workOrderAction);

        actions.addAll(
                IntStream.range(0, sortedWorkOrders.size())
                        .filter(i -> i != sequenceNumber - 1) // Exclude current work order because status was already changed
                        .mapToObj(i -> new WorkOrderStatusTransitionContext(sortedWorkOrders, i, sequenceNumber - 1))
                        .map(this::changeStatusIfNeeded)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );

        return actions;
    }

    private WorkOrderAction changeStatusIfNeeded(final WorkOrderStatusTransitionContext context) {
        WorkOrderAction toReturn = null;
        for (WorkOrderStatusTransitionRule rule : rules) {
            if (rule.workOrderMatchesCondition(context)) {
                ObsActionEnum resultingAction = rule.getAction(context);
                log.warn("Workorder '" + context.getWorkOrder().getName() + "' in inconstant state => transition to " + resultingAction.getResultingWorkOrderState());
                context.getWorkOrder().setState(resultingAction.getResultingWorkOrderState());
                toReturn = WorkOrderAction.builder()
                        .action(rule.getAction(context))
                        .workOrder(context.getWorkOrder())
                        .build();
            }
        }
        return toReturn;
    }
}
