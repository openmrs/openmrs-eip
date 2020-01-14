package org.openmrs.sync.component.service.light.impl;

import org.openmrs.sync.component.entity.light.FormLight;
import org.openmrs.sync.component.repository.OpenmrsRepository;
import org.openmrs.sync.component.service.light.AbstractLightService;
import org.springframework.stereotype.Service;

@Service
public class FormLightService extends AbstractLightService<FormLight> {

    public FormLightService(final OpenmrsRepository<FormLight> repository) {
        super(repository);
    }

    @Override
    protected FormLight createPlaceholderEntity(final String uuid) {
        FormLight form = new FormLight();
        form.setName(DEFAULT_STRING);
        form.setVersion(DEFAULT_STRING);
        form.setCreator(DEFAULT_USER_ID);
        form.setDateCreated(DEFAULT_DATE);
        return form;
    }
}
