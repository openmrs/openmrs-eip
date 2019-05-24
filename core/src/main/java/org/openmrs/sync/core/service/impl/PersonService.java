package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.camel.TableNameEnum;
import org.openmrs.sync.core.entity.PersonEty;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class PersonService extends AbstractEntityService<PersonEty, PersonModel> {

    public PersonService(final OpenMrsRepository<PersonEty> personRepository,
                         final Function<PersonEty, PersonModel> personEtyToModelMapper,
                         final Function<PersonModel, PersonEty> personModelToEtyMapper) {
        super(personRepository, personEtyToModelMapper, personModelToEtyMapper);
    }

    @Override
    public TableNameEnum getEntityName() {
        return TableNameEnum.PERSON;
    }
}
