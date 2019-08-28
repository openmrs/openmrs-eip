package org.openmrs.sync.map.mapper;

import org.junit.Test;
import org.openmrs.sync.common.model.sync.PersonModel;

import java.util.Optional;

import static org.junit.Assert.assertEquals;

public class OdooModelTypeEnumTest {

    @Test
    public void getDerivedOdooEntityType_should_return_enum() {
        // Given
        Class<PersonModel> modelClass = PersonModel.class;

        // When
        Optional<OdooModelTypeEnum> result = OdooModelTypeEnum.getDerivedOdooEntityType(modelClass);

        // Then
        assertEquals(Optional.of(OdooModelTypeEnum.CUSTOMER), result);
    }
}
