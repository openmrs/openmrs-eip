package org.openmrs.sync.sender.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.sync.sender.management.entity.TableSyncStatus;
import org.openmrs.sync.sender.management.repository.TableSyncStatusRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class SaveSyncStatusProcessor implements Processor {

    private TableSyncStatusRepository repository;

    public SaveSyncStatusProcessor(final TableSyncStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    public void process(final Exchange exchange) {
        Optional<TableSyncStatus> statusOptional = repository.findById((Long) exchange.getIn().getHeader("OpenMrsEntitySyncStatusId"));
        statusOptional.ifPresent(status -> {
            status.setLastSyncDate(LocalDateTime.now());
            repository.save(status);
        });
    }
}
