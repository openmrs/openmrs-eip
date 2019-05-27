package org.openmrs.sync.core.mapper.modelToEntity;

import org.openmrs.sync.core.entity.Concept;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.entity.User;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.service.SimpleService;
import org.springframework.stereotype.Component;

@Component
public class PersonModelToEtyMapper implements java.util.function.Function<PersonModel, Person> {

    private SimpleService<User> userService;
    private SimpleService<Concept> conceptService;

    public PersonModelToEtyMapper(final SimpleService<User> userService,
                                  final SimpleService<Concept> conceptService) {
        this.userService = userService;
        this.conceptService = conceptService;
    }

    @Override
    public Person apply(final PersonModel person) {
        Person ety = new Person();
        ety.setGender(person.getGender());
        ety.setBirthdate(person.getBirthdate());
        ety.setBirthdateEstimated(person.getBirthdateEstimated());
        ety.setDead(person.getDead());
        ety.setDeathDate(person.getDeathDate());
        ety.setCauseOfDeath(person.getCauseOfDeathUUID() == null ? null : conceptService.getOrInit(person.getCauseOfDeathUUID()));
        ety.setCreator(person.getCreatorUUID() == null ? null : userService.getOrInit(person.getCreatorUUID()));
        ety.setDateCreated(person.getDateCreated());
        ety.setChangedBy(person.getChangedByUUID() == null ? null : userService.getOrInit(person.getChangedByUUID()));
        ety.setDateChanged(person.getDateChanged());
        ety.setVoided(person.getVoided());
        ety.setVoidedBy(person.getVoidedByUUID() == null ? null : userService.getOrInit(person.getVoidedByUUID()));
        ety.setDateVoided(person.getDateVoided());
        ety.setVoidReason(person.getVoidReason());
        ety.setUuid(person.getUuid());
        ety.setDeathdateEstimated(person.getDeathdateEstimated());
        ety.setBirthtime(person.getBirthtime());

        return ety;
    }
}
