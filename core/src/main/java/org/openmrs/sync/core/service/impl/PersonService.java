package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.camel.EntityNameEnum;
import org.openmrs.sync.core.entity.PersonEty;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PersonService extends AbstractEntityService<PersonEty, PersonModel> {

    private OpenMrsRepository<PersonEty> personRepository;
    private Function<PersonEty, PersonModel> personEtyToModelMapper;
    private Function<PersonModel, PersonEty> personModelToEtyMapper;

    public PersonService(final OpenMrsRepository<PersonEty> personRepository,
                         final Function<PersonEty, PersonModel> personEtyToModelMapper,
                         final Function<PersonModel, PersonEty> personModelToEtyMapper) {
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
    protected Function<PersonEty, PersonModel> getEntityToModelMapper() {
        return personEtyToModelMapper;
    }

    @Override
    protected Function<PersonModel, PersonEty> getModelToEntityMapper() {
        return personModelToEtyMapper;
    }
}
