package org.openmrs.sync.core.camel.extract;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.openmrs.sync.core.camel.TransferObject;
import org.openmrs.sync.core.camel.extract.fetchmodels.ComponentParams;
import org.openmrs.sync.core.camel.extract.fetchmodels.FetchModelsRuleEngine;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.core.utils.JsonUtils;

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

    private TransferObject buildTransferObject(final BaseModel model) {
        return TransferObject.builder()
                .tableToSync(tableToSync)
                .model(model)
                .build();
    }
}
