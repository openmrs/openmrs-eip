package org.openmrs.sync.core.mapper;

import org.openmrs.sync.core.entity.light.*;

public abstract class AbstractMapperTest {

    protected UserLight getUser() {
        UserLight user = new UserLight();
        user.setUuid("user");
        return user;
    }

    protected DrugLight getValueDrug() {
        DrugLight drug = new DrugLight();
        drug.setUuid("drug");
        return drug;
    }

    protected EncounterTypeLight getEncounterType() {
        EncounterTypeLight encounterType = new EncounterTypeLight();
        encounterType.setUuid("encounterType");
        return encounterType;
    }

    protected VisitLight getVisit() {
        VisitLight visit = new VisitLight();
        visit.setUuid("visit");
        VisitTypeLight visitType = new VisitTypeLight();
        visitType.setUuid("visitVisitType");
        visit.setVisitType(visitType);
        visit.setPatient(getPatient("visitPatient"));
        return visit;
    }


    protected LocationLight getLocation() {
        LocationLight location = new LocationLight();
        location.setUuid("location");
        return location;
    }

    protected PatientLight getPatient(final String uuid) {
        PatientLight patient = new PatientLight();
        patient.setUuid(uuid);
        return patient;
    }

    protected FormLight getForm() {
        FormLight form = new FormLight();
        form.setUuid("form");
        return form;
    }

    protected VisitTypeLight getVisitType() {
        VisitTypeLight visitType = new VisitTypeLight();
        visitType.setUuid("visitType");
        return visitType;
    }

    protected ConceptLight getConcept() {
        ConceptLight concept = new ConceptLight();
        concept.setUuid("concept");
        return concept;
    }

    protected ConceptLight getConcept(final String uuid) {
        ConceptLight concept = new ConceptLight();
        concept.setUuid(uuid);
        return concept;
    }

    protected PatientLight getPatient() {
        PatientLight patient = new PatientLight();
        patient.setUuid("patient");
        return patient;
    }

    protected ObservationLight getObsGroup() {
        ObservationLight observation = new ObservationLight();
        observation.setUuid("observation");
        return observation;
    }

    protected OrderLight getOrder() {
        OrderLight order = new OrderLight();
        order.setUuid("order");
        return order;
    }

    protected ConceptLight getValueCoded() {
        ConceptLight valueCode = new ConceptLight();
        valueCode.setUuid("valueCoded");
        return valueCode;
    }

    protected ConceptNameLight getValueCodeName() {
        ConceptNameLight conceptName = new ConceptNameLight();
        conceptName.setUuid("conceptName");
        return conceptName;
    }

    protected ObservationLight getObservation(final String uuid) {
        ObservationLight observation = new ObservationLight();
        observation.setUuid(uuid);
        return observation;
    }

    protected PersonLight getPerson() {
        PersonLight person = new PersonLight();
        person.setUuid("person");
        return person;
    }

    protected EncounterLight getEncounter() {
        EncounterLight encounter = new EncounterLight();
        encounter.setUuid("encounter");
        return encounter;
    }
}
