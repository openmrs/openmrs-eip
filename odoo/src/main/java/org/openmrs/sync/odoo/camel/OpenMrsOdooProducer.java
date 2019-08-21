package org.openmrs.sync.odoo.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.impl.DefaultProducer;
import org.openmrs.sync.common.marshalling.JsonUtils;
import org.openmrs.sync.common.model.odoo.OdooModel;
import org.openmrs.sync.odoo.service.OdooService;

public class OpenMrsOdooProducer extends DefaultProducer {

    private OdooService odooService;

    public OpenMrsOdooProducer(final Endpoint endpoint,
                               final OdooService odooService) {
        super(endpoint);
        this.odooService = odooService;
    }

    @Override
    public void process(final Exchange exchange) {
        String json = (String) exchange.getIn().getBody();

        OdooModel to = JsonUtils.unmarshal(json, OdooModel.class);

        odooService.sendModel(to);
    }
}
