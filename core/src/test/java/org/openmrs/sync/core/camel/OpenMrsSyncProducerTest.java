package org.openmrs.sync.core.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.model.MockedModel;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import static org.mockito.Mockito.verify;

public class OpenMrsSyncProducerTest {

    @Mock
    private Endpoint endpoint;

    @Mock
    private EntityServiceFacade facade;

    private OpenMrsSyncProducer producer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        producer = new OpenMrsSyncProducer(endpoint, EntityNameEnum.PERSON, facade);
    }

    @Test
    public void process() throws Exception {
        // Given
        MockedModel mockedModel = new MockedModel("uuid");
        Exchange exchange = new DefaultExchange(endpoint);
        exchange.getIn().setBody(mockedModel);

        // When
        producer.process(exchange);

        // Then
        verify(facade).saveModel(EntityNameEnum.PERSON, mockedModel);
    }
}
