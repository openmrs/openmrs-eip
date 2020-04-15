package org.openmrs.utils.odoo.workordermanager;

import lombok.extern.slf4j.Slf4j;
import org.openmrs.utils.odoo.ErpWorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrder;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderAction;
import org.openmrs.utils.odoo.workordermanager.model.ErpWorkOrderStateEnum;
import org.openmrs.utils.odoo.workordermanager.rule.ErpWorkOrderStatusTransitionRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The {@link ErpWorkOrderStatusManager} is due to apply the action to the {@link ErpWorkOrder} with the given sequenceNumberIndex
 * To do so, the list of {@link ErpWorkOrder} in parameter is sorted so that the sequenceNumberIndex corresponds to the
 * correct {@link ErpWorkOrder}
 * It is possible that external actions were applied to the {@link ErpWorkOrder} via Odoo application between two work order updates.
 * A list of {@link ErpWorkOrderStatusTransitionRule} is applied after the action to the other work orders so that their states
 * remain consistent between each other
 * Basically, the resulting work order states will be as follows:
 *
 * [DONE, DONE, ..., PROGRESS, READY, ...,READY]
 */
@Slf4j
public class ErpWorkOrderStatusManager {

    private ErpWorkOrderActionEnum action;

    private int sequenceNumberIndex;

    private List<ErpWorkOrderStatusTransitionRule> rules;

    public ErpWorkOrderStatusManager(final ErpWorkOrderActionEnum action,
                                     final int sequenceNumberIndex,
                                     final List<ErpWorkOrderStatusTransitionRule> rules) {
        this.action = action;
        this.sequenceNumberIndex = sequenceNumberIndex;
        this.rules = rules;
    }

    /**
     * From a given list of {@link ErpWorkOrder}, returns a list of {@link ErpWorkOrderAction} encapsulating
     * a {@link ErpWorkOrder} and an {@link ErpWorkOrderActionEnum} to apply to it to keep the list of {@link ErpWorkOrder}
     * in a consistent state
     *
     * @param workOrders the list of actions to apply
     * @return a list of {@link ErpWorkOrderAction}
     */
    public List<ErpWorkOrderAction> manageStatus(final List<ErpWorkOrder> workOrders) {
        List<ErpWorkOrder> sortedWorkOrders = new ErpWorkOrderSorter(workOrders).sort();

        ErpWorkOrder workOrder = sortedWorkOrders.get(sequenceNumberIndex - 1);
        workOrder.setState(this.action.getResultingWorkOrderState());
        ErpWorkOrderAction workOrderAction = ErpWorkOrderAction.builder()
                .action(this.action)
                .workOrder(workOrder)
                .build();

        List<ErpWorkOrderAction> actions = new ArrayList<>();
        actions.add(workOrderAction);

        actions.addAll(
                IntStream.range(0, sortedWorkOrders.size())
                        //Exclude current work order because status was already changed
                        //Any earlier finished work order because odoo won't allow us change it anyways
                        .filter(i -> canProcess(i, sortedWorkOrders))
                        .mapToObj(i -> new ErpWorkOrderStatusTransitionContext(sortedWorkOrders, i, sequenceNumberIndex - 1))
                        .map(this::changeStatusIfNeeded)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList())
        );

        return actions;
    }

    /**
     * Determines if a work order can be processed or not
     *
     * @param i                the index of the work order to check
     * @param sortedWorkOrders the lst of work orders
     * @return true if the work order can be processed otherwise false
     */
    private boolean canProcess(int i, List<ErpWorkOrder> sortedWorkOrders) {
        if (i != sequenceNumberIndex - 1 && sortedWorkOrders.get(i).getState() != ErpWorkOrderStateEnum.DONE) {
            return true;
        } else if (sortedWorkOrders.get(i).getState() == ErpWorkOrderStateEnum.DONE && i > sequenceNumberIndex - 1) {
            return true;
        }

        return false;
    }

    private ErpWorkOrderAction changeStatusIfNeeded(final ErpWorkOrderStatusTransitionContext context) {
        ErpWorkOrderAction toReturn = null;
        for (ErpWorkOrderStatusTransitionRule rule : rules) {
            if (rule.workOrderMatchesCondition(context)) {
                ErpWorkOrderActionEnum resultingAction = rule.getAction(context);
                log.warn("Work order '" + context.getWorkOrder().getName() + "' is in inconsistant state => being transition to " + resultingAction.getResultingWorkOrderState());
                context.getWorkOrder().setState(resultingAction.getResultingWorkOrderState());
                toReturn = ErpWorkOrderAction.builder()
                        .action(rule.getAction(context))
                        .workOrder(context.getWorkOrder())
                        .build();
            }
        }
        return toReturn;
    }
}
