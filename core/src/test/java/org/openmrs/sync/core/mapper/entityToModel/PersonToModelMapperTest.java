package org.openmrs.sync.core.mapper.entityToModel;

import org.openmrs.sync.core.entity.Concept;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.entity.User;
import org.openmrs.sync.core.model.PersonModel;
import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class PersonToModelMapperTest {

    private PersonEtyToModelMapper mapper = new PersonEtyToModelMapper();

    @Test
    public void apply() {
        // Given
        Person ety = getPersonEty();

        // When
        PersonModel result = mapper.apply(ety);

        // Then
        assertResult(ety, result);
    }

    private void assertResult(Person ety, PersonModel result) {
        assertEquals(ety.getGender(), result.getGender());
        assertEquals(ety.getBirthdate(), result.getBirthdate());
        assertEquals(ety.isBirthdateEstimated(), result.getBirthdateEstimated());
        assertEquals(ety.isDead(), result.getDead());
        assertEquals(ety.getDeathDate(), result.getDeathDate());
        assertEquals(ety.getCauseOfDeath().getUuid(), result.getCauseOfDeathUUID());
        assertEquals(ety.getCreator().getUuid(), result.getCreatorUUID());
        assertEquals(ety.getDateCreated(), result.getDateCreated());
        assertEquals(ety.getChangedBy().getUuid(), result.getChangedByUUID());
        assertEquals(ety.getDateChanged(), result.getDateChanged());
        assertEquals(ety.isVoided(), result.getVoided());
        assertEquals(ety.getVoidedBy().getUuid(), result.getVoidedByUUID());
        assertEquals(ety.getDateVoided(), result.getDateVoided());
        assertEquals(ety.getVoidReason(), result.getVoidReason());
        assertEquals(ety.getUuid(), result.getUuid());
        assertEquals(ety.isDeathdateEstimated(), result.getDeathdateEstimated());
        assertEquals(ety.getBirthtime(), result.getBirthtime());
    }

    private Person getPersonEty() {
        Concept concept = new Concept();
        concept.setUuid("causeOfDeath");

        User user = new User();
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
}
