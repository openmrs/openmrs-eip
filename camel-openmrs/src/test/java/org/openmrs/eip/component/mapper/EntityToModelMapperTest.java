package org.openmrs.eip.component.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.mapper.operations.Context;
import org.openmrs.eip.component.mapper.operations.MappingDirectionEnum;
import org.openmrs.eip.component.entity.MockedEntity;
import org.openmrs.eip.component.MockedModel;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EntityToModelMapperTest {
	
	@Mock
	private Function<MockedEntity, Context<MockedEntity, MockedModel>> instantiateModel;
	
	@Mock
	private UnaryOperator<Context<MockedEntity, MockedModel>> copyStandardFields;
	
	@Mock
	private BiConsumer<Context<MockedEntity, MockedModel>, String> extractUuid;
	
	@Mock
	private BiFunction<Context<MockedEntity, MockedModel>, BiConsumer<Context<MockedEntity, MockedModel>, String>, MockedModel> forEachLinkedEntity;
	
	private EntityToModelMapper<MockedEntity, MockedModel> mapper;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		when(instantiateModel.andThen(any())).thenCallRealMethod();
		when(copyStandardFields.andThen(any())).thenCallRealMethod();
		when(forEachLinkedEntity.andThen(any())).thenCallRealMethod();
		
		mapper = new EntityToModelMapper<>(instantiateModel, copyStandardFields, extractUuid, forEachLinkedEntity);
	}
	
	@Test
	public void entityToModel_should_return_model() {
		// Given
		MockedEntity entity = new MockedEntity(1L, "uuid");
		MockedModel expectedModel = new MockedModel("uuid");
		Context<MockedEntity, MockedModel> context = new Context<>(entity, expectedModel,
		        MappingDirectionEnum.ENTITY_TO_MODEL);
		
		when(instantiateModel.apply(entity)).thenReturn(context);
		when(copyStandardFields.apply(context)).thenReturn(context);
		when(forEachLinkedEntity.apply(context, extractUuid)).thenReturn(expectedModel);
		
		// When
		MockedModel result = mapper.apply(entity);
		
		// Then
		assertEquals(expectedModel, result);
		verify(instantiateModel).apply(entity);
		verify(copyStandardFields).apply(context);
		verify(forEachLinkedEntity).apply(context, extractUuid);
	}
}
