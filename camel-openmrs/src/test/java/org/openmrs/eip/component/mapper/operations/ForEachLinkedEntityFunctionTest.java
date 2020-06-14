package org.openmrs.eip.component.mapper.operations;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.MockedEntity;
import org.openmrs.eip.component.MockedModel;

import java.util.function.BiConsumer;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ForEachLinkedEntityFunctionTest {

    @Mock
    private BiConsumer<Context<MockedEntity, MockedModel>, String> action;

    private ForEachLinkedEntityFunction<MockedEntity, MockedModel> function = new ForEachLinkedEntityFunction<>();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void apply_should_call_action() {
        // Given
        MockedModel model = new MockedModel("uuid");
        Context<MockedEntity, MockedModel> context = getContext(model);

        // When
        MockedModel result = function.apply(context, action);

        // Then
        assertEquals(model, result);
        verify(action, times(4)).accept(any(Context.class), any(String.class));
    }

    private Context<MockedEntity, MockedModel> getContext(final MockedModel model) {
        MockedEntity entity = new MockedEntity(1L, "uuid");

        return new Context<>(entity, model, MappingDirectionEnum.ENTITY_TO_MODEL);
    }
}
