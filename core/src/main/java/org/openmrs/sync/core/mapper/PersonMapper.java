package org.openmrs.sync.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.openmrs.sync.core.entity.Patient;
import org.openmrs.sync.core.entity.light.ConceptLight;
import org.openmrs.sync.core.entity.Person;
import org.openmrs.sync.core.entity.light.UserLight;
import org.openmrs.sync.core.model.PatientModel;
import org.openmrs.sync.core.model.PersonModel;
import org.openmrs.sync.core.service.SimpleService;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PersonMapper implements EntityMapper<Person, PersonModel> {

    @Autowired
    protected SimpleService<ConceptLight> conceptService;

    @Autowired
    protected SimpleService<UserLight> userService;

    @Autowired
    protected PatientMapper patientMapper;

    @Override
    public PersonModel entityToModel(final Person entity) {
        if (entity instanceof Patient) {
            return patientMapper.entityToModel((Patient) entity);
        } else {
            return personToModel(entity);
        }
    }

    @Override
    public Person modelToEntity(final PersonModel model) {
        if (model instanceof PatientModel) {
            return patientMapper.modelToEntity((PatientModel) model);
        } else {
            return modelToPerson(model);
        }
    }

    @Mappings({
            @Mapping(source = "causeOfDeath.uuid", target = "causeOfDeathUuid"),
            @Mapping(source = "personCreator.uuid", target = "personCreatorUuid"),
            @Mapping(source = "personChangedBy.uuid", target = "personChangedByUuid"),
            @Mapping(source = "personVoidedBy.uuid", target = "personVoidedByUuid")
    })
    public abstract PersonModel personToModel(Person entity);

    @Mappings({
            @Mapping(expression = "java(conceptService.getOrInit(model.getCauseOfDeathUuid()))", target ="causeOfDeath"),
            @Mapping(expression = "java(userService.getOrInit(model.getPersonCreatorUuid()))", target ="personCreator"),
            @Mapping(expression = "java(userService.getOrInit(model.getPersonChangedByUuid()))", target ="personChangedBy"),
            @Mapping(expression = "java(userService.getOrInit(model.getPersonVoidedByUuid()))", target ="personVoidedBy"),
            @Mapping(ignore = true, target = "id")
    })
    public abstract Person modelToPerson(PersonModel model);
}
