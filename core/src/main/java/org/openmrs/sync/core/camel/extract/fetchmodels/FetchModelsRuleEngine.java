package org.openmrs.sync.core.camel.extract.fetchmodels;

import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableToSyncEnum;

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
