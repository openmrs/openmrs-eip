package org.openmrs.sync.component.camel.utils;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.sync.component.camel.utils.IsFilePredicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IsFilePredicateTest {

    private IsFilePredicate isFilePredicate;

    @Before
    public void init() {
        isFilePredicate = new IsFilePredicate();
    }

    @Test
    public void match_should_return_true() {
        // Given
        String fileBody = "<FILE>fileAsBase64</FILE>";
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(fileBody);

        // When
        boolean result = isFilePredicate.matches(exchange);

        // Then
        assertTrue(result);
    }

    @Test
    public void match_should_return_false() {
        // Given
        String fileBase64 = "fileAsBase64";
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(fileBase64);

        // When
        boolean result = isFilePredicate.matches(exchange);

        // Then
        assertFalse(result);
    }
}
