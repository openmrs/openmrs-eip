package org.openmrs.sync.core.service.light.impl;

import org.openmrs.sync.core.entity.light.FormLight;
import org.openmrs.sync.core.repository.OpenMrsRepository;
import org.openmrs.sync.core.service.light.AbstractLightServiceNoContext;
import org.springframework.stereotype.Service;

@Service
public class FormLightService extends AbstractLightServiceNoContext<FormLight> {

    public FormLightService(final OpenMrsRepository<FormLight> repository) {
        super(repository);
    }

    @Override
    protected FormLight getShadowEntity(final String uuid) {
        FormLight form = new FormLight();
        form.setUuid(uuid);
        form.setName(DEFAULT_STRING);
        form.setVersion(DEFAULT_STRING);
        form.setCreator(DEFAULT_USER_ID);
        form.setDateCreated(DEFAULT_DATE);
        return form;
    }
}
