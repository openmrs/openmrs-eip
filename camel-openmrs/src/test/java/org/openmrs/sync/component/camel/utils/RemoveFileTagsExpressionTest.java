package org.openmrs.sync.component.camel.utils;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.sync.component.camel.utils.RemoveFileTagsExpression;

import static org.junit.Assert.assertEquals;

public class RemoveFileTagsExpressionTest {

    private RemoveFileTagsExpression removeFileTagsExpression;

    @Before
    public void init() {
        removeFileTagsExpression = new RemoveFileTagsExpression();
    }

    @Test
    public void evaluate_should_remove_tags() {
        // Given
        String fileAsBase64 = "fileAsBase64";
        String fileBody = "<FILE>" + fileAsBase64 + "</FILE>";
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody(fileBody);

        // When
        String result = removeFileTagsExpression.evaluate(exchange, String.class);

        // Then
        assertEquals(fileAsBase64, result);
    }
}
