package org.openmrs.utils.odoo.workordermanager;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.utils.odoo.ObsActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrderAction;
import org.openmrs.utils.odoo.workordermanager.model.WorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.rule.WorkOrderStatusTransitionRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The {@link WorkOrderStatusManager} is due to apply the action to the {@link WorkOrder} with the given sequenceNumberIndex
 * To do so, the list of {@link WorkOrder} in parameter is sorted so that the sequenceNumberIndex corresponds to the
 * correct {@link WorkOrder}
 * It is possible that external actions were applied to the {@link WorkOrder} via Odoo application between two work order updates.
 * A list of {@link WorkOrderStatusTransitionRule} is applied after the action to the other work orders so that their states
 * remain consistent between each other
 * Basically, the resulting work order states will be as follows:
 *
 * [DONE, DONE, ..., PROGRESS, READY, ...,READY]
 */
@Slf4j
public class WorkOrderStatusManager {

    private ObsActionEnum action;

    private int sequenceNumberIndex;

    private List<WorkOrderStatusTransitionRule> rules;

    public WorkOrderStatusManager(final ObsActionEnum action,
                                  final int sequenceNumberIndex,
                                  final List<WorkOrderStatusTransitionRule> rules) {
        this.action = action;
        this.sequenceNumberIndex = sequenceNumberIndex;
        this.rules = rules;
    }

    /**
     * From a given list of {@link WorkOrder}, returns a list of {@link WorkOrderAction} encapsulating
     * a {@link WorkOrder} and an {@link ObsActionEnum} to apply to it to keep the list of {@link WorkOrder}
     * in a consistent state
     *
     * @param workOrders the list of actions to apply
     * @return a list of {@link WorkOrderAction}
     */
    public List<WorkOrderAction> manageStatus(final List<WorkOrder> workOrders) {
        List<WorkOrder> sortedWorkOrders = new WorkOrderSorter(workOrders).sort();

        WorkOrder workOrder = sortedWorkOrders.get(sequenceNumberIndex - 1);
        workOrder.setState(this.action.getResultingWorkOrderState());
        WorkOrderAction workOrderAction = WorkOrderAction.builder()
                .action(this.action)
                .workOrder(workOrder)
                .build();

        List<WorkOrderAction> actions = new ArrayList<>();
        actions.add(workOrderAction);

        actions.addAll(
                IntStream.range(0, sortedWorkOrders.size())
                        //Exclude current work order because status was already changed
                        //Any finished work order because odoo won't allow us change it anyways
                        .filter(i -> i != sequenceNumberIndex - 1 && sortedWorkOrders.get(i).getState() != WorkOrderStateEnum.DONE)
                        .mapToObj(i -> new WorkOrderStatusTransitionContext(sortedWorkOrders, i, sequenceNumberIndex - 1))
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
                log.warn("Work order '" + context.getWorkOrder().getName() + "' is in inconsistant state => being transition to " + resultingAction.getResultingWorkOrderState());
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
