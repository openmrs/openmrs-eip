package org.openmrs.sync.core.camel.extract;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.openmrs.sync.core.camel.TransferObject;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.openmrs.sync.core.utils.JsonUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OpenMrsExtractProducer extends DefaultProducer {

    private EntityServiceFacade entityServiceFacade;
    private LocalDateTime lastSyncDate;
    private TableToSyncEnum tableToSync;

    public OpenMrsExtractProducer(final Endpoint endpoint,
                                  final EntityServiceFacade entityServiceFacade,
                                  final TableToSyncEnum tableToSync,
                                  final LocalDateTime lastSyncDate) {
        super(endpoint);
        this.entityServiceFacade = entityServiceFacade;
        this.tableToSync = tableToSync;
        this.lastSyncDate = lastSyncDate;
    }

    @Override
    public void process(final Exchange exchange) {
        List<? extends BaseModel> models = entityServiceFacade.getModels(tableToSync, lastSyncDate);

        List<String> json = models.stream()
                .filter(Objects::nonNull)
                .map(this::buildTransformObject)
                .map(JsonUtils::marshall)
                .collect(Collectors.toList());

        exchange.getIn().setBody(json);
    }

    private TransferObject buildTransformObject(final BaseModel model) {
        return TransferObject.builder()
                .tableToSync(tableToSync)
                .model(model)
                .build();
    }
}
