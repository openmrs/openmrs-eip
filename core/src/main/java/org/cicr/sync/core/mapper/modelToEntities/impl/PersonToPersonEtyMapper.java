package org.cicr.sync.core.mapper.modelToEntities.impl;

import org.cicr.sync.core.entity.PersonEty;
import org.cicr.sync.core.mapper.modelToEntities.ModelToEntityMapper;
import org.cicr.sync.core.model.PersonModel;
import org.cicr.sync.core.servicedeprecated.ConceptService;
import org.cicr.sync.core.servicedeprecated.UserService;
import org.springframework.stereotype.Component;

@Component
public class PersonToPersonEtyMapper implements ModelToEntityMapper<PersonModel, PersonEty> {

    private ConceptService conceptService;
    private UserService userService;

    public PersonToPersonEtyMapper(final UserService userService,
                                   final ConceptService conceptService) {
        this.userService = userService;
        this.conceptService = conceptService;
    }

    @Override
    public PersonEty apply(PersonModel person) {
        PersonEty ety = new PersonEty();
        ety.setId(person.getPersonId());
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
