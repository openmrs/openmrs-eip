package org.openmrs.sync.core.camel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableToSyncEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferObject {

    private TableToSyncEnum tableToSync;

    private BaseModel model;
}
