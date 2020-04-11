package org.openmrs.utils.odoo.workordermanager;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.utils.odoo.WorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.rule.WorkOrderStatusTransitionRule;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class WorkOrderStatusManagerFactoryTest {

    private List<WorkOrderStatusTransitionRule> rules;

    private WorkOrderStatusManagerFactory factory;

    @Before
    public void init() {

        factory = new WorkOrderStatusManagerFactory(rules);
    }

    @Test
    public void createManager_should_return_manager() {
        // Given
        WorkOrderActionEnum state = WorkOrderActionEnum.PAUSE;
        Integer sequenceNumber = 1;

        // When
        WorkOrderStatusManager manager = factory.createManager(state, sequenceNumber);

        // Then
        assertNotNull(manager);
    }
}
