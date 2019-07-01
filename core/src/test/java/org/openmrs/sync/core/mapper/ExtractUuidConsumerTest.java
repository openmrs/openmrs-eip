package org.openmrs.sync.core.mapper;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.entity.MockedLightEntity;
import org.openmrs.sync.core.model.MockedModel;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.*;

public class ExtractUuidConsumerTest {

    private ExtractUuidConsumer consumer = new ExtractUuidConsumer();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void accept_should_copy_linked_entity_uuid_in_model() {
        // Given
        MockedEntity entity = new MockedEntity(1L, "uuid");
        MockedLightEntity linkedEntity = new MockedLightEntity(2L, "uuid2");
        MockedModel model = new MockedModel("uuid");
        Context context = getContext(entity, model, linkedEntity);
        PropertyDescriptor desc = BeanUtils.getPropertyDescriptor(MockedEntity.class, "linkedEntity");

        // When
        consumer.accept(context, desc);

        // Then
        assertNotNull(context);
        assertEquals("uuid2", ((MockedModel) context.getModel()).getLinkedEntityUuid());
    }

    @Test
    public void accept_should_leave_uuid_null() {
        // Given
        MockedEntity entity = new MockedEntity(1L, "uuid");
        MockedModel model = new MockedModel("uuid");
        Context context = getContext(entity, model, null);
        PropertyDescriptor desc = BeanUtils.getPropertyDescriptor(MockedEntity.class, "linkedEntity");

        // When
        consumer.accept(context, desc);

        // Then
        assertNotNull(context);
        assertNull(((MockedModel) context.getModel()).getLinkedEntityUuid());
    }

    @Test
    public void accept_should_throw_exception() {
        // Given
        WrongMockedEntity entity = new WrongMockedEntity(1L, "uuid");
        MockedLightEntity linkedEntity = new MockedLightEntity(2L, "uuid2");
        MockedModel model = new MockedModel("uuid");
        Context context = getContext(entity, model, linkedEntity);
        PropertyDescriptor desc = BeanUtils.getPropertyDescriptor(WrongMockedEntity.class, "linkedEntity2");

        // When
        try {
            consumer.accept(context, desc);

            fail();
        } catch (Exception e) {
            // Then
            assertTrue(e.getCause() instanceof InvocationTargetException);
        }
    }

    @Test
    public void accept_should_throw_exception_when_attribute_not_in_model() {
        // Given
        WrongMockedEntity entity = new WrongMockedEntity(1L, "uuid");
        MockedLightEntity linkedEntity = new MockedLightEntity(2L, "uuid2");
        MockedLightEntity linkedEntity3 = new MockedLightEntity(3L, "uuid3");
        entity.setLinkedEntity3(linkedEntity3);
        MockedModel model = new MockedModel("uuid");
        Context context = getContext(entity, model, linkedEntity);
        PropertyDescriptor desc = BeanUtils.getPropertyDescriptor(WrongMockedEntity.class, "linkedEntity3");

        // When
        try {
            consumer.accept(context, desc);

            fail();
        } catch (Exception e) {
            // Then
            assertTrue(e.getCause() instanceof NoSuchMethodException);
        }
    }

    private Context getContext(final MockedEntity entity, final MockedModel model, final MockedLightEntity linkedEntity) {
        entity.setLinkedEntity(linkedEntity);

        return Context.builder()
                .entity(entity)
                .model(model)
                .build();
    }

    private class WrongMockedEntity extends MockedEntity {
        private MockedLightEntity linkedEntity2;
        private MockedLightEntity linkedEntity3;

        public WrongMockedEntity(final Long id, final String uuid) {
            super(id, uuid);
        }

        public  MockedLightEntity getLinkedEntity2() {
            throw new RuntimeException();
        }

        public MockedLightEntity getLinkedEntity3() {
            return linkedEntity3;
        }

        public void setLinkedEntity3(final MockedLightEntity linkedEntity3) {
            this.linkedEntity3 = linkedEntity3;
        }
    }
}
