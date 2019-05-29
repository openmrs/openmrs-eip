package org.openmrs.sync.remote.camel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.openmrs.sync.remote.management.entity.TableSyncStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class OpenMrsExtractProcessor implements Processor {

    private EntityServiceFacade entityServiceFacade;

    public OpenMrsExtractProcessor(final EntityServiceFacade entityServiceFacade) {
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public void process(final Exchange exchange) {
        if (exchange.getIn().getBody() instanceof TableSyncStatus) {
            TableSyncStatus status = (TableSyncStatus) exchange.getIn().getBody();

            List<? extends BaseModel> models = entityServiceFacade.getModels(status.getTableName(), status.getLastSyncDate());

            exchange.getIn().setBody(models);
            exchange.getIn().setHeader("OpenMrsTableSyncName", status.getTableName().name());
        }
    }

    private String marshall(final BaseModel model) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            return mapper.writeValueAsString(model).replaceAll("^\"|\"$|\\\\", "");
        } catch (JsonProcessingException e) {
            log.error("Error while marshalling model", e);
            throw new RuntimeException(e);
        }
    }
}
