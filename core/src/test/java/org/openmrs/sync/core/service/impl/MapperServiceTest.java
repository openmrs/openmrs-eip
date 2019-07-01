package org.openmrs.sync.core.service.impl;

import org.junit.Test;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.model.PersonModel;

import static org.junit.Assert.assertEquals;

public class MapperServiceTest {

    private MapperServiceImpl mapperService = new MapperServiceImpl();

    @Test
    public void getCorrespondingModelClass_should_return_model_class() {
        // Given
        Person person = new Person();

        // When
        Class result = mapperService.getCorrespondingModelClass(person);

        // Then
        assertEquals(PersonModel.class, result);
    }
}
