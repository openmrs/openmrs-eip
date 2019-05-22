package org.openmrs.sync.core.mapper.entityToModel;

import org.openmrs.sync.core.entity.PersonEty;
import org.openmrs.sync.core.model.PersonModel;
import org.springframework.stereotype.Component;

@Component
public class PersonEtyToModelMapper implements java.util.function.Function<PersonEty, PersonModel> {

    @Override
    public PersonModel apply(PersonEty ety) {
        PersonModel model = new PersonModel();
        model.setGender(ety.getGender());
        model.setBirthdate(ety.getBirthdate());
        model.setBirthdateEstimated(ety.isBirthdateEstimated());
        model.setDead(ety.isDead());
        model.setDeathDate(ety.getDeathDate());
        model.setCauseOfDeathUUID(ety.getCauseOfDeath() == null ? null : ety.getCauseOfDeath().getUuid());
        model.setCreatorUUID(ety.getCreator() == null ? null : ety.getCreator().getUuid());
        model.setDateCreated(ety.getDateCreated());
        model.setChangedByUUID(ety.getChangedBy() == null ? null : ety.getChangedBy().getUuid());
        model.setDateChanged(ety.getDateChanged());
        model.setVoided(ety.isVoided());
        model.setVoidedByUUID(ety.getVoidedBy() == null ? null : ety.getVoidedBy().getUuid());
        model.setDateVoided(ety.getDateVoided());
        model.setVoidReason(ety.getVoidReason());
        model.setUuid(ety.getUuid());
        model.setDeathdateEstimated(ety.isDeathdateEstimated());
        model.setBirthtime(ety.getBirthtime());
        return model;
    }
}
