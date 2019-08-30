package org.openmrs.sync.component.camel.extract.fetchmodels;

import org.openmrs.sync.component.model.BaseModel;
import org.openmrs.sync.component.service.TableToSyncEnum;

import java.util.List;

public interface FetchModelsRuleEngine {

    /**
     * get models corresponding to the given arguments
     * @param tableToSync name of the table to sync
     * @param params the parameters to get the models
     * @return list of models
     */
    List<BaseModel> process(final TableToSyncEnum tableToSync, final ComponentParams params);
}
