package org.cicr.camel.remote.mapper;

import org.cicr.camel.remote.entity.PersonEty;
import org.cicr.camel.remote.model.Person;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class PersonEtyToPersonMapper implements Function<PersonEty, Person> {

    @Override
    public Person apply(PersonEty ety) {
        return Person.builder()
                .personId(ety.getPersonId())
                .gender(ety.getGender())
                .birthdate(ety.getBirthdate())
                .birthdateEstimated(ety.getBirthdateEstimated())
                .dead(ety.getDead())
                .deathDate(ety.getDeathDate())
                .causeOfDeathUUID(ety.getCauseOfDeath() == null ? null : ety.getCauseOfDeath().getUuid())
                .creatorUUID(ety.getCreator() == null ? null : ety.getCreator().getUuid())
                .dateCreated(ety.getDateCreated())
                .changedByUUID(ety.getChangedBy() == null ? null : ety.getChangedBy().getUuid())
                .dateChanged(ety.getDateChanged())
                .voided(ety.getVoided())
                .voidedByUUID(ety.getVoidedBy() == null ? null : ety.getVoidedBy().getUuid())
                .dateVoided(ety.getDateVoided())
                .voidReason(ety.getVoidReason())
                .uuid(ety.getUuid())
                .deathdateEstimated(ety.getDeathdateEstimated())
                .birthtime(ety.getBirthtime())
                .build();
    }
}
