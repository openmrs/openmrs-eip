package org.openmrs.utils.odoo.workordermanager;

import org.junit.Before;
import org.junit.Test;
import org.openmrs.utils.odoo.ErpWorkOrderActionEnum;
import org.openmrs.utils.odoo.workordermanager.rule.ErpWorkOrderStatusTransitionRule;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class ErpWorkOrderStatusManagerFactoryTest {

    private List<ErpWorkOrderStatusTransitionRule> rules;

    private ErpWorkOrderStatusManagerFactory factory;

    @Before
    public void init() {

        factory = new ErpWorkOrderStatusManagerFactory(rules);
    }

    @Test
    public void createManager_should_return_manager() {
        // Given
        ErpWorkOrderActionEnum state = ErpWorkOrderActionEnum.PAUSE;
        Integer sequenceNumber = 1;

        // When
        ErpWorkOrderStatusManager manager = factory.createManager(state, sequenceNumber);

        // Then
        assertNotNull(manager);
    }
}
