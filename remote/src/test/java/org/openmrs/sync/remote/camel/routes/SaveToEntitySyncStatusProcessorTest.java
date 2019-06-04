package org.openmrs.sync.remote.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.DefaultExchange;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.remote.camel.SaveEntitySyncStatusProcessor;
import org.openmrs.sync.remote.management.entity.EntitySyncStatus;
import org.openmrs.sync.remote.management.repository.EntitySyncStatusRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SaveToEntitySyncStatusProcessorTest {

    @Mock
    private EntitySyncStatusRepository repository;

    @Captor
    private ArgumentCaptor<EntitySyncStatus> captor;

    private SaveEntitySyncStatusProcessor processor;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        processor = new SaveEntitySyncStatusProcessor(repository);
    }

    @Test
    public void process() {
        // Given
        Exchange exchange = new DefaultExchange(new DefaultCamelContext());
        exchange.getIn().setHeader("OpenMrsEntitySyncStatusId", 1L);
        EntitySyncStatus status = new EntitySyncStatus();
        status.setId(1L);
        status.setEntityName(EntityNameEnum.PERSON);
        when(repository.findById(1L)).thenReturn(Optional.of(status));

        // When
        processor.process(exchange);

        // Then
        verify(repository).save(captor.capture());
        assertEquals(1L, captor.getValue().getId().longValue());
        assertEquals(EntityNameEnum.PERSON, captor.getValue().getEntityName());
        assertEquals(LocalDate.now(), captor.getValue().getLastSyncDate().toLocalDate());
    }
}
