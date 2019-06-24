package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.VisitAttribute;
import org.openmrs.sync.core.entity.light.VisitAttributeTypeLight;
import org.openmrs.sync.core.entity.light.VisitLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.VisitAttributeModel;
import org.openmrs.sync.core.service.light.impl.VisitAttributeTypeLightService;
import org.openmrs.sync.core.service.light.impl.VisitLightService;
import org.openmrs.sync.core.service.light.impl.UserLightService;
import org.openmrs.sync.core.service.light.impl.context.VisitContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class VisitAttributeMapperTest extends AbstractMapperTest {

    @Mock
    protected VisitLightService visitService;

    @Mock
    protected VisitAttributeTypeLightService visitAttributeTypeService;

    @Mock
    protected UserLightService userService;

    @InjectMocks
    private VisitAttributeMapperImpl mapper;

    private UserLight user = initBaseModel(UserLight.class, "user");
    private VisitLight visit = initBaseModel(VisitLight.class, "visit");
    private VisitAttributeTypeLight attributeType = initBaseModel(VisitAttributeTypeLight.class, "attributeType");

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void entityToModel() {
        // Given
        VisitAttribute ety = getVisitAttributeEty();

        // When
        VisitAttributeModel result = mapper.entityToModel(ety);

        // Then
        assertResult(ety, result);
    }

    @Test
    public void modelToEntity() {
        // Given
        VisitContext visitContext = VisitContext.builder()
                .visitTypeUuid("visitType")
                .patientUuid("patient")
                .build();

        VisitAttributeModel model = getVisitAttributeModel();
        when(userService.getOrInit("user")).thenReturn(user);
        when(visitService.getOrInit("visit", visitContext)).thenReturn(visit);
        when(visitAttributeTypeService.getOrInit("attributeType")).thenReturn(attributeType);

        // When
        VisitAttribute result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(final VisitAttribute ety, final VisitAttributeModel result) {
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

    private VisitAttribute getVisitAttributeEty() {
        VisitAttribute visitAttribute = new VisitAttribute();

        visitAttribute.setUuid("visitAttribute");
        visitAttribute.setReferencedEntity(visit);
        visitAttribute.setAttributeType(attributeType);
        visitAttribute.setValueReference("value");
        visitAttribute.setDateCreated(LocalDateTime.of(2010,Month.JANUARY, 1, 10, 11));
        visitAttribute.setDateChanged(LocalDateTime.of(2011, Month.JANUARY, 1, 10, 11));
        visitAttribute.setVoided(true);
        visitAttribute.setDateVoided(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 11));
        visitAttribute.setVoidReason("reason");
        visitAttribute.setVoidedBy(user);
        visitAttribute.setCreator(user);
        visitAttribute.setChangedBy(user);

        return visitAttribute;
    }

    private void assertResult(final VisitAttributeModel model, final VisitAttribute result) {
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

    private VisitAttributeModel getVisitAttributeModel() {
        VisitAttributeModel attributeModel = new VisitAttributeModel();

        attributeModel.setReferencedEntityUuid("visit");
        attributeModel.setPatientUuid("patient");
        attributeModel.setVisitTypeUuid("visitType");
        attributeModel.setAttributeTypeUuid("attributeType");
        attributeModel.setValueReference("value");
        attributeModel.setCreatorUuid("user");
        attributeModel.setVoidedByUuid("user");
        attributeModel.setChangedByUuid("user");
        attributeModel.setUuid("visitAttribute");
        attributeModel.setDateCreated(LocalDateTime.of(2011,Month.JANUARY, 1, 10, 11));
        attributeModel.setDateChanged(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 11));
        attributeModel.setVoided(true);
        attributeModel.setDateVoided(LocalDateTime.of(2013,Month.JANUARY, 1, 10, 11));
        attributeModel.setVoidReason("reason");

        return attributeModel;
    }
}
