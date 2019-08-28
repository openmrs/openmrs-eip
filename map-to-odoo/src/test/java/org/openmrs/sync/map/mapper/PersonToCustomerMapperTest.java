package org.openmrs.sync.map.mapper;

import org.junit.Test;
import org.openmrs.sync.common.model.odoo.OdooModel;
import org.openmrs.sync.common.model.sync.PersonModel;

import static org.junit.Assert.assertEquals;

public class PersonToCustomerMapperTest {

    PersonToCustomerMapper mapper = new PersonToCustomerMapper();

    @Test
    public void apply_should_return_odoo_model() {
        // Given
        PersonModel model = new PersonModel();

        // When
        OdooModel result = mapper.apply(model);

        // Then
        assertEquals(getExpectedResult(), result);
    }

    private OdooModel getExpectedResult() {
        OdooModel odooModel = new OdooModel();
        odooModel.setType(OdooModelTypeEnum.CUSTOMER.name());
        odooModel.addValue("name", "test");

        return odooModel;
    }
}
