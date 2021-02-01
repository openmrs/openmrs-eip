package org.openmrs.eip.component.utils;

import static org.junit.Assert.assertEquals;

import org.apache.camel.Exchange;
import org.apache.camel.builder.ExchangeBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;

public class SplitBodyConcatAggregationStrategyTest {
	
	private SplitBodyConcatAggregationStrategy strategy;
	
	private Exchange oldExchange, newExchange;
	
	@Before
    public void setup() {
		strategy = new SplitBodyConcatAggregationStrategy();
    }

    @Test
    public void aggregate_shouldReturnExchangeWithConcatenateBodyContentsAsCommaSeparated() throws JSONException {
        // Given
    	oldExchange = ExchangeBuilder.anExchange(new DefaultCamelContext()).withBody("Hello").build();
		newExchange = ExchangeBuilder.anExchange(new DefaultCamelContext()).withBody("World").build();
    	
        // When
		Exchange exchange = strategy.aggregate(oldExchange, newExchange);
		
        // Then
		assertEquals("Hello,World", exchange.getIn().getBody());
    }
    
    public void aggregate_shouldReturnExchangeWithNewExchangeBodyContentGivenOldExchangeIsNull() throws JSONException {
        // Given
    	oldExchange = null;
		newExchange = ExchangeBuilder.anExchange(new DefaultCamelContext()).withBody("World").build();
    	
        // When
		Exchange exchange = strategy.aggregate(oldExchange, newExchange);
		
        // Then
		assertEquals("World", exchange.getIn().getBody());
    }

}
