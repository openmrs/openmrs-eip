package org.openmrs.sync.component.camel.fetchmodels;

import org.openmrs.sync.component.camel.ProducerParams;
import org.openmrs.sync.component.model.BaseModel;

import java.util.List;

public interface FetchModelsRule {

    boolean evaluate(ProducerParams params);

    List<BaseModel> getModels(ProducerParams params);
}
