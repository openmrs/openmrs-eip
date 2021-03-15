package org.openmrs.utils.odoo.aggregator;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SetOdooTokenToHeaderAggregationStrategyTest {

    private SetOdooTokenToHeaderAggregationStrategy strategy = new SetOdooTokenToHeaderAggregationStrategy();

    @Test
    public void aggregate_should_put_token_to_header() {
        // Given
        CamelContext camelContext = new DefaultCamelContext();
        Exchange oldExchange = new DefaultExchange(camelContext);
        Exchange newExchange = new DefaultExchange(camelContext);
        String tokenJson = "{\"access_token\":\"TOKEN\"}";
        newExchange.getIn().setBody(tokenJson);

        // When
        strategy.aggregate(oldExchange, newExchange);

        // Then
        assertEquals("TOKEN", oldExchange.getIn().getHeader("access-token"));
    }
}
