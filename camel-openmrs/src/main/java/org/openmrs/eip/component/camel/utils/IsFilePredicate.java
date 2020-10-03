package org.openmrs.eip.component.camel.utils;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.openmrs.eip.component.camel.TypeEnum;
import org.springframework.stereotype.Component;

/**
 * Checks that the body of the exchange is a file
 */
@Component("isFilePredicate")
public class IsFilePredicate implements Predicate {

    @Override
    public boolean matches(final Exchange exchange) {
        Object in = exchange.getIn().getBody();
        if(in instanceof String){
            String body = (String) in;

            return body.startsWith(TypeEnum.FILE.getOpeningTag()) && body.endsWith(TypeEnum.FILE.getClosingTag());
        }

        return false;
    }
}
