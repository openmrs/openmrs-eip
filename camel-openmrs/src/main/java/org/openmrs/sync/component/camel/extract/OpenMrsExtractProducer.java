package org.openmrs.sync.component.camel.extract;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.openmrs.sync.component.model.BaseModel;
import org.openmrs.sync.component.model.SyncModel;
import org.openmrs.sync.component.camel.extract.fetchmodels.ComponentParams;
import org.openmrs.sync.component.camel.extract.fetchmodels.FetchModelsRuleEngine;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.openmrs.sync.component.utils.JsonUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OpenMrsExtractProducer extends DefaultProducer {

    private FetchModelsRuleEngine ruleEngine;
    private TableToSyncEnum tableToSync;
    private ComponentParams params;

    public OpenMrsExtractProducer(final Endpoint endpoint,
                                  final FetchModelsRuleEngine ruleEngine,
                                  final TableToSyncEnum tableToSync,
                                  final ComponentParams params) {
        super(endpoint);
        this.ruleEngine = ruleEngine;
        this.tableToSync = tableToSync;
        this.params = params;
    }

    @Override
    public void process(final Exchange exchange) {
        List<BaseModel> models = ruleEngine.process(tableToSync, params);

        List<String> json = models.stream()
                .filter(Objects::nonNull)
                .map(this::buildTransferObject)
                .map(JsonUtils::marshall)
                .collect(Collectors.toList());

        exchange.getIn().setBody(json);
    }

    private SyncModel buildTransferObject(final BaseModel model) {
        return SyncModel.builder()
                .tableToSyncModelClass(tableToSync.getModelClass())
                .model(model)
                .build();
    }
}
