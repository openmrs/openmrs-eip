package org.openmrs.eip.component.camel.fetchmodels;

import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.camel.ProducerParams;

import java.util.List;

public interface FetchModelsRule {
	
	boolean evaluate(ProducerParams params);
	
	List<BaseModel> getModels(ProducerParams params);
}
