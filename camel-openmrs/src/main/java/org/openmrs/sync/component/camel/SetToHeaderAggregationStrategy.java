package org.openmrs.sync.component.camel;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class SetToHeaderAggregationStrategy implements AggregationStrategy {

    @Override
    public Exchange aggregate(final Exchange oldExchange, final Exchange newExchange) {
        oldExchange.getIn().setHeader("access-token", new JSONObject(newExchange.getIn().getBody(String.class)).get("access_token"));
        return oldExchange;
    }
}
