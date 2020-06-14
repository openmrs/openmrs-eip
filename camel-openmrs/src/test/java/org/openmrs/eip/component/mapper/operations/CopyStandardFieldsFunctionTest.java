package org.openmrs.eip.component.mapper.operations;

import org.junit.Test;
import org.openmrs.eip.component.entity.MockedEntity;
import org.openmrs.eip.component.MockedModel;

import static org.junit.Assert.assertEquals;

public class CopyStandardFieldsFunctionTest {

    private CopyStandardFieldsFunction<MockedEntity, MockedModel> function = new CopyStandardFieldsFunction<>();

    @Test
    public void apply_should_copy_model_to_entity() {
        // Given
        MockedModel model = getModel();
        MockedEntity entity = new MockedEntity(1L, null);
        Context<MockedEntity, MockedModel> context = new Context<>(entity, model, MappingDirectionEnum.MODEL_TO_ENTITY);

        // When
        Context<MockedEntity, MockedModel> result = function.apply(context);

        // Then
        assertEquals(getEntity(), result.getEntity());
    }

    @Test
    public void apply_should_copy_entity_to_model() {
        // Given
        MockedEntity entity = getEntity();
        MockedModel model = new MockedModel(null);
        Context<MockedEntity, MockedModel> context = new Context<>(entity, model, MappingDirectionEnum.ENTITY_TO_MODEL);

        // When
        Context<MockedEntity, MockedModel> result = function.apply(context);

        // Then
        assertEquals(getModel(), result.getModel());
    }

    public MockedEntity getEntity() {
        MockedEntity expectedEntity = new MockedEntity(1L, "uuid");
        expectedEntity.setField1("field1");
        expectedEntity.setField2("field2");
        return expectedEntity;
    }

    public MockedModel getModel() {
        MockedModel expectedModel = new MockedModel("uuid");
        expectedModel.setField1("field1");
        expectedModel.setField2("field2");
        return expectedModel;
    }
}
