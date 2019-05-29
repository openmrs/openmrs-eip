package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.repository.AuditableRepository;
import org.openmrs.sync.core.service.TableNameEnum;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.springframework.stereotype.Service;

@Service
public class PersonService extends AbstractEntityService<Person, PersonModel> {

    public PersonService(final AuditableRepository<Person> personRepository,
                         final EntityMapper<Person, PersonModel> mapper) {
        super(personRepository, mapper);
    }

    @Override
    public TableNameEnum getTableName() {
        return TableNameEnum.PERSON;
    }
}
