package org.openmrs.sync.core.mapper.modelToEntity;

import org.openmrs.sync.core.entity.Concept;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.entity.User;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.service.SimpleService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

public class PersonModelToEtyMapperTest {

    @Mock
    private SimpleService<User> userService;

    @Mock
    private SimpleService<Concept> conceptService;

    private PersonModelToEtyMapper mapper;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        mapper = new PersonModelToEtyMapper(userService, conceptService);
    }

    @Test
    public void apply() {
        // Given
        PersonModel model = getPersonModel(false);
        when(conceptService.getOrInit("concept")).thenReturn(getConceptEty());
        when(userService.getOrInit("user")).thenReturn(getUserEty());

        // When
        Person result = mapper.apply(model);

        // Then
        assertResult(model, result);
    }

    @Test
    public void applyWithNullDependencies() {
        // Given
        PersonModel model = getPersonModel(true);
        when(conceptService.getOrInit("concept")).thenReturn(getConceptEty());
        when(userService.getOrInit("user")).thenReturn(getUserEty());

        // When
        Person result = mapper.apply(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(PersonModel model, Person result) {
        assertEquals(model.getGender(), result.getGender());
        assertEquals(model.getBirthdate(), result.getBirthdate());
        assertEquals(model.getBirthdateEstimated(), result.isBirthdateEstimated());
        assertEquals(model.getDead(), result.isDead());
        assertEquals(model.getDeathDate(), result.getDeathDate());
        if (model.getCauseOfDeathUUID() != null) {
            assertEquals(model.getCauseOfDeathUUID(), result.getCauseOfDeath().getUuid());
        } else {
            assertNull(result.getCauseOfDeath());
        }
        if (model.getCreatorUUID() != null) {
            assertEquals(model.getCreatorUUID(), result.getCreator().getUuid());
        } else {
            assertNull(result.getCreator());
        }
        assertEquals(model.getDateCreated(), result.getDateCreated());
        if (model.getChangedByUUID() != null) {
            assertEquals(model.getChangedByUUID(), result.getChangedBy().getUuid());
        } else {
            assertNull(result.getChangedBy());
        }
        assertEquals(model.getDateChanged(), result.getDateChanged());
        assertEquals(model.getVoided(), result.isVoided());
        if (model.getVoidedByUUID() != null) {
            assertEquals(model.getVoidedByUUID(), result.getVoidedBy().getUuid());
        } else {
            assertNull(result.getVoidedBy());
        }
        assertEquals(model.getDateVoided(), result.getDateVoided());
        assertEquals(model.getVoidReason(), result.getVoidReason());
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
        model.setCauseOfDeathUUID(nullDependencies ? null : "concept");
        model.setCreatorUUID(nullDependencies ? null : "user");
        model.setDateCreated(nullDependencies ? null : LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0));
        model.setChangedByUUID(nullDependencies ? null : "user");
        model.setDateChanged(nullDependencies ? null : LocalDateTime.of(2011, Month.JANUARY, 1, 0, 0));
        model.setVoided(true);
        model.setVoidedByUUID(nullDependencies ? null : "user");
        model.setDateVoided(nullDependencies ? null : LocalDateTime.of(1012, Month.JANUARY, 1, 0, 0));
        model.setVoidReason(nullDependencies ? null : "voided");
        model.setUuid("person");
        model.setDeathdateEstimated(false);
        model.setBirthtime(LocalTime.of(10, 10));
        return model;
    }

    private User getUserEty() {
        User user = new User();
        user.setUuid("user");
        return user;
    }

    private Concept getConceptEty() {
        Concept concept = new Concept();
        concept.setUuid("concept");
        return concept;
    }
}
