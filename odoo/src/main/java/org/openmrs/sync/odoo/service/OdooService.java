package org.openmrs.sync.odoo.service;

import org.openmrs.sync.common.model.odoo.OdooModel;

public interface OdooService {

    void sendModel(OdooModel model);
}
