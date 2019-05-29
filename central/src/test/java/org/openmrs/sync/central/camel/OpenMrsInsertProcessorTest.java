package org.openmrs.sync.central.camel;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.Person;
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
    public void process() {
        // Given
        //Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        //String json = "{\"uuid\": \"uuid\"}";
        //exchange.getIn().setBody(json);
        //exchange.getIn().setHeader("OpenMrsTableSyncName", "PERSON");
        //exchange.getIn().setHeader("CamelJacksonUnmarshalType", PersonModel.class.getName());
//
        //// When
        //processor.process(exchange);
//
        //// Then
        //PersonModel expectedModel = new PersonModel();
        //expectedModel.setUuid("uuid");
        //verify(entityServiceFacade).saveModel(TableNameEnum.PERSON, expectedModel);
    }
}
