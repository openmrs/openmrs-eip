package org.openmrs.sync.remote.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.sync.remote.management.entity.EntitySyncStatus;
import org.openmrs.sync.remote.management.repository.EntitySyncStatusRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class SaveEntitySyncStatusProcessor implements Processor {

    private EntitySyncStatusRepository repository;

    public SaveEntitySyncStatusProcessor(final EntitySyncStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    public void process(final Exchange exchange) {
        Optional<EntitySyncStatus> statusOptional = repository.findById((Long) exchange.getIn().getHeader("OpenMrsEntitySyncStatusId"));
        statusOptional.ifPresent(status -> {
            status.setLastSyncDate(LocalDateTime.now());
            repository.save(status);
        });
    }
}
