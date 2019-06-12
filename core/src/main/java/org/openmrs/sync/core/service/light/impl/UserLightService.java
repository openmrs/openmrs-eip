package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightService;
import org.openmrs.sync.core.service.attribute.AttributeUuid;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserLightService extends AbstractLightService<UserLight> {

    public UserLightService(final OpenMrsRepository<UserLight> userRepository) {
        super(userRepository);
    }

    @Override
    protected UserLight getFakeEntity(final String uuid,
                                      final List<AttributeUuid> attributeUuids) {
        UserLight user = new UserLight();
        user.setUuid(uuid);
        user.setCreator(DEFAULT_USER_ID);
        user.setDateCreated(DEFAULT_DATE);
        user.setSystemId("admin");
        user.setPersonId(1L);
        return user;
    }
}
