package org.openmrs.eip.component.mapper.operations;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.entity.MockedEntity;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.MockedModel;
import org.openmrs.eip.component.service.light.LightService;
import org.openmrs.eip.component.service.light.impl.UserLightService;
import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

import static org.mockito.Mockito.*;
import static org.junit.Assert.assertEquals;

public class LinkLightEntityConsumerTest {

    @Mock
    private ApplicationContext applicationContext;

    @Mock
    private UserLightService userService;

    private LinkLightEntityConsumer<MockedEntity, MockedModel> consumer;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        consumer = new LinkLightEntityConsumer<>(applicationContext);
    }

    @Test
    public void apply_should_call_service() {
        // Given
        MockedEntity entity = new MockedEntity(1L, "uuid");
        MockedModel model = new MockedModel("uuid");
        model.setCreatorUuid(UserLight.class.getName() + "(userUuid)");
        Context<MockedEntity, MockedModel> mapperContext = new Context<>(entity, model, MappingDirectionEnum.MODEL_TO_ENTITY);
        String[] userServiceClassName = new String[]{"userLightService"};
        when(applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(LightService.class, UserLight.class))).thenReturn(userServiceClassName);
        when(applicationContext.getBean(userServiceClassName[0])).thenReturn(userService);
        when(userService.getOrInitEntity("userUuid")).thenReturn(getUser());

        // When
        consumer.accept(mapperContext, "creatorUuid");

        // Then
        verify(userService).getOrInitEntity("userUuid");
        assertEquals(getUser(), mapperContext.getEntity().getCreator());
    }

    @Test
    public void apply_should_not_call_service_if_uuid_null() {
        // Given
        MockedEntity entity = new MockedEntity(1L, "uuid");
        MockedModel model = new MockedModel("uuid");
        Context<MockedEntity, MockedModel> mapperContext = new Context<>(entity, model, MappingDirectionEnum.MODEL_TO_ENTITY);
        String[] userServiceClassName = new String[]{"userLightService"};
        when(applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(LightService.class, UserLight.class))).thenReturn(userServiceClassName);
        when(applicationContext.getBean(userServiceClassName[0])).thenReturn(userService);
        when(userService.getOrInitEntity("userUuid")).thenReturn(getUser());

        // When
        consumer.accept(mapperContext, "creatorUuid");

        // Then
        verify(userService, never()).getOrInitEntity(anyString());
    }

    @Test
    public void apply_should_ignore_when_attribute_unknown() {
        // Given
        MockedEntity entity = new MockedEntity(1L, "uuid");
        MockedModel model = new MockedModel("uuid");
        model.setCreatorUuid("userUuid");
        Context<MockedEntity, MockedModel> mapperContext = new Context<>(entity, model, MappingDirectionEnum.MODEL_TO_ENTITY);

        // When
        consumer.accept(mapperContext, "unknownPropUuid");

        // Then
        verify(applicationContext, never()).getBeanNamesForType(any(ResolvableType.class));
        verify(applicationContext, never()).getBean(anyString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void apply_should_throw_illegal_argument_exception() {
        // Given
        MockedEntity entity = new MockedEntity(1L, "uuid");
        MockedModel model = new MockedModel("uuid");
        Context<MockedEntity, MockedModel> mapperContext = new Context<>(entity, model, MappingDirectionEnum.MODEL_TO_ENTITY);

        // When
        consumer.accept(mapperContext, "field1");

        // Then
    }

    @Test(expected = EIPException.class)
    public void apply_should_throw_exception_if_no_service_available_for_getter_return_type() {
        // Given
        MockedEntity entity = new MockedEntity(1L, "uuid");
        MockedModel model = new MockedModel("uuid");
        model.setCreatorUuid(UserLight.class.getName() + "(userUuid)");
        Context<MockedEntity, MockedModel> mapperContext = new Context<>(entity, model, MappingDirectionEnum.MODEL_TO_ENTITY);
        String[] userServiceClassName = new String[0];
        when(applicationContext.getBeanNamesForType(ResolvableType.forClassWithGenerics(LightService.class, UserLight.class))).thenReturn(userServiceClassName);
        when(userService.getOrInitEntity("userUuid")).thenReturn(getUser());

        // When
        consumer.accept(mapperContext, "creatorUuid");

        // Then
        verify(applicationContext, never()).getBean(anyString());
    }

    private UserLight getUser() {
        UserLight user = new UserLight();
        user.setUuid("userUuid");
        return user;
    }
}
