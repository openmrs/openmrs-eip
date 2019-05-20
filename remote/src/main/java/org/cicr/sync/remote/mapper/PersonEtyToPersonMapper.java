package org.cicr.sync.remote.mapper;

import org.cicr.sync.core.entity.PersonEty;
import org.cicr.sync.core.model.Person;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PersonEtyToPersonMapper implements Function<PersonEty, Person> {

    @Override
    public Person apply(PersonEty ety) {
        return new Person(
                ety.getPersonId(),
                ety.getGender(),
                ety.getBirthdate(),
                ety.getBirthdateEstimated(),
                ety.getDead(),
                ety.getDeathDate(),
                ety.getCauseOfDeath() == null ? null : ety.getCauseOfDeath().getUuid(),
                ety.getCreator() == null ? null : ety.getCreator().getUuid(),
                ety.getDateCreated(),
                ety.getChangedBy() == null ? null : ety.getChangedBy().getUuid(),
                ety.getDateChanged(),
                ety.getVoided(),
                ety.getVoidedBy() == null ? null : ety.getVoidedBy().getUuid(),
                ety.getDateVoided(),
                ety.getVoidReason(),
                ety.getUuid(),
                ety.getDeathdateEstimated(),
                ety.getBirthtime()
        );
    }
}
