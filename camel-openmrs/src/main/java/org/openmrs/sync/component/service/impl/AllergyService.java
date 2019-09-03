package org.openmrs.sync.component.service.impl;

import org.openmrs.sync.component.entity.Allergy;
import org.openmrs.sync.component.mapper.EntityToModelMapper;
import org.openmrs.sync.component.mapper.ModelToEntityMapper;
import org.openmrs.sync.component.model.AllergyModel;
import org.openmrs.sync.component.repository.SyncEntityRepository;
import org.openmrs.sync.component.service.AbstractEntityService;
import org.openmrs.sync.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class AllergyService extends AbstractEntityService<Allergy, AllergyModel> {

    public AllergyService(final SyncEntityRepository<Allergy> repository,
                          final EntityToModelMapper<Allergy, AllergyModel> entityToModelMapper,
                          final ModelToEntityMapper<AllergyModel, Allergy> modelToEntityMapper) {
        super(repository, entityToModelMapper, modelToEntityMapper);
    }

    @Override
    public TableToSyncEnum getTableToSync() {
        return TableToSyncEnum.ALLERGY;
    }
}
