package org.openmrs.sync.map.mapper;

import org.openmrs.sync.common.model.odoo.OdooModel;
import org.openmrs.sync.common.model.sync.BaseModel;
import org.openmrs.sync.common.model.sync.PatientModel;

import java.util.function.Function;

public class PatientToCustomerMapper implements Function<BaseModel, OdooModel> {

    @Override
    public OdooModel apply(final BaseModel patientModel) {
        PatientModel model = (PatientModel) patientModel;

        OdooModel odooModel = new OdooModel();
        odooModel.setType(OdooModelTypeEnum.CUSTOMER.name());
        odooModel.addValue("name", "test");

        return odooModel;
    }
}
