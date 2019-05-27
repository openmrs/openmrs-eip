package org.openmrs.sync.remote.management.entity;

import lombok.Data;
import org.openmrs.sync.core.service.TableNameEnum;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class TableSyncStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TableNameEnum tableName;

    private LocalDateTime lastSyncDate;
}
