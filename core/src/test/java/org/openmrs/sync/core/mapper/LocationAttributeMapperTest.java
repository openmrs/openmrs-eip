package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.LocationAttribute;
import org.openmrs.sync.core.entity.light.LocationAttributeTypeLight;
import org.openmrs.sync.core.entity.light.LocationLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.AttributeModel;
import org.openmrs.sync.core.service.light.impl.LocationAttributeTypeLightService;
import org.openmrs.sync.core.service.light.impl.LocationLightService;
import org.openmrs.sync.core.service.light.impl.UserLightService;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class LocationAttributeMapperTest extends AbstractMapperTest {

    @Mock
    protected LocationLightService locationService;

    @Mock
    protected LocationAttributeTypeLightService locationAttributeTypeService;

    @Mock
    protected UserLightService userService;

    @InjectMocks
    private LocationAttributeMapperImpl mapper;

    private UserLight user = initBaseModel(UserLight.class, "user");
    private LocationLight location = initBaseModel(LocationLight.class, "location");
    private LocationAttributeTypeLight attributeType = initBaseModel(LocationAttributeTypeLight.class, "attributeType");

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void entityToModel() {
        // Given
        LocationAttribute ety = getLocationAttributeEty();

        // When
        AttributeModel result = mapper.entityToModel(ety);

        // Then
        assertResult(ety, result);
    }

    @Test
    public void modelToEntity() {
        // Given
        AttributeModel model = getLocationAttributeModel();
        when(userService.getOrInit("user")).thenReturn(user);
        when(locationService.getOrInit("location")).thenReturn(location);
        when(locationAttributeTypeService.getOrInit("attributeType")).thenReturn(attributeType);

        // When
        LocationAttribute result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(final LocationAttribute ety, final AttributeModel result) {
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

    private LocationAttribute getLocationAttributeEty() {
        LocationAttribute locationAttribute = new LocationAttribute();

        locationAttribute.setUuid("locationAttribute");
        locationAttribute.setReferencedEntity(location);
        locationAttribute.setAttributeType(attributeType);
        locationAttribute.setValueReference("value");
        locationAttribute.setDateCreated(LocalDateTime.of(2010,Month.JANUARY, 1, 10, 11));
        locationAttribute.setDateChanged(LocalDateTime.of(2011, Month.JANUARY, 1, 10, 11));
        locationAttribute.setVoided(true);
        locationAttribute.setDateVoided(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 11));
        locationAttribute.setVoidReason("reason");
        locationAttribute.setVoidedBy(user);
        locationAttribute.setCreator(user);
        locationAttribute.setChangedBy(user);

        return locationAttribute;
    }

    private void assertResult(final AttributeModel model, final LocationAttribute result) {
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

    private AttributeModel getLocationAttributeModel() {
        AttributeModel attributeModel = new AttributeModel();

        attributeModel.setReferencedEntityUuid("location");
        attributeModel.setAttributeTypeUuid("attributeType");
        attributeModel.setValueReference("value");
        attributeModel.setCreatorUuid("user");
        attributeModel.setVoidedByUuid("user");
        attributeModel.setChangedByUuid("user");
        attributeModel.setUuid("locationAttribute");
        attributeModel.setDateCreated(LocalDateTime.of(2011,Month.JANUARY, 1, 10, 11));
        attributeModel.setDateChanged(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 11));
        attributeModel.setVoided(true);
        attributeModel.setDateVoided(LocalDateTime.of(2013,Month.JANUARY, 1, 10, 11));
        attributeModel.setVoidReason("reason");

        return attributeModel;
    }
}
