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
    protected EntityMapper<Patient, PatientModel> patientMapper;

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
            @Mapping(source = "creator.uuid", target = "creatorUuid"),
            @Mapping(source = "changedBy.uuid", target = "changedByUuid"),
            @Mapping(source = "voidedBy.uuid", target = "voidedByUuid")
    })
    protected abstract PersonModel personToModel(Person entity);

    @Mappings({
            @Mapping(expression = "java(conceptService.getOrInit(model.getCauseOfDeathUuid()))", target ="causeOfDeath"),
            @Mapping(expression = "java(userService.getOrInit(model.getCreatorUuid()))", target ="creator"),
            @Mapping(expression = "java(userService.getOrInit(model.getChangedByUuid()))", target ="changedBy"),
            @Mapping(expression = "java(userService.getOrInit(model.getVoidedByUuid()))", target ="voidedBy"),
            @Mapping(ignore = true, target = "id")
    })
    protected abstract Person modelToPerson(PersonModel model);
}
