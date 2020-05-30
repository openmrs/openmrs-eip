package org.openmrs.utils.odoo.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

/**
 * Camel strategy to copy properties from newExchange and put them into oldExchange
 */
@Component
public class ErpWorkOrderSequenceAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(final Exchange oldExchange, final Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }

        newExchange.getProperties().forEach(oldExchange::setProperty);

        return oldExchange;
    }
}
