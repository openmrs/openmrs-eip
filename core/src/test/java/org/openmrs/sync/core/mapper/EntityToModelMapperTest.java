package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.BaseEntity;
import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.entity.MockedLightEntity;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.MockedModel;

import java.beans.PropertyDescriptor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class EntityToModelMapperTest {

    @Mock
    private Function<BaseEntity, Context> instantiateModel;

    @Mock
    private UnaryOperator<Context> copyStandardFields;

    @Mock
    private BiConsumer<Context, PropertyDescriptor> extractUuid;

    @Mock
    private BiFunction<Context, BiConsumer<Context, PropertyDescriptor>, Context> forEachLinkedEntity;

    private EntityToModelMapper mapper;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        when(instantiateModel.andThen(any())).thenCallRealMethod();
        when(copyStandardFields.andThen(any())).thenCallRealMethod();
        when(forEachLinkedEntity.andThen(any())).thenCallRealMethod();

        mapper = new EntityToModelMapper(instantiateModel, copyStandardFields, extractUuid, forEachLinkedEntity);
    }

    @Test
    public void entityToModel_should_return_model() {
        // Given
        MockedLightEntity entity = new MockedLightEntity(1L, "uuid");
        MockedModel expectedModel = new MockedModel("uuid");
        Context context = Context.builder()
                .entity(entity)
                .model(expectedModel)
                .build();
        when(instantiateModel.apply(entity)).thenReturn(context);
        when(copyStandardFields.apply(context)).thenReturn(context);
        when(forEachLinkedEntity.apply(context, extractUuid)).thenReturn(context);

        // When
        MockedModel result = (MockedModel) mapper.entityToModel(entity);

        // Then
        assertEquals(expectedModel, result);
        verify(instantiateModel).apply(entity);
        verify(copyStandardFields).apply(context);
        verify(forEachLinkedEntity).apply(context, extractUuid);
    }
}
