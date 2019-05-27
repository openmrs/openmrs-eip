package org.openmrs.sync.central.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.sync.core.service.TableNameEnum;
import org.openmrs.sync.core.model.OpenMrsModel;
import org.openmrs.sync.core.service.facade.EntityServiceFacade;
import org.springframework.stereotype.Component;

@Component
public class OpenMrsInsertProcessor implements Processor {

    private EntityServiceFacade entityServiceFacade;

    public OpenMrsInsertProcessor(final EntityServiceFacade entityServiceFacade) {
        this.entityServiceFacade = entityServiceFacade;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        TableNameEnum tableName = TableNameEnum.getTableNameEnum((String) exchange.getIn().getHeader("OpenMrsTableSyncName"));
        entityServiceFacade.saveModel(tableName, (OpenMrsModel) exchange.getIn().getBody());
    }
}
