package org.openmrs.utils.odoo.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Camel strategy to set the ODOO_ID to a property in the Camel context
 * The property should be stored in the header with the name property-name
 */
@Component
public class SetOdooIdToPropertyAggregationStrategy implements AggregationStrategy {

    private static final String PROPERTY_NAME = "property-name";

    @Override
    public Exchange aggregate(final Exchange oldExchange, final Exchange newExchange) {
        List exchangeBody = newExchange.getIn().getBody(List.class);

        if (!exchangeBody.isEmpty()) {
            String propertyName = oldExchange.getIn().getHeader(PROPERTY_NAME, String.class);
            oldExchange.setProperty(propertyName, ((Map) exchangeBody.get(0)).get(propertyName));
        }

        return oldExchange;
    }
}
