package org.openmrs.sync.core.camel.extract;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.model.CamelModel;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.openmrs.sync.core.utils.JsonUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class OpenMrsExtractProducer extends DefaultProducer {

    private EntityServiceFacade entityServiceFacade;
    private LocalDateTime lastSyncDate;
    private EntityNameEnum entityName;

    public OpenMrsExtractProducer(final Endpoint endpoint,
                                  final EntityServiceFacade entityServiceFacade,
                                  final EntityNameEnum entityName,
                                  final LocalDateTime lastSyncDate) {
        super(endpoint);
        this.entityServiceFacade = entityServiceFacade;
        this.entityName = entityName;
        this.lastSyncDate = lastSyncDate;
    }

    @Override
    public void process(final Exchange exchange) {
        List<? extends BaseModel> models = entityServiceFacade.getModels(entityName, lastSyncDate);

        List<String> json = models.stream()
                .filter(Objects::nonNull)
                .map(CamelModel::new)
                .map(JsonUtils::marshall)
                .collect(Collectors.toList());

        exchange.getIn().setBody(json);
        exchange.getIn().setHeader("OpenMrsTableSyncName", entityName);
    }
}
