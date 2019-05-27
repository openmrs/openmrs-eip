package org.openmrs.sync.remote.camel;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.TableNameEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.openmrs.sync.remote.management.entity.TableSyncStatus;

import java.time.LocalDateTime;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class OpenMrsExtractProcessorTest {

    @Mock
    private EntityServiceFacade entityServiceFacade;

    private OpenMrsExtractProcessor processor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        processor = new OpenMrsExtractProcessor(entityServiceFacade);
    }

    @Test
    public void process() throws Exception {
        // Given
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        TableSyncStatus status = new TableSyncStatus();
        status.setTableName(TableNameEnum.PERSON);
        exchange.getIn().setBody(status);

        // When
        processor.process(exchange);

        // Then
        verify(entityServiceFacade).getModels(TableNameEnum.PERSON, status.getLastSyncDate());
        assertEquals(TableNameEnum.PERSON.name(), exchange.getIn().getHeader("OpenMrsTableSyncName"));
        assertEquals(TableNameEnum.PERSON.getModelClass().getName(), exchange.getIn().getHeader("CamelJacksonUnmarshalType"));
    }

    @Test
    public void process_body_wrong_type() throws Exception {
        // Given
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setBody("body");

        // When
        processor.process(exchange);

        // Then
        verify(entityServiceFacade, never()).getModels(any(TableNameEnum.class), any(LocalDateTime.class));
        assertNull(exchange.getIn().getHeader("OpenMrsTableSyncName"));
        assertNull(exchange.getIn().getHeader("CamelJacksonUnmarshalType"));
    }
}
