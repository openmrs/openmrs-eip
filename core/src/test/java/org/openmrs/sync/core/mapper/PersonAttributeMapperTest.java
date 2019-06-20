package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.sync.core.entity.PersonAttribute;
import org.openmrs.sync.core.entity.light.PersonAttributeTypeLight;
import org.openmrs.sync.core.entity.light.PersonLight;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.PersonAttributeModel;
import org.openmrs.sync.core.service.light.LightServiceNoContext;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonAttributeMapperTest extends AbstractMapperTest {

    @Mock
    protected LightServiceNoContext<UserLight> userService;

    @Mock
    protected LightServiceNoContext<PersonLight> personService;

    @Mock
    protected LightServiceNoContext<PersonAttributeTypeLight> persontAttributeTypeService;

    @InjectMocks
    private PersonAttributeMapperImpl mapper;

    private PersonLight person = initBaseModel(PersonLight.class, "person");
    private PersonAttributeTypeLight personAttributeType = initBaseModel(PersonAttributeTypeLight.class, "personAttributeType");

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void entityToModel() {
        // Given
        PersonAttribute ety = getPersonAttributeEty();

        // When
        PersonAttributeModel result = mapper.entityToModel(ety);

        // Then
        assertResult(ety, result);
    }

    @Test
    public void modelToEntity() {
        // Given
        PersonAttributeModel model = getVisitModel();
        when(userService.getOrInit("user")).thenReturn(user);
        when(personService.getOrInit("person")).thenReturn(person);
        when(persontAttributeTypeService.getOrInit("personAttributeType")).thenReturn(personAttributeType);

        // When
        PersonAttribute result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(final PersonAttribute ety, final PersonAttributeModel result) {
        assertEquals(ety.getUuid(), result.getUuid());
        assertEquals(ety.getDateCreated(), result.getDateCreated());
        assertEquals(ety.getDateChanged(), result.getDateChanged());
        assertEquals(ety.isVoided(), result.isVoided());
        assertEquals(ety.getDateVoided(), result.getDateVoided());
        assertEquals(ety.getVoidReason(), result.getVoidReason());
        assertEquals(ety.getVoidedBy().getUuid(), result.getVoidedByUuid());
        assertEquals(ety.getCreator().getUuid(), result.getCreatorUuid());
        assertEquals(ety.getChangedBy().getUuid(), result.getChangedByUuid());
        assertEquals(ety.getPerson().getUuid(), result.getPersonUuid());
        assertEquals(ety.getPersonAttributeType().getUuid(), result.getPersonAttributeTypeUuid());
        assertEquals(ety.getValue(), result.getValue());
    }

    private PersonAttribute getPersonAttributeEty() {
        PersonAttribute visit = new PersonAttribute();
        visit.setUuid("visit");
        visit.setDateCreated(LocalDateTime.of(2010, Month.JANUARY, 1, 10, 10));
        visit.setDateChanged(LocalDateTime.of(2011,Month.JANUARY, 1, 10, 10));
        visit.setVoided(true);
        visit.setDateVoided(LocalDateTime.of(2012,Month.JANUARY, 1, 10, 10));
        visit.setVoidReason("reason");
        visit.setVoidedBy(user);
        visit.setCreator(user);
        visit.setChangedBy(user);
        visit.setPerson(person);
        visit.setPersonAttributeType(personAttributeType);
        visit.setValue("value");

        return visit;
    }

    private void assertResult(final PersonAttributeModel model, final PersonAttribute result) {
        assertEquals(model.getUuid(), result.getUuid());
        assertEquals(model.getDateCreated(), result.getDateCreated());
        assertEquals(model.getDateChanged(), result.getDateChanged());
        assertEquals(model.isVoided(), result.isVoided());
        assertEquals(model.getDateVoided(), result.getDateVoided());
        assertEquals(model.getVoidReason(), result.getVoidReason());
        assertEquals(model.getVoidedByUuid(), result.getVoidedBy().getUuid());
        assertEquals(model.getCreatorUuid(), result.getCreator().getUuid());
        assertEquals(model.getChangedByUuid(), result.getChangedBy().getUuid());
        assertEquals(model.getPersonUuid(), result.getPerson().getUuid());
        assertEquals(model.getPersonAttributeTypeUuid(), result.getPersonAttributeType().getUuid());
        assertEquals(model.getValue(), result.getValue());
    }

    private PersonAttributeModel getVisitModel() {
        PersonAttributeModel visitModel = new PersonAttributeModel();
        visitModel.setCreatorUuid("user");
        visitModel.setVoidedByUuid("user");
        visitModel.setChangedByUuid("user");
        visitModel.setUuid("visit");
        visitModel.setDateCreated(LocalDateTime.of(2010, Month.JANUARY, 1, 10, 10));
        visitModel.setDateChanged(LocalDateTime.of(2011, Month.JANUARY, 1, 10, 10));
        visitModel.setVoided(true);
        visitModel.setDateVoided(LocalDateTime.of(2012, Month.JANUARY, 1, 10, 10));
        visitModel.setVoidReason("reason");
        visitModel.setPersonUuid("person");
        visitModel.setPersonAttributeTypeUuid("personAttributeType");
        visitModel.setValue("value");

        return visitModel;
    }
}
