package org.openmrs.eip.component.camel;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.camel.fetchmodels.FetchModelsRuleEngine;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.context.ApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;

public class OpenmrsExtractProducerTest {

    @Mock
    private OpenmrsEndpoint endpoint;

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private FetchModelsRuleEngine ruleEngine;

    private Exchange exchange = new DefaultExchange(new DefaultCamelContext());

    private ProducerParams params;

    private OpenmrsExtractProducer producer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        params = ProducerParams.builder()
                .tableToSync(TableToSyncEnum.PERSON)
                .build();
        producer = new OpenmrsExtractProducer(endpoint, applicationContext, params);
    }

    @Test
    public void process() throws JSONException {
        // Given
        PersonModel model1 = new PersonModel();
        model1.setUuid("uuid1");
        PersonModel model2 = new PersonModel();
        model2.setUuid("uuid2");
        when(applicationContext.getBean("fetchModelsRuleEngine")).thenReturn(ruleEngine);
        when(ruleEngine.process(params)).thenReturn(Arrays.asList(model1, model2));

        // When
        producer.process(exchange);

        // Then
        List<SyncModel> syncModels = exchange.getIn().getBody(List.class);
        Assert.assertEquals(2, syncModels.size());
        Assert.assertEquals("uuid1", syncModels.get(0).getModel().getUuid());
        Assert.assertEquals("uuid2", syncModels.get(1).getModel().getUuid());
    }
}
