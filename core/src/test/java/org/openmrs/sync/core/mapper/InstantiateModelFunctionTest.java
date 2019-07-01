package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.exception.OpenMrsSyncException;
import org.openmrs.sync.core.model.BaseModel;
import org.openmrs.sync.core.model.MockedModel;
import org.openmrs.sync.core.service.MapperService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InstantiateModelFunctionTest {

    @Mock
    private MapperService mapperService;

    private InstantiateModelFunction instantiateModel;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        instantiateModel = new InstantiateModelFunction(mapperService);
    }

    @Test
    public void apply_should_instantiate_model() {
        // Given
        MockedEntity entity = new MockedEntity(1L, "uuid");
        Mockito.<Class<? extends BaseModel>>when(mapperService.getCorrespondingModelClass(entity)).thenReturn(MockedModel.class);

        // When
        Context result = instantiateModel.apply(entity);

        // Then
        assertNotNull(result);
        assertNotNull(result.getModel());
        assertEquals(entity, result.getEntity());
    }

    @Test(expected = OpenMrsSyncException.class)
    public void apply_should_throw_exception() {
        // Given
        MockedEntity entity = new MockedEntity(1L, "uuid");
        Mockito.<Class<? extends BaseModel>>when(mapperService.getCorrespondingModelClass(entity)).thenReturn(MockedModelWithNoConstructor.class);

        // When
        instantiateModel.apply(entity);

        // Then
    }

    private class MockedModelWithNoConstructor extends MockedModel {}
}
