package org.openmrs.sync.core.camel;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RemoveFileTagsExpressionTest {

    private RemoveFileTagsExpression removeFileTagsExpression = new RemoveFileTagsExpression();

    @Test
    public void evaluate_should_remove_tags() {
        // Given
        String message = "test";
        String taggedMessage = TypeEnum.FILE.getOpeningTag() + message + TypeEnum.FILE.getClosingTag();
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(taggedMessage);

        // When
        Object result = removeFileTagsExpression.evaluate(exchange, Object.class);

        // Then
        assertTrue(result instanceof String);
        assertEquals(message, result);
    }
}
