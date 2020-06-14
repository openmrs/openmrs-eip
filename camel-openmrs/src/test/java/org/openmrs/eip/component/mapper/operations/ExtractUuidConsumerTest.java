package org.openmrs.eip.component.mapper.operations;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.eip.component.entity.MockedEntity;
import org.openmrs.eip.component.entity.MockedLightEntity;
import org.openmrs.eip.component.MockedModel;

import static org.junit.Assert.*;

public class ExtractUuidConsumerTest {

    private ExtractUuidConsumer<MockedEntity, MockedModel> consumer = new ExtractUuidConsumer<>();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void accept_should_copy_linked_entity_uuid_in_model() {
        // Given
        MockedEntity entity = new MockedEntity(1L, "uuid");
        MockedLightEntity linkedEntity = new MockedLightEntity(2L, "uuid2");
        MockedModel model = new MockedModel("uuid");
        Context<MockedEntity, MockedModel> context = getContext(entity, model, linkedEntity);

        // When
        consumer.accept(context, "linkedEntity");

        // Then
        assertNotNull(context);
        assertEquals(MockedLightEntity.class.getName() + "(uuid2)", context.getModel().getLinkedEntityUuid());
    }

    @Test
    public void accept_should_leave_uuid_null() {
        // Given
        MockedEntity entity = new MockedEntity(1L, "uuid");
        MockedModel model = new MockedModel("uuid");
        Context<MockedEntity, MockedModel> context = getContext(entity, model, null);

        // When
        consumer.accept(context, "linkedEntity");

        // Then
        assertNotNull(context);
        assertNull(context.getModel().getLinkedEntityUuid());
    }

    private Context<MockedEntity, MockedModel> getContext(final MockedEntity entity, final MockedModel model, final MockedLightEntity linkedEntity) {
        entity.setLinkedEntity(linkedEntity);

        return new Context<>(entity, model, MappingDirectionEnum.ENTITY_TO_MODEL);
    }
}
