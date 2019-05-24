package org.openmrs.sync.core.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.model.MockedModel;
import org.openmrs.sync.core.model.OpenMrsModel;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class OpenMrsSyncConsumerTest {

    @Mock
    private Endpoint endpoint;

    @Mock
    private EntityServiceFacade facade;

    @Mock
    private Processor processor;

    private OpenMrsSyncConsumer consumer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        consumer = new OpenMrsSyncConsumer(endpoint, processor, TableNameEnum.PERSON, facade);
    }

    @Test
    public void poll() throws Exception {
        // Given
        OpenMrsModel model1 = new MockedModel("uuid1");
        OpenMrsModel model2 = new MockedModel("uuid2");
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        when(facade.getModels(TableNameEnum.PERSON)).thenReturn(Arrays.asList(model1, model2));
        when(endpoint.createExchange()).thenReturn(exchange);

        // When
        int result = consumer.poll();

        // Then
        assertEquals(2, result);
        verify(processor, times(2)).process(exchange);
    }
}
