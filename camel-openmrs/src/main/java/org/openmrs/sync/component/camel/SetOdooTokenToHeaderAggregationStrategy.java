package org.openmrs.sync.component.camel;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

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
