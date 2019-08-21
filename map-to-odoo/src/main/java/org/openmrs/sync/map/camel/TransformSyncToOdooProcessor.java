package org.openmrs.sync.map.camel;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.openmrs.sync.common.marshalling.JsonUtils;
import org.openmrs.sync.common.model.odoo.OdooModel;
import org.openmrs.sync.common.model.sync.SyncModel;
import org.openmrs.sync.map.mapper.OdooModelTypeEnum;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("transformSyncToOdooProcessor")
public class TransformSyncToOdooProcessor implements Processor {

    @Override
    public void process(final Exchange exchange) {
        String syncJson = (String) exchange.getIn().getBody();

        SyncModel syncModel = JsonUtils.unmarshal(syncJson, SyncModel.class);

        Optional<OdooModelTypeEnum> modelType = OdooModelTypeEnum.getDerivedOdooEntity(syncModel.getTableToSyncModelClass());

        String body = null;
        if (modelType.isPresent()) {
            OdooModel odooModel = modelType.get().getMapper().apply(syncModel.getModel());

            body = JsonUtils.marshall(odooModel);
        }

        exchange.getIn().setBody(body);
    }
}
