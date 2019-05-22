package org.cicr.sync.core.service.impl;

import org.cicr.sync.core.camel.EntityNameEnum;
import org.cicr.sync.core.entity.PersonEty;
import org.cicr.sync.core.mapper.entityToModel.impl.PersonEtyToModelMapper;
import org.cicr.sync.core.mapper.modelToEntity.impl.PersonModelToEtyMapper;
import org.cicr.sync.core.model.PersonModel;
import org.cicr.sync.core.repository.OpenMrsRepository;
import org.cicr.sync.core.service.AbstractEntityService;
import org.springframework.stereotype.Service;

@Service
public class PersonService extends AbstractEntityService<PersonEty, PersonModel> {

    private OpenMrsRepository<PersonEty> personRepository;
    private PersonEtyToModelMapper personEtyToModelMapper;
    private PersonModelToEtyMapper personModelToEtyMapper;

    public PersonService(final OpenMrsRepository<PersonEty> personRepository,
                         final PersonEtyToModelMapper personEtyToModelMapper,
                         final PersonModelToEtyMapper personModelToEtyMapper) {
        this.personRepository = personRepository;
        this.personEtyToModelMapper = personEtyToModelMapper;
        this.personModelToEtyMapper = personModelToEtyMapper;
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
    protected PersonEtyToModelMapper getEntityToModelMapper() {
        return personEtyToModelMapper;
    }

    @Override
    protected PersonModelToEtyMapper getModelToEntityMapper() {
        return personModelToEtyMapper;
    }
}
