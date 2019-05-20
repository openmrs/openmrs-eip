package org.cicr.camel.remote.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.cicr.camel.remote.repository.PersonRepository;
import org.springframework.stereotype.Component;

@Component
public class SelectPersonProcessor implements Processor {

    private PersonRepository personRepository;

    public SelectPersonProcessor(final PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void process(final Exchange exchange) throws Exception {
        exchange.getIn().setBody(this.personRepository.findAll());
    }
}
