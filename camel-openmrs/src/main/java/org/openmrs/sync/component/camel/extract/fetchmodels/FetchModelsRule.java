package org.openmrs.sync.component.camel.extract.fetchmodels;

import org.openmrs.sync.component.model.BaseModel;
import org.openmrs.sync.component.service.TableToSyncEnum;

import java.util.List;

public interface FetchModelsRule {

    boolean evaluate(ComponentParams params);

    List<BaseModel> getModels(TableToSyncEnum tableToSync, ComponentParams params);
}
