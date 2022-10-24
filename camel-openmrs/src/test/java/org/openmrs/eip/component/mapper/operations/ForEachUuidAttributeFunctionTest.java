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

public class ForEachUuidAttributeFunctionTest {
	
	@Mock
	private BiConsumer<Context<MockedEntity, MockedModel>, String> action;
	
	private ForEachUuidAttributeFunction<MockedEntity, MockedModel> function = new ForEachUuidAttributeFunction<>();
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void apply_should_call_action() {
		// Given
		MockedEntity entity = new MockedEntity(1L, "uuid");
		Context<MockedEntity, MockedModel> context = getContext(entity);
		
		// When
		MockedEntity result = function.apply(context, action);
		
		// Then
		assertEquals(entity, result);
		verify(action, times(2)).accept(any(Context.class), any(String.class));
	}
	
	private Context<MockedEntity, MockedModel> getContext(final MockedEntity entity) {
		MockedModel model = new MockedModel("uuid");
		
		return new Context<>(entity, model, MappingDirectionEnum.ENTITY_TO_MODEL);
	}
}
