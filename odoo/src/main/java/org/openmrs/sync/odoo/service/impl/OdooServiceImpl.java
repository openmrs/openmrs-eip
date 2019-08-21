package org.openmrs.sync.odoo.service.impl;

import org.openmrs.sync.common.model.odoo.OdooModel;
import org.openmrs.sync.odoo.service.OdooService;
import org.openmrs.sync.odoo.service.OdooServiceXmlRpc;
import org.springframework.stereotype.Service;

@Service
public class OdooServiceImpl implements OdooService {

    private Integer uid;

    private OdooServiceXmlRpc odooServiceXmlRpc;

    public OdooServiceImpl(final OdooServiceXmlRpc odooServiceXmlRpc) {
        this.odooServiceXmlRpc = odooServiceXmlRpc;
    }

    private Integer getUid() {
        if (uid == null) {
            uid = odooServiceXmlRpc.authenticate();
        }
        return uid;
    }

    public void sendModel(final OdooModel model) {
        odooServiceXmlRpc.sendToOdoo(getUid(), model);
    }
}
