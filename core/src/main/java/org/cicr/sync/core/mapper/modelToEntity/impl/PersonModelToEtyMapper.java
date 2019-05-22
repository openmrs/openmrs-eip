package org.cicr.sync.core.mapper.modelToEntity.impl;

import org.cicr.sync.core.entity.PersonEty;
import org.cicr.sync.core.mapper.modelToEntity.ModelToEntityMapper;
import org.cicr.sync.core.model.PersonModel;
import org.cicr.sync.core.service.impl.ConceptService;
import org.cicr.sync.core.service.impl.UserService;
import org.springframework.stereotype.Component;

@Component
public class PersonModelToEtyMapper implements ModelToEntityMapper<PersonModel, PersonEty> {

    private ConceptService conceptService;
    private UserService userService;

    public PersonModelToEtyMapper(final UserService userService,
                                  final ConceptService conceptService) {
        this.userService = userService;
        this.conceptService = conceptService;
    }

    @Override
    public PersonEty apply(final PersonModel person) {
        PersonEty ety = new PersonEty();
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
