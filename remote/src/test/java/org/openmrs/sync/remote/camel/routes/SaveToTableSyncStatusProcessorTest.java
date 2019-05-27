package org.openmrs.sync.remote.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.TableNameEnum;
import org.openmrs.sync.remote.management.entity.TableSyncStatus;
import org.openmrs.sync.remote.management.repository.TableSyncStatusRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SaveToTableSyncStatusProcessorTest {

    @Mock
    private TableSyncStatusRepository repository;

    @Captor
    private ArgumentCaptor<TableSyncStatus> captor;

    private SaveTableSyncStatusProcessor processor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        processor = new SaveTableSyncStatusProcessor(repository);
    }

    @Test
    public void process() throws Exception {
        // Given
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("OpenMrsTableSyncStatusId", 1L);
        TableSyncStatus status = new TableSyncStatus();
        status.setId(1L);
        status.setTableName(TableNameEnum.PERSON);
        when(repository.findById(1L)).thenReturn(Optional.of(status));

        // When
        processor.process(exchange);

        // Then
        verify(repository).save(captor.capture());
        assertEquals(1L, captor.getValue().getId().longValue());
        assertEquals(TableNameEnum.PERSON, captor.getValue().getTableName());
        assertEquals(LocalDate.now(), captor.getValue().getLastSyncDate().toLocalDate());
    }
}
