package org.cicr.sync.central.mapper;

import org.cicr.sync.central.service.ConceptService;
import org.cicr.sync.central.service.UserService;
import org.cicr.sync.core.entity.PersonEty;
import org.cicr.sync.core.model.Person;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PersonToPersonEtyMapper implements Function<Person, PersonEty> {

    private ConceptService conceptService;
    private UserService userService;

    public PersonToPersonEtyMapper(final UserService userService,
                                   final ConceptService conceptService) {
        this.userService = userService;
        this.conceptService = conceptService;
    }

    @Override
    public PersonEty apply(Person person) {
        PersonEty ety = new PersonEty();
        ety.setPersonId(person.getPersonId());
        ety.setGender(person.getGender());
        ety.setBirthdate(person.getBirthdate());
        ety.setBirthdateEstimated(person.getBirthdateEstimated());
        ety.setDead(person.getDead());
        ety.setDeathDate(person.getDeathDate());
        ety.setCauseOfDeath(person.getCauseOfDeathUUID() == null ? null : conceptService.getOrInitConcept(person.getCauseOfDeathUUID()));
        ety.setCreator(person.getCreatorUUID() == null ? null : userService.getOrInitUser(person.getCreatorUUID()));
        ety.setDateCreated(person.getDateCreated());
        ety.setChangedBy(person.getChangedByUUID() == null ? null : userService.getOrInitUser(person.getChangedByUUID()));
        ety.setDateChanged(person.getDateChanged());
        ety.setVoided(person.getVoided());
        ety.setVoidedBy(person.getVoidedByUUID() == null ? null : userService.getOrInitUser(person.getVoidedByUUID()));
        ety.setDateVoided(person.getDateVoided());
        ety.setVoidReason(person.getVoidReason());
        ety.setUuid(person.getUuid());
        ety.setDeathdateEstimated(person.getDeathdateEstimated());
        ety.setBirthtime(person.getBirthtime());

        return ety;
    }
}
