package org.openmrs.sync.map.mapper;

import org.openmrs.sync.common.model.odoo.OdooModel;
import org.openmrs.sync.common.model.sync.BaseModel;
import org.openmrs.sync.common.model.sync.PersonModel;

import java.util.function.Function;

public class PersonToCustomerMapper implements Function<BaseModel, OdooModel> {

    @Override
    public OdooModel apply(final BaseModel personModel) {
        PersonModel model = (PersonModel) personModel;

        OdooModel odooModel = new OdooModel();
        odooModel.setType(OdooModelTypeEnum.CUSTOMER.name());
        odooModel.addValue("name", "test");

        return odooModel;
    }
}
