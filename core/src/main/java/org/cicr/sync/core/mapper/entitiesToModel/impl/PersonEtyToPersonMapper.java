package org.cicr.sync.core.mapper.entitiesToModel.impl;

import org.cicr.sync.core.entity.PersonEty;
import org.cicr.sync.core.mapper.entitiesToModel.EntityToModelMapper;
import org.cicr.sync.core.model.PersonModel;
import org.springframework.stereotype.Component;

@Component
public class PersonEtyToPersonMapper implements EntityToModelMapper<PersonEty, PersonModel> {

    @Override
    public PersonModel apply(PersonEty ety) {
        return new PersonModel(
                ety.getId(),
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
