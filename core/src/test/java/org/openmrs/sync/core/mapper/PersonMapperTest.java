package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.PersonModel;
import org.junit.Test;
import org.openmrs.sync.core.service.SimpleService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonMapperTest {

    @Mock
    private SimpleService<UserLight> userService;

    @Mock
    private SimpleService<ConceptLight> conceptService;

    @InjectMocks
    private PersonMapperImpl mapper;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void entityToModel() {
        // Given
        Person ety = getPersonEty();

        // When
        PersonModel result = mapper.entityToModel(ety);

        // Then
        assertResult(ety, result);
    }

    @Test
    public void modelToEntity() {
        // Given
        PersonModel model = getPersonModel(false);
        when(conceptService.getOrInit("concept")).thenReturn(getConcept());
        when(userService.getOrInit("user")).thenReturn(getUser());

        // When
        Person result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    @Test
    public void modelToEntityWithNullDependencies() {
        // Given
        PersonModel model = getPersonModel(true);
        when(conceptService.getOrInit("concept")).thenReturn(getConcept());
        when(userService.getOrInit("user")).thenReturn(getUser());

        // When
        Person result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(Person ety, PersonModel result) {
        assertEquals(ety.getGender(), result.getGender());
        assertEquals(ety.getBirthdate(), result.getBirthdate());
        assertEquals(ety.isBirthdateEstimated(), result.getBirthdateEstimated());
        assertEquals(ety.isDead(), result.getDead());
        assertEquals(ety.getDeathDate(), result.getDeathDate());
        assertEquals(ety.getCauseOfDeath().getUuid(), result.getCauseOfDeathUuid());
        assertEquals(ety.getCreator().getUuid(), result.getPersonCreatorUuid());
        assertEquals(ety.getDateCreated(), result.getPersonDateCreated());
        assertEquals(ety.getChangedBy().getUuid(), result.getPersonChangedByUuid());
        assertEquals(ety.getDateChanged(), result.getPersonDateChanged());
        assertEquals(ety.isVoided(), result.getPersonVoided());
        assertEquals(ety.getVoidedBy().getUuid(), result.getPersonVoidedByUuid());
        assertEquals(ety.getDateVoided(), result.getPersonDateVoided());
        assertEquals(ety.getVoidReason(), result.getPersonVoidReason());
        assertEquals(ety.getUuid(), result.getUuid());
        assertEquals(ety.isDeathdateEstimated(), result.getDeathdateEstimated());
        assertEquals(ety.getBirthtime(), result.getBirthtime());
    }

    private Person getPersonEty() {
        ConceptLight concept = new ConceptLight();
        concept.setUuid("causeOfDeath");

        UserLight user = new UserLight();
        user.setUuid("userId");

        Person ety = new Person();
        ety.setGender("M");
        ety.setBirthdate(LocalDate.of(1956, Month.OCTOBER, 22));
        ety.setBirthdateEstimated(true);
        ety.setDead(false);
        ety.setDeathDate(LocalDate.of(1988, Month.NOVEMBER, 1));
        ety.setCauseOfDeath(concept);
        ety.setCreator(user);
        ety.setDateCreated(LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0));
        ety.setChangedBy(user);
        ety.setDateChanged(LocalDateTime.of(2011, Month.JANUARY, 1, 0, 0));
        ety.setVoided(true);
        ety.setVoidedBy(user);
        ety.setDateVoided(LocalDateTime.of(2012, Month.JANUARY, 1, 0, 0));
        ety.setVoidReason("voided");
        ety.setUuid("person");
        ety.setDeathdateEstimated(false);
        ety.setBirthtime(LocalTime.of(10, 10));
        return ety;
    }

    private void assertResult(PersonModel model, Person result) {
        assertEquals(model.getGender(), result.getGender());
        assertEquals(model.getBirthdate(), result.getBirthdate());
        assertEquals(model.getBirthdateEstimated(), result.isBirthdateEstimated());
        assertEquals(model.getDead(), result.isDead());
        assertEquals(model.getDeathDate(), result.getDeathDate());
        if (model.getCauseOfDeathUuid() != null) {
            assertEquals(model.getCauseOfDeathUuid(), result.getCauseOfDeath().getUuid());
        } else {
            assertNull(result.getCauseOfDeath());
        }
        if (model.getPersonCreatorUuid() != null) {
            assertEquals(model.getPersonCreatorUuid(), result.getCreator().getUuid());
        } else {
            assertNull(result.getCreator());
        }
        assertEquals(model.getPersonDateCreated(), result.getDateCreated());
        if (model.getPersonChangedByUuid() != null) {
            assertEquals(model.getPersonChangedByUuid(), result.getChangedBy().getUuid());
        } else {
            assertNull(result.getChangedBy());
        }
        assertEquals(model.getPersonDateChanged(), result.getDateChanged());
        assertEquals(model.getPersonVoided(), result.isVoided());
        if (model.getPersonVoidedByUuid() != null) {
            assertEquals(model.getPersonVoidedByUuid(), result.getVoidedBy().getUuid());
        } else {
            assertNull(result.getVoidedBy());
        }
        assertEquals(model.getPersonDateVoided(), result.getDateVoided());
        assertEquals(model.getPersonVoidReason(), result.getVoidReason());
        assertEquals(model.getUuid(), result.getUuid());
        assertEquals(model.getDeathdateEstimated(), result.isDeathdateEstimated());
        assertEquals(model.getBirthtime(), result.getBirthtime());
    }

    private PersonModel getPersonModel(boolean nullDependencies) {
        PersonModel model = new PersonModel();
        model.setGender("M");
        model.setBirthdate(LocalDate.of(1956, Month.OCTOBER, 22));
        model.setBirthdateEstimated(false);
        model.setDead(false);
        model.setDeathDate(LocalDate.of(1988, Month.NOVEMBER, 1));
        model.setCauseOfDeathUuid(nullDependencies ? null : "concept");
        model.setPersonCreatorUuid(nullDependencies ? null : "user");
        model.setPersonDateCreated(nullDependencies ? null : LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0));
        model.setPersonChangedByUuid(nullDependencies ? null : "user");
        model.setPersonDateChanged(nullDependencies ? null : LocalDateTime.of(2011, Month.JANUARY, 1, 0, 0));
        model.setPersonVoided(true);
        model.setPersonVoidedByUuid(nullDependencies ? null : "user");
        model.setPersonDateVoided(nullDependencies ? null : LocalDateTime.of(1012, Month.JANUARY, 1, 0, 0));
        model.setPersonVoidReason(nullDependencies ? null : "voided");
        model.setUuid("person");
        model.setDeathdateEstimated(false);
        model.setBirthtime(LocalTime.of(10, 10));
        return model;
    }

    private UserLight getUser() {
        UserLight user = new UserLight();
        user.setUuid("user");
        return user;
    }

    private ConceptLight getConcept() {
        ConceptLight concept = new ConceptLight();
        concept.setUuid("concept");
        return concept;
    }
}
