package org.openmrs.sync.central.camel;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.sync.core.service.TableNameEnum;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class OpenMrsInsertProcessor implements Processor {

    private EntityServiceFacade entityServiceFacade;

    public OpenMrsInsertProcessor(final EntityServiceFacade entityServiceFacade) {
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public void process(final Exchange exchange) {
        TableNameEnum tableName = TableNameEnum.getTableNameEnum((String) exchange.getIn().getHeader("OpenMrsTableSyncName"));

        BaseModel model = (BaseModel) exchange.getIn().getBody();

        entityServiceFacade.saveModel(tableName, model);
    }

    private BaseModel unmarshal(final String json,
                                final String className) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());

            return (BaseModel) mapper.readValue(json, Class.forName(className));
        } catch (IOException | ClassNotFoundException e) {
            log.error("Error while unmarshalling json", e);
            throw new RuntimeException(e);
        }
    }
}
