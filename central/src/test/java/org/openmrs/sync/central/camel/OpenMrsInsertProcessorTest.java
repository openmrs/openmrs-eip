package org.openmrs.sync.central.camel;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.service.TableNameEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import static org.mockito.Mockito.verify;

public class OpenMrsInsertProcessorTest {

    @Mock
    private EntityServiceFacade entityServiceFacade;

    private OpenMrsInsertProcessor processor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        processor = new OpenMrsInsertProcessor(entityServiceFacade);
    }

    @Test
    public void process() throws Exception {
        // Given
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        PersonModel model = new PersonModel();
        exchange.getIn().setBody(model);
        exchange.getIn().setHeader("OpenMrsTableSyncName", "PERSON");

        // When
        processor.process(exchange);

        // Then
        verify(entityServiceFacade).saveModel(TableNameEnum.PERSON, model);
    }
}
