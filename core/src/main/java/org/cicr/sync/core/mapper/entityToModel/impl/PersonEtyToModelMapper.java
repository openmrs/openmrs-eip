package org.cicr.sync.core.mapper.entityToModel.impl;

import org.cicr.sync.core.entity.PersonEty;
import org.cicr.sync.core.mapper.entityToModel.EntityToModelMapper;
import org.cicr.sync.core.model.PersonModel;
import org.springframework.stereotype.Component;

@Component
public class PersonEtyToModelMapper implements EntityToModelMapper<PersonEty, PersonModel> {

    @Override
    public PersonModel apply(PersonEty ety) {
        PersonModel model = new PersonModel();
        model.setGender(ety.getGender());
        model.setBirthdate(ety.getBirthdate());
        model.setBirthdateEstimated(ety.getBirthdateEstimated());
        model.setDead(ety.getDead());
        model.setDeathDate(ety.getDeathDate());
        model.setCauseOfDeathUUID(ety.getCauseOfDeath() == null ? null : ety.getCauseOfDeath().getUuid());
        model.setCreatorUUID(ety.getCreator() == null ? null : ety.getCreator().getUuid());
        model.setDateCreated(ety.getDateCreated());
        model.setChangedByUUID(ety.getChangedBy() == null ? null : ety.getChangedBy().getUuid());
        model.setDateChanged(ety.getDateChanged());
        model.setVoided(ety.getVoided());
        model.setVoidedByUUID(ety.getVoidedBy() == null ? null : ety.getVoidedBy().getUuid());
        model.setDateVoided(ety.getDateVoided());
        model.setVoidReason(ety.getVoidReason());
        model.setUuid(ety.getUuid());
        model.setDeathdateEstimated(ety.getDeathdateEstimated());
        model.setBirthtime(ety.getBirthtime());
        return model;
    }
}
