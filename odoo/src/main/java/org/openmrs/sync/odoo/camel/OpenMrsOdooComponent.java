package org.openmrs.sync.odoo.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.Endpoint;
import org.apache.camel.impl.DefaultComponent;
import org.openmrs.sync.odoo.service.OdooService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OpenMrsOdooComponent extends DefaultComponent {

    private OdooService odooService;

    public OpenMrsOdooComponent(final CamelContext context,
                                final OdooService odooService) {
        super(context);
        this.odooService = odooService;
    }

    @Override
    protected Endpoint createEndpoint(final String uri,
                                      final String remaining,
                                      final Map<String, Object> parameters) {
        return new OpenMrsOdooEndpoint(uri, this, odooService);
    }
}
