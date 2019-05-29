package org.openmrs.sync.remote.camel;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.service.TableNameEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.openmrs.sync.remote.management.entity.TableSyncStatus;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

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
    public void process() {
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
    }

    @Test
    public void process_body_wrong_type() {
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
