package org.openmrs.utils.odoo.aggregator;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

/**
 * Camel strategy to set access token from the REST API to a property in the Camel Context
 */
@Component
public class SetOdooTokenToHeaderAggregationStrategy implements AggregationStrategy {

    private static final String HEADER_TOKEN_NAME = "access-token";
    private static final String HEADER_FIELD_NAME = "access_token";

    @Override
    public Exchange aggregate(final Exchange oldExchange, final Exchange newExchange) {
        oldExchange.getIn().setHeader(HEADER_TOKEN_NAME, new JSONObject(newExchange.getIn().getBody(String.class)).get(HEADER_FIELD_NAME));
        return oldExchange;
    }
}
