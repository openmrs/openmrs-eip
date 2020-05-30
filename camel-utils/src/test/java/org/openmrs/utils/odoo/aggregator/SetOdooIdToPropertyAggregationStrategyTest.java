package org.openmrs.utils.odoo.aggregator;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SetOdooIdToPropertyAggregationStrategyTest {

    private SetOdooIdToPropertyAggregationStrategy strategy = new SetOdooIdToPropertyAggregationStrategy();

    @Test
    public void aggregate_should_put_odoo_id_to_property() {
        // Given
        CamelContext camelContext = new DefaultCamelContext();
        Exchange oldExchange = new DefaultExchange(camelContext);
        Exchange newExchange = new DefaultExchange(camelContext);
        Map<String, String> map = new HashMap<>();
        map.put("ODOO_ID", "odooId");
        map.put("PERSON_ID", "personId");
        map.put("ID", "id");
        oldExchange.getIn().setHeader("property-name", "ODOO_ID");
        newExchange.getIn().setBody(Collections.singletonList(map));

        // When
        strategy.aggregate(oldExchange, newExchange);

        // Then
        assertEquals("odooId", oldExchange.getProperty("ODOO_ID"));
    }

    @Test
    public void aggregate_should_put_nothing_in_property_if_exchange_empty() {
        // Given
        CamelContext camelContext = new DefaultCamelContext();
        Exchange oldExchange = new DefaultExchange(camelContext);
        Exchange newExchange = new DefaultExchange(camelContext);
        newExchange.getIn().setBody(Collections.emptyList());
        oldExchange.getIn().setHeader("property-name", "ODOO_ID");

        // When
        strategy.aggregate(oldExchange, newExchange);

        // Then
        assertNull(oldExchange.getProperty("odoo-person-id"));
    }
}
