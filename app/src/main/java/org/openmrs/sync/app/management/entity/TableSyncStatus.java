package org.openmrs.sync.app.management.entity;

import lombok.Data;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.component.utils.DateUtils;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class TableSyncStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "table_name")
    private TableToSyncEnum tableToSync;

    @Column(name = "last_sync_date")
    private LocalDateTime lastSyncDate;

    public String getLastSyncDateAsString() {
        if (lastSyncDate == null) {
            return null;
        }
        return DateUtils.dateToString(lastSyncDate);
    }
}
