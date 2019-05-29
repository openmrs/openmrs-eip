package org.openmrs.sync.remote.camel.routes;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.sync.remote.management.entity.TableSyncStatus;
import org.openmrs.sync.remote.management.repository.TableSyncStatusRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class SaveTableSyncStatusProcessor implements Processor {

    private TableSyncStatusRepository repository;

    public SaveTableSyncStatusProcessor(final TableSyncStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    public void process(final Exchange exchange) {
        Optional<TableSyncStatus> statusOptional = repository.findById((Long) exchange.getIn().getHeader("OpenMrsTableSyncStatusId"));
        statusOptional.ifPresent(status -> {
            status.setLastSyncDate(LocalDateTime.now());
            repository.save(status);
        });
    }
}
