package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.PersonAttribute;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.PersonAttributeModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class PersonAttributeService extends AbstractEntityService<PersonAttribute, PersonAttributeModel> {
	
	public PersonAttributeService(final SyncEntityRepository<PersonAttribute> repository,
	    final EntityToModelMapper<PersonAttribute, PersonAttributeModel> entityToModelMapper,
	    final ModelToEntityMapper<PersonAttributeModel, PersonAttribute> modelToEntityMapper) {
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.PERSON_ATTRIBUTE;
	}
}
