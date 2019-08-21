package org.openmrs.sync.odoo.service;

import org.openmrs.sync.common.model.odoo.OdooModel;

public interface OdooServiceXmlRpc {

    int authenticate();

    int sendToOdoo(int uid, OdooModel odooModel);
}
