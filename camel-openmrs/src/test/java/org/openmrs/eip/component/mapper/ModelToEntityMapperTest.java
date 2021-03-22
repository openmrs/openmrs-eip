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

public class ModelToEntityMapperTest {

    @Mock
    private Function<MockedModel, Context<MockedEntity, MockedModel>> instantiateEntity;

    @Mock
    private UnaryOperator<Context<MockedEntity, MockedModel>> copyStandardFields;

    @Mock
    private BiConsumer<Context<MockedEntity, MockedModel>, String> linkLightEntity;

    @Mock
    private BiFunction<Context<MockedEntity, MockedModel>, BiConsumer<Context<MockedEntity, MockedModel>, String>, MockedEntity> forEachUuidAttribute;

    private ModelToEntityMapper<MockedModel, MockedEntity> mapper;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        when(instantiateEntity.andThen(any())).thenCallRealMethod();
        when(copyStandardFields.andThen(any())).thenCallRealMethod();
        when(forEachUuidAttribute.andThen(any())).thenCallRealMethod();

        mapper = new ModelToEntityMapper<>(instantiateEntity, copyStandardFields, linkLightEntity, forEachUuidAttribute);
    }

    @Test
    public void entityToModel_should_return_model() {
        // Given
        MockedModel model = new MockedModel("uuid");
        MockedEntity expectedEntity = new MockedEntity(1L, "uuid");
        Context<MockedEntity, MockedModel> context = new Context<>(expectedEntity, model, MappingDirectionEnum.ENTITY_TO_MODEL);

        when(instantiateEntity.apply(model)).thenReturn(context);
        when(copyStandardFields.apply(context)).thenReturn(context);
        when(forEachUuidAttribute.apply(context, linkLightEntity)).thenReturn(expectedEntity);

        // When
        MockedEntity result = mapper.apply(model);

        // Then
        assertEquals(expectedEntity, result);
        verify(instantiateEntity).apply(model);
        verify(copyStandardFields).apply(context);
        verify(forEachUuidAttribute).apply(context, linkLightEntity);
    }
}
