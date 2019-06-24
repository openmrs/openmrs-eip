package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.ProviderAttribute;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.entity.light.ProviderAttributeTypeLight;
import org.openmrs.sync.core.entity.light.ProviderLight;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.service.light.impl.UserLightService;
import org.openmrs.sync.core.service.light.impl.ProviderAttributeTypeLightService;
import org.openmrs.sync.core.service.light.impl.ProviderLightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ProviderAttributeMapperTest extends AbstractMapperTest {

    @Mock
    protected ProviderLightService providerService;

    @Mock
    protected ProviderAttributeTypeLightService providerAttributeTypeService;

    @Mock
    protected UserLightService userService;

    @InjectMocks
    private ProviderAttributeMapperImpl mapper;

    private UserLight user = initBaseModel(UserLight.class, "user");
    private ProviderLight provider = initBaseModel(ProviderLight.class, "provider");
    private ProviderAttributeTypeLight attributeType = initBaseModel(ProviderAttributeTypeLight.class, "attributeType");

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void entityToModel() {
        // Given
        ProviderAttribute ety = getProviderAttributeEty();

        // When
        AttributeModel result = mapper.entityToModel(ety);

        // Then
        assertResult(ety, result);
    }

    @Test
    public void modelToEntity() {
        // Given
        AttributeModel model = getProviderAttributeModel();
        when(userService.getOrInit("user")).thenReturn(user);
        when(providerService.getOrInit("provider")).thenReturn(provider);
        when(providerAttributeTypeService.getOrInit("attributeType")).thenReturn(attributeType);

        // When
        ProviderAttribute result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(final ProviderAttribute ety, final AttributeModel result) {
        assertEquals(ety.getUuid(), result.getUuid());
        assertEquals(ety.getAttributeType().getUuid(), result.getAttributeTypeUuid());
        assertEquals(ety.getReferencedEntity().getUuid(), result.getReferencedEntityUuid());
        assertEquals(ety.getValueReference(), result.getValueReference());
        assertEquals(ety.getDateCreated(), result.getDateCreated());
        assertEquals(ety.getDateChanged(), result.getDateChanged());
        assertEquals(ety.isVoided(), result.isVoided());
        assertEquals(ety.getDateVoided(), result.getDateVoided());
        assertEquals(ety.getVoidReason(), result.getVoidReason());
        assertEquals(ety.getVoidedBy().getUuid(), result.getVoidedByUuid());
        assertEquals(ety.getCreator().getUuid(), result.getCreatorUuid());
        assertEquals(ety.getChangedBy().getUuid(), result.getChangedByUuid());
    }

    private ProviderAttribute getProviderAttributeEty() {
        ProviderAttribute providerAttribute = new ProviderAttribute();

        providerAttribute.setUuid("providerAttribute");
        providerAttribute.setReferencedEntity(provider);
        providerAttribute.setAttributeType(attributeType);
        providerAttribute.setValueReference("value");
        providerAttribute.setDateCreated(LocalDateTime.of(2010,Month.JANUARY, 1, 10, 11));
        providerAttribute.setDateChanged(LocalDateTime.of(2011, Month.JANUARY, 1, 10, 11));
        providerAttribute.setVoided(true);
        providerAttribute.setDateVoided(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 11));
        providerAttribute.setVoidReason("reason");
        providerAttribute.setVoidedBy(user);
        providerAttribute.setCreator(user);
        providerAttribute.setChangedBy(user);

        return providerAttribute;
    }

    private void assertResult(final AttributeModel model, final ProviderAttribute result) {
        assertEquals(model.getReferencedEntityUuid(), result.getReferencedEntity().getUuid());
        assertEquals(model.getAttributeTypeUuid(), result.getAttributeType().getUuid());
        assertEquals(model.getValueReference(), result.getValueReference());
        assertEquals(model.getCreatorUuid(), result.getCreator().getUuid());
        assertEquals(model.getVoidedByUuid(), result.getVoidedBy().getUuid());
        assertEquals(model.getChangedByUuid(), result.getChangedBy().getUuid());
        assertEquals(model.getUuid(), result.getUuid());
        assertEquals(model.getDateCreated(), result.getDateCreated());
        assertEquals(model.getDateChanged(), result.getDateChanged());
        assertEquals(model.isVoided(), result.isVoided());
        assertEquals(model.getDateVoided(), result.getDateVoided());
        assertEquals(model.getVoidReason(), result.getVoidReason());
    }

    private AttributeModel getProviderAttributeModel() {
        AttributeModel attributeModel = new AttributeModel();

        attributeModel.setReferencedEntityUuid("provider");
        attributeModel.setAttributeTypeUuid("attributeType");
        attributeModel.setValueReference("value");
        attributeModel.setCreatorUuid("user");
        attributeModel.setVoidedByUuid("user");
        attributeModel.setChangedByUuid("user");
        attributeModel.setUuid("providerAttribute");
        attributeModel.setDateCreated(LocalDateTime.of(2011,Month.JANUARY, 1, 10, 11));
        attributeModel.setDateChanged(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 11));
        attributeModel.setVoided(true);
        attributeModel.setDateVoided(LocalDateTime.of(2013,Month.JANUARY, 1, 10, 11));
        attributeModel.setVoidReason("reason");

        return attributeModel;
    }
}
