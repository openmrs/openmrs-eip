package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.ConceptAttribute;
import org.openmrs.sync.core.entity.light.ConceptAttributeTypeLight;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.ConceptAttributeModel;
import org.openmrs.sync.core.service.light.impl.ConceptAttributeTypeLightService;
import org.openmrs.sync.core.service.light.impl.ConceptLightService;
import org.openmrs.sync.core.service.light.impl.UserLightService;
import org.openmrs.sync.core.service.light.impl.context.ConceptContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ConceptAttributeMapperTest extends AbstractMapperTest {

    @Mock
    protected ConceptLightService conceptService;

    @Mock
    protected ConceptAttributeTypeLightService conceptAttributeTypeService;

    @Mock
    protected UserLightService userService;

    @InjectMocks
    private ConceptAttributeMapperImpl mapper;

    private UserLight user = initBaseModel(UserLight.class, "user");
    private ConceptLight concept = initBaseModel(ConceptLight.class, "concept");
    private ConceptAttributeTypeLight attributeType = initBaseModel(ConceptAttributeTypeLight.class, "attributeType");

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void entityToModel() {
        // Given
        ConceptAttribute ety = getConceptAttributeEty();

        // When
        ConceptAttributeModel result = mapper.entityToModel(ety);

        // Then
        assertResult(ety, result);
    }

    @Test
    public void modelToEntity() {
        // Given
        ConceptContext conceptContext = ConceptContext.builder()
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .build();

        ConceptAttributeModel model = getConceptAttributeModel();
        when(userService.getOrInit("user")).thenReturn(user);
        when(conceptService.getOrInit("concept", conceptContext)).thenReturn(concept);
        when(conceptAttributeTypeService.getOrInit("attributeType")).thenReturn(attributeType);

        // When
        ConceptAttribute result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(final ConceptAttribute ety, final ConceptAttributeModel result) {
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

    private ConceptAttribute getConceptAttributeEty() {
        ConceptAttribute conceptAttribute = new ConceptAttribute();

        conceptAttribute.setUuid("conceptAttribute");
        conceptAttribute.setReferencedEntity(concept);
        conceptAttribute.setAttributeType(attributeType);
        conceptAttribute.setValueReference("value");
        conceptAttribute.setDateCreated(LocalDateTime.of(2010,Month.JANUARY, 1, 10, 11));
        conceptAttribute.setDateChanged(LocalDateTime.of(2011, Month.JANUARY, 1, 10, 11));
        conceptAttribute.setVoided(true);
        conceptAttribute.setDateVoided(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 11));
        conceptAttribute.setVoidReason("reason");
        conceptAttribute.setVoidedBy(user);
        conceptAttribute.setCreator(user);
        conceptAttribute.setChangedBy(user);

        return conceptAttribute;
    }

    private void assertResult(final ConceptAttributeModel model, final ConceptAttribute result) {
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

    private ConceptAttributeModel getConceptAttributeModel() {
        ConceptAttributeModel attributeModel = new ConceptAttributeModel();

        attributeModel.setConceptClassUuid("conceptClass");
        attributeModel.setConceptDatatypeUuid("conceptDatatype");
        attributeModel.setReferencedEntityUuid("concept");
        attributeModel.setAttributeTypeUuid("attributeType");
        attributeModel.setValueReference("value");
        attributeModel.setCreatorUuid("user");
        attributeModel.setVoidedByUuid("user");
        attributeModel.setChangedByUuid("user");
        attributeModel.setUuid("conceptAttribute");
        attributeModel.setDateCreated(LocalDateTime.of(2011,Month.JANUARY, 1, 10, 11));
        attributeModel.setDateChanged(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 11));
        attributeModel.setVoided(true);
        attributeModel.setDateVoided(LocalDateTime.of(2013,Month.JANUARY, 1, 10, 11));
        attributeModel.setVoidReason("reason");

        return attributeModel;
    }
}
