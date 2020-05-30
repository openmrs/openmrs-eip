package org.openmrs.utils.odoo.aggregator;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ErpWorkOrderSequenceAggregationStrategyTest {

    ErpWorkOrderSequenceAggregationStrategy strategy = new ErpWorkOrderSequenceAggregationStrategy();

    @Test
    public void aggregate_should_return_oldExchange_with_newExchange_properties() {
        // Given
        CamelContext context = new DefaultCamelContext();
        Exchange oldExchange = new DefaultExchange(context);
        Exchange newExchange = new DefaultExchange(context);
        newExchange.setProperty("property1", 1);
        newExchange.setProperty("property2", 2);

        // When
        Exchange result = strategy.aggregate(oldExchange, newExchange);

        // Then
        assertEquals(oldExchange, result);
        assertEquals(1, oldExchange.getProperty("property1"));
        assertEquals(2, oldExchange.getProperty("property2"));
    }

    @Test
    public void aggregate_should_return_new_exchange() {
        // Given
        CamelContext context = new DefaultCamelContext();
        Exchange oldExchange = null;
        Exchange newExchange = new DefaultExchange(context);

        // When
        Exchange result = strategy.aggregate(oldExchange, newExchange);

        // Then
        assertEquals(newExchange, result);
    }
}
