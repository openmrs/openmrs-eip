package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Patient;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.EntityNameEnum;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService extends AbstractEntityService<Person, PersonModel> {

    public PersonService(final SyncEntityRepository<Person> personRepository,
                         final EntityMapper<Person, PersonModel> mapper) {
        super(personRepository, mapper);
    }

    @Override
    public EntityNameEnum getEntityName() {
        return EntityNameEnum.PERSON;
    }

    @Override
    protected List<PersonModel> mapEntities(final List<Person> entities) {
        return entities.stream()
                .filter(person -> !(person instanceof Patient))
                .map(mapper::entityToModel)
                .collect(Collectors.toList());
    }
}
