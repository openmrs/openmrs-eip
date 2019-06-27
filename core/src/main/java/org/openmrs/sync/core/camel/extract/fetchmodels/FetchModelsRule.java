package org.openmrs.sync.core.camel.extract.fetchmodels;

import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableToSyncEnum;

import java.util.List;

public interface FetchModelsRule {

    boolean evaluate(ComponentParams params);

    List<BaseModel> getModels(TableToSyncEnum tableToSync, ComponentParams params);
}
