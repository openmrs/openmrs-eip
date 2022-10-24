package org.openmrs.eip.component.camel.fetchmodels;

import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.camel.ProducerParams;

import java.util.List;

public interface FetchModelsRuleEngine {
	
	/**
	 * get models corresponding to the given arguments
	 * 
	 * @param params the parameters to get the models with
	 * @return list of models
	 */
	List<BaseModel> process(final ProducerParams params);
}
