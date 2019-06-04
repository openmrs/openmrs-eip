package org.openmrs.sync.remote.management.entity;

import lombok.Data;
import org.openmrs.sync.core.service.EntityNameEnum;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class EntitySyncStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_name")
    private EntityNameEnum entityName;

    @Column(name = "last_sync_date")
    private LocalDateTime lastSyncDate;
}
