package org.openmrs.eip.component.camel.utils;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.openmrs.eip.component.camel.TypeEnum;
import org.springframework.stereotype.Component;

/**
 * Removes the <FILE> prefix and the </FILE> suffix from a file converted to base64 string
 */
@Component("removeFileTagsExpression")
public class RemoveFileTagsExpression implements Expression {

    @Override
    public <T> T evaluate(final Exchange exchange, final Class<T> type) {
        String body = (String) exchange.getIn().getBody();
        String newBody = body.substring(TypeEnum.FILE.getOpeningTag().length());
        newBody = newBody.substring(0, newBody.length() - TypeEnum.FILE.getClosingTag().length());
        return (T) newBody;
    }
}
