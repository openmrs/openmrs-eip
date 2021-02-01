package org.openmrs.eip.component.utils;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;
import org.springframework.stereotype.Component;

@Component("splitBodyConcatAggregationStrategy")
public class SplitBodyConcatAggregationStrategy implements AggregationStrategy {

	@Override
	public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
		
		if (oldExchange == null) {
			return newExchange;
		}
		
		String combinedBodyString = oldExchange.getIn().getBody(String.class) + "," + newExchange.getIn().getBody(String.class);
		oldExchange.getIn().setBody(combinedBodyString);
		
		return oldExchange;
	}
}
