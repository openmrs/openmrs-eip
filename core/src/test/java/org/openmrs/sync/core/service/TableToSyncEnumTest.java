package org.openmrs.sync.core.service;

import org.junit.Test;
import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.openmrs.sync.core.model.PersonModel;

import static org.junit.Assert.assertEquals;

public class TableToSyncEnumTest {

    @Test
    public void getTableToSyncEnum_should_return_enum() {
        // Given
        String nameString = "person";

        // When
        TableToSyncEnum result = TableToSyncEnum.getTableToSyncEnum(nameString);

        // Then
        assertEquals(TableToSyncEnum.PERSON, result);
    }

    @Test
    public void getModelClass_should_return_model_class() {
        // Given
        Person person = new Person();

        // When
        Class result = TableToSyncEnum.getModelClass(person);

        // Then
        assertEquals(PersonModel.class, result);
    }

    @Test(expected = OpenMrsSyncException.class)
    public void getModelClass_should_throw_exception() {
        // Given
        MockedEntity mockedEntity = new MockedEntity(1L, "uuid");

        // When
        TableToSyncEnum.getModelClass(mockedEntity);

        // Then
    }

}
