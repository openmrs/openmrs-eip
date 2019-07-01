package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.entity.MockedLightEntity;
import org.openmrs.sync.core.model.MockedModel;

import java.beans.PropertyDescriptor;
import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ForEachLinkedEntityFunctionTest {

    @Mock
    private BiConsumer<Context, PropertyDescriptor> action;

    private ForEachLinkedEntityFunction function = new ForEachLinkedEntityFunction();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void apply_should_call_action() {
        // Given
        MockedModel model = new MockedModel("uuid");
        Context context = getContext(model);

        // When
        Context result = function.apply(context, action);

        // Then
        assertEquals(result, context);
        verify(action, times(4)).accept(any(Context.class), any(PropertyDescriptor.class));
    }

    private Context getContext(final MockedModel model) {
        MockedLightEntity linkedEntity = new MockedLightEntity(2L, "uuid2");
        MockedEntity entity = new MockedEntity(1L, "uuid");
        entity.setLinkedEntity(linkedEntity);

        return Context.builder()
                .entity(entity)
                .model(model)
                .build();
    }
}
