package org.cicr.sync.core.service.impl;

import org.cicr.sync.core.camel.EntityNameEnum;
import org.cicr.sync.core.entity.PersonEty;
import org.cicr.sync.core.mapper.entitiesToModel.impl.PersonEtyToPersonMapper;
import org.cicr.sync.core.model.PersonModel;
import org.cicr.sync.core.repository.OpenMrsRepository;
import org.cicr.sync.core.service.LoadEntityService;
import org.springframework.stereotype.Service;

@Service
public class LoadPersonService extends LoadEntityService<PersonEty, PersonModel> {

    private OpenMrsRepository<PersonEty> personRepository;
    private PersonEtyToPersonMapper personEtyToPersonMapper;

    public LoadPersonService(final OpenMrsRepository<PersonEty> personRepository,
                             final PersonEtyToPersonMapper personEtyToPersonMapper) {
        this.personRepository = personRepository;
        this.personEtyToPersonMapper = personEtyToPersonMapper;
    }

    @Override
    public EntityNameEnum getEntityName() {
        return EntityNameEnum.PERSON;
    }

    @Override
    protected OpenMrsRepository<PersonEty> getRepository() {
        return personRepository;
    }

    @Override
    protected PersonEtyToPersonMapper getEntityToModelMapper() {
        return personEtyToPersonMapper;
    }
}
