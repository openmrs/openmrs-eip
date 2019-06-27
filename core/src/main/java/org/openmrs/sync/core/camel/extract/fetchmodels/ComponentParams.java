package org.openmrs.sync.core.camel.extract.fetchmodels;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode
public class ComponentParams {

    private LocalDateTime lastSyncDate;

    private String uuid;

    private Long id;
}
