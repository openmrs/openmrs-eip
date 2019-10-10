package org.openmrs.sync.component.camel;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SetOdooIdToPropertyAggregationStrategy implements AggregationStrategy {

    private static final String ODOO_ID_FIELD_NAME = "ODOO_ID";
    private static final String ODOO_ID_PROP_NAME = "odoo-person-id";

    @Override
    public Exchange aggregate(final Exchange oldExchange, final Exchange newExchange) {
        List exchangeBody = newExchange.getIn().getBody(List.class);

        if (!exchangeBody.isEmpty()) {
            oldExchange.setProperty(ODOO_ID_PROP_NAME, ((Map) exchangeBody.get(0)).get(ODOO_ID_FIELD_NAME));
        }

        return oldExchange;
    }
}
