package org.openmrs.sync.component.camel.fetchmodels;

import org.openmrs.sync.component.camel.ProducerParams;
import org.openmrs.sync.component.model.BaseModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("fetchModelsRuleEngine")
public class FetchModelsRuleEngineImpl implements FetchModelsRuleEngine {

    private List<FetchModelsRule> fetchModelsRules;

    private DefaultFetchModelsRule defaultRule;

    public FetchModelsRuleEngineImpl(final List<FetchModelsRule> fetchModelsRules,
                                     final DefaultFetchModelsRule defaultRule) {
        this.fetchModelsRules = fetchModelsRules;
        this.defaultRule = defaultRule;
    }

    @Override
    public List<BaseModel> process(final ProducerParams params) {
        FetchModelsRule fetchModelsRule = fetchModelsRules
                .stream()
                .filter(r -> r.evaluate(params))
                .findFirst()
                .orElse(defaultRule);
        return fetchModelsRule.getModels(params);
    }
}
