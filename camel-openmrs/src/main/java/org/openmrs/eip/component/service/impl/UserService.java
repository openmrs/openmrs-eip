package org.openmrs.eip.component.service.impl;

import org.openmrs.eip.component.entity.User;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;
import org.openmrs.eip.component.model.UserModel;
import org.openmrs.eip.component.repository.SyncEntityRepository;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.springframework.stereotype.Service;

@Service
public class UserService extends AbstractEntityService<User, UserModel> {
	
	public UserService(final SyncEntityRepository<User> repository,
	    final EntityToModelMapper<User, UserModel> entityToModelMapper,
	    final ModelToEntityMapper<UserModel, User> modelToEntityMapper) {
		super(repository, entityToModelMapper, modelToEntityMapper);
	}
	
	@Override
	public TableToSyncEnum getTableToSync() {
		return TableToSyncEnum.USERS;
	}
}
