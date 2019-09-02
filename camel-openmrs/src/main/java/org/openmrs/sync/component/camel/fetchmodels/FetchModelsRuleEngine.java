package org.openmrs.sync.component.camel.fetchmodels;

import org.openmrs.sync.component.camel.ProducerParams;
import org.openmrs.sync.component.model.BaseModel;

import java.util.List;

public interface FetchModelsRuleEngine {

    /**
     * get models corresponding to the given arguments
     * @param params the parameters to get the models with
     * @return list of models
     */
    List<BaseModel> process(final ProducerParams params);
}
