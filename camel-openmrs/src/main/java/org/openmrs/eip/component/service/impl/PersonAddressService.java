package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.PersonAddress;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.PersonAddressModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PersonAddressService extends AbstractEntityService<PersonAddress, PersonAddressModel> {

    public PersonAddressService(final SyncEntityRepository<PersonAddress> repository,
                                final EntityToModelMapper<PersonAddress, PersonAddressModel> entityToModelMapper,
                                final ModelToEntityMapper<PersonAddressModel, PersonAddress> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.PERSON_ADDRESS;
    }
}
