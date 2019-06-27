package org.openmrs.sync.core.camel.extract.fetchmodels;

import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FetchModelsRuleEngineImpl implements FetchModelsRuleEngine {

    private List<FetchModelsRule> fetchModelsRules;

    private DefaultFetchModelsRule defaultRule;

    public FetchModelsRuleEngineImpl(final List<FetchModelsRule> fetchModelsRules,
                                     final DefaultFetchModelsRule defaultRule) {
        this.fetchModelsRules = fetchModelsRules;
        this.defaultRule = defaultRule;
    }

    @Override
    public List<BaseModel> process(final TableToSyncEnum tableToSync,
                                   final ComponentParams params) {
        FetchModelsRule fetchModelsRule = fetchModelsRules
                .stream()
                .filter(r -> r.evaluate(params))
                .findFirst()
                .orElse(defaultRule);
        return fetchModelsRule.getModels(tableToSync, params);
    }
}
