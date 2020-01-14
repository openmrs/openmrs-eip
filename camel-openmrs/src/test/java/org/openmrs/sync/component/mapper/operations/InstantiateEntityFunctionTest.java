package org.openmrs.sync.component.mapper.operations;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.component.entity.BaseEntity;
import org.openmrs.sync.component.entity.MockedEntity;
import org.openmrs.sync.component.exception.OpenmrsSyncException;
import org.openmrs.sync.component.MockedModel;
import org.openmrs.sync.component.service.MapperService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class InstantiateEntityFunctionTest {

    @Mock
    private MapperService<MockedEntity, MockedModel> mapperService;

    private InstantiateEntityFunction<MockedEntity, MockedModel> instantiateEntity;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        instantiateEntity = new InstantiateEntityFunction<>(mapperService);
    }

    @Test
    public void apply_should_instantiate_model() {
        // Given
        MockedModel model = new MockedModel("uuid");
        Mockito.when(mapperService.getCorrespondingEntityClass(model)).thenReturn(MockedEntity.class);

        // When
        Context<MockedEntity, MockedModel> result = instantiateEntity.apply(model);

        // Then
        assertNotNull(result);
        assertNotNull(result.getEntity());
        assertEquals(model, result.getModel());
    }

    @Test(expected = OpenmrsSyncException.class)
    public void apply_should_throw_exception() {
        // Given
        MockedModel model = new MockedModel("uuid");
        Mockito.<Class<? extends BaseEntity>>when(mapperService.getCorrespondingEntityClass(model)).thenReturn(MockedEntityWithNoConstructor.class);

        // When
        instantiateEntity.apply(model);

        // Then
    }

    private class MockedEntityWithNoConstructor extends MockedEntity {}
}
