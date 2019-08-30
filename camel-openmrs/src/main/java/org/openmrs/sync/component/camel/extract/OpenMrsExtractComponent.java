package org.openmrs.sync.component.camel.extract;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.openmrs.sync.component.camel.extract.fetchmodels.FetchModelsRuleEngine;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OpenMrsExtractComponent extends DefaultComponent {

    private FetchModelsRuleEngine ruleEngine;

    public OpenMrsExtractComponent(final CamelContext context,
                                   final FetchModelsRuleEngine ruleEngine) {
        super(context);
        this.ruleEngine = ruleEngine;
    }

    @Override
    protected Endpoint createEndpoint(final String uri,
                                      final String remaining,
                                      final Map<String, Object> parameters) {
        TableToSyncEnum tableToSync = TableToSyncEnum.getTableToSyncEnum(remaining);
        return new OpenMrsExtractEndpoint(uri, this, ruleEngine, tableToSync);
    }
}
