package org.openmrs.sync.app.camel;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.sync.app.management.entity.TableSyncStatus;
import org.openmrs.sync.app.management.repository.TableSyncStatusRepository;
import org.openmrs.sync.component.SyncProfiles;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile(SyncProfiles.SENDER)
@Component("saveSyncStatusProcessor")
public class SaveSyncStatusProcessor implements Processor {

    private TableSyncStatusRepository repository;

    public SaveSyncStatusProcessor(final TableSyncStatusRepository repository) {
        this.repository = repository;
    }

    @Override
    public void process(final Exchange exchange) {
        Optional<TableSyncStatus> statusOptional = repository.findById((Long) exchange.getIn().getHeader("OpenmrsEntitySyncStatusId"));
        statusOptional.ifPresent(status -> {
            status.setLastSyncDate(LocalDateTime.now());
            repository.save(status);
        });
    }
}
