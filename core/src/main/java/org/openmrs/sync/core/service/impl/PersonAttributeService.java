package org.openmrs.sync.core.service.impl;

import org.openmrs.sync.core.entity.PersonAttribute;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.PersonAttributeModel;
import org.openmrs.sync.core.repository.SyncEntityRepository;
import org.openmrs.sync.core.service.AbstractEntityService;
import org.openmrs.sync.core.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PersonAttributeService extends AbstractEntityService<PersonAttribute, PersonAttributeModel> {

    public PersonAttributeService(final SyncEntityRepository<PersonAttribute> repository,
                                  final EntityMapper<PersonAttribute, PersonAttributeModel> mapper) {
        super(repository, mapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PERSON_ATTRIBUTE;
    }
}
