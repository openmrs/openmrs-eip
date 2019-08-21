package org.openmrs.sync.odoo.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.openmrs.sync.common.model.odoo.OdooModel;
import org.openmrs.sync.odoo.config.OdooProperties;
import org.openmrs.sync.odoo.exeption.OdooException;
import org.openmrs.sync.odoo.service.OdooModelEnum;
import org.openmrs.sync.odoo.service.OdooServiceXmlRpc;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;

@Slf4j
@Service
public class OdooServiceXmlRpcImpl implements OdooServiceXmlRpc {

    private OdooProperties odooProperties;

    private static final String COMMON_URL = "%s/xmlrpc/2/common";
    private static final String AUTHENTICATE_METHOD = "authenticate";
    private static final String OBJECT_URL = "%s/xmlrpc/2/object";
    private static final String EXECUTE_METHOD = "execute_kw";

    private XmlRpcClient client = new XmlRpcClient();

    public OdooServiceXmlRpcImpl(final OdooProperties odooProperties) {
        this.odooProperties = odooProperties;
    }

    public int authenticate() {
        try {
            final XmlRpcClientConfigImpl commonConfig = new XmlRpcClientConfigImpl();
            commonConfig.setServerURL(new URL(String.format(COMMON_URL, odooProperties.getUrl())));

            int uid = (int) client.execute(commonConfig, AUTHENTICATE_METHOD,
                    Arrays.asList(odooProperties.getDbName(), odooProperties.getUsername(), odooProperties.getPassword(), Collections.emptyMap()));

            log.info("Authentication successful for login " + odooProperties.getUsername());

            return uid;
        } catch (MalformedURLException | XmlRpcException e) {
            throw new OdooException("Error while authenticating to Odoo server", e);
        }
    }

    public int sendToOdoo(final int uid,
                          final OdooModel odooModel) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL(String.format(OBJECT_URL, odooProperties.getUrl())));

            client.setConfig(config);

            OdooModelEnum odooModelEnum = OdooModelEnum.getOdooModelEnum(odooModel.getType());

            return (Integer) client.execute(EXECUTE_METHOD, Arrays.asList(
                    odooProperties.getDbName(), uid, odooProperties.getPassword(),
                    odooModelEnum.getOdooModelName(), "create",
                    Arrays.asList(odooModel.getData())
            ));
        } catch (MalformedURLException | XmlRpcException e) {
            throw new OdooException("Error while sending value to Odoo server", e);
        }
    }
}
