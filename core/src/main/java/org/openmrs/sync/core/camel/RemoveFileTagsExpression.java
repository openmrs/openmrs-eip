package org.openmrs.sync.core.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Expression;
import org.springframework.stereotype.Component;

@Component
public class RemoveFileTagsExpression implements Expression {

    @Override
    public <T> T evaluate(final Exchange exchange, final Class<T> type) {
        String body = (String) exchange.getIn().getBody();
        String newBody = body.substring(TypeEnum.FILE.getOpeningTag().length());
        newBody = newBody.substring(0, newBody.length() - TypeEnum.FILE.getClosingTag().length());
        return (T) newBody;
    }
}
