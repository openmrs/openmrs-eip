package org.openmrs.sync.core.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.sync.core.entity.Observation;
import org.openmrs.sync.core.entity.light.*;
import org.openmrs.sync.core.model.ObservationModel;
import org.openmrs.sync.core.service.light.LightService;
import org.openmrs.sync.core.service.light.LightServiceNoContext;
import org.openmrs.sync.core.service.light.impl.context.*;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class ObservationMapperTest extends AbstractMapperTest {

    @Mock
    protected LightServiceNoContext<PersonLight> personService;

    @Mock
    protected LightService<ConceptLight, ConceptContext> conceptService;

    @Mock
    protected LightService<EncounterLight, EncounterContext> encounterService;

    @Mock
    protected LightService<OrderLight, OrderContext> orderService;

    @Mock
    protected LightServiceNoContext<LocationLight> locationService;

    @Mock
    protected LightService<ObservationLight, ObservationContext> observationService;

    @Mock
    protected LightServiceNoContext<ConceptNameLight> conceptNameService;

    @Mock
    protected LightService<DrugLight, DrugContext> drugService;

    @Mock
    protected LightServiceNoContext<UserLight> userService;

    @InjectMocks
    private ObservationMapperImpl mapper;

    private PersonLight person = initBaseModel(PersonLight.class, "person");
    private ConceptLight concept = initBaseModel(ConceptLight.class, "concept");
    private ConceptLight valueCoded = initBaseModel(ConceptLight.class, "valueCoded");
    private EncounterLight encounter = initBaseModel(EncounterLight.class, "encounter");
    private OrderLight order = initBaseModel(OrderLight.class, "order");
    private LocationLight location = initBaseModel(LocationLight.class, "location");
    private ObservationLight obsGroup = initBaseModel(ObservationLight.class, "obsGroup");
    private ObservationLight previousVersion = initBaseModel(ObservationLight.class, "previousVersion");
    private ConceptNameLight valueCodeName = initBaseModel(ConceptNameLight.class, "valueCodeName");
    private DrugLight drug = initBaseModel(DrugLight.class, "drug");

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void modelToEntity() {
        // Given
        Observation ety = getObservationEty();

        // When
        ObservationModel result = mapper.entityToModel(ety);

        // Then
        assertResult(ety, result);
    }

    @Test
    public void etyToModel() {
        // Given
        ObservationModel model = getObservationModel();
        when(personService.getOrInit("person")).thenReturn(person);
        when(conceptService.getOrInit("concept", getConceptContext())).thenReturn(concept);
        when(conceptService.getOrInit("valueCoded", getConceptContext())).thenReturn(valueCoded);
        when(encounterService.getOrInit("encounter", getEncounterContext())).thenReturn(encounter);
        when(orderService.getOrInit("order", getOrderContext())).thenReturn(order);
        when(locationService.getOrInit("location")).thenReturn(location);
        when(observationService.getOrInit("obsGroup", getObservationContext())).thenReturn(obsGroup);
        when(observationService.getOrInit("previousVersion", getObservationContext())).thenReturn(previousVersion);
        when(conceptNameService.getOrInit("valueCodeName")).thenReturn(valueCodeName);
        when(drugService.getOrInit("drug", getDrugContext())).thenReturn(drug);
        when(userService.getOrInit("user")).thenReturn(user);

        // When
        Observation result = mapper.modelToEntity(model);

        // Then
        assertResult(model, result);
    }

    private void assertResult(final Observation ety, final ObservationModel result) {
        assertEquals(ety.getUuid(), result.getUuid());
        assertEquals(ety.getObsDatetime(), result.getObsDatetime());
        assertEquals(ety.getAccessionNumber(), result.getAccessionNumber());
        assertEquals(ety.getValueGroupId(), result.getValueGroupId());
        assertEquals(ety.getValueDatetime(), result.getValueDatetime());
        assertEquals(ety.getValueNumeric(), result.getValueNumeric());
        assertEquals(ety.getValueModifier(), result.getValueModifier());
        assertEquals(ety.getValueText(), result.getValueText());
        assertEquals(ety.getValueComplex(), result.getValueComplex());
        assertEquals(ety.getComments(), result.getComments());
        assertEquals(ety.getFormNamespaceAndPath(), result.getFormNamespaceAndPath());
        assertEquals(ety.getStatus(), result.getStatus());
        assertEquals(ety.getInterpretation(), result.getInterpretation());
        assertEquals(ety.getDateCreated(), result.getDateCreated());
        assertEquals(ety.isVoided(), result.isVoided());
        assertEquals(ety.getDateVoided(), result.getDateVoided());
        assertEquals(ety.getVoidReason(), result.getVoidReason());
        assertEquals(ety.getCreator().getUuid(), result.getCreatorUuid());
        assertEquals(ety.getValueDrug().getUuid(), result.getValueDrugUuid());
        assertEquals(ety.getConcept().getUuid(), result.getConceptUuid());
        assertEquals(ety.getEncounter().getUuid(), result.getEncounterUuid());
        assertEquals(ety.getVoidedBy().getUuid(), result.getVoidedByUuid());
        assertEquals(ety.getPerson().getUuid(), result.getPersonUuid());
        assertEquals(ety.getPreviousVersion().getUuid(), result.getPreviousVersionUuid());
        assertEquals(ety.getLocation().getUuid(), result.getLocationUuid());
        assertEquals(ety.getValueCodedName().getUuid(), result.getValueCodedNameUuid());
        assertEquals(ety.getValueCoded().getUuid(), result.getValueCodedUuid());
        assertEquals(ety.getOrder().getUuid(), result.getOrderUuid());
        assertEquals(ety.getObsGroup().getUuid(), result.getObsGroupUuid());
    }

    private Observation getObservationEty() {
        Observation observation = new Observation();
        observation.setUuid("uuid");
        observation.setObsDatetime(LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0));
        observation.setAccessionNumber("accessionNumber");
        observation.setValueGroupId(1L);
        observation.setValueDatetime(LocalDateTime.of(2011, Month.JANUARY, 1, 0, 0));
        observation.setValueNumeric(1.0);
        observation.setValueModifier(1);
        observation.setValueText("valueText");
        observation.setValueComplex("valueComplex");
        observation.setComments("comments");
        observation.setFormNamespaceAndPath("formNamespaceAndPath");
        observation.setStatus("status");
        observation.setInterpretation("interpretation");
        observation.setDateCreated(LocalDateTime.of(2012, Month.JANUARY, 1, 0, 0));
        observation.setVoided(true);
        observation.setDateVoided(LocalDateTime.of(2013, Month.JANUARY, 1, 0, 0));
        observation.setVoidReason("voidReason");
        observation.setCreator(user);
        observation.setValueDrug(drug);
        observation.setConcept(concept);
        observation.setEncounter(encounter);
        observation.setVoidedBy(user);
        observation.setPerson(person);
        observation.setPreviousVersion(previousVersion);
        observation.setLocation(location);
        observation.setValueCodedName(valueCodeName);
        observation.setValueCoded(valueCoded);
        observation.setOrder(order);
        observation.setObsGroup(obsGroup);
        return observation;
    }

    private void assertResult(final ObservationModel model, final Observation result) {
        assertEquals(model.getOrderUuid(), result.getOrder().getUuid());
        assertEquals(model.getPersonUuid(), result.getPerson().getUuid());
        assertEquals(model.getConceptUuid(), result.getConcept().getUuid());
        assertEquals(model.getValueCodedUuid(), result.getValueCoded().getUuid());
        assertEquals(model.getLocationUuid(), result.getLocation().getUuid());
        assertEquals(model.getVoidedByUuid(), result.getVoidedBy().getUuid());
        assertEquals(model.getValueDrugUuid(), result.getValueDrug().getUuid());
        assertEquals(model.getObsGroupUuid(), result.getObsGroup().getUuid());
        assertEquals(model.getCreatorUuid(), result.getCreator().getUuid());
        assertEquals(model.getPreviousVersionUuid(), result.getPreviousVersion().getUuid());
        assertEquals(model.getValueCodedNameUuid(), result.getValueCodedName().getUuid());
        assertEquals(model.getEncounterUuid(), result.getEncounter().getUuid());
        assertEquals(model.getUuid(), result.getUuid());
        assertEquals(model.getDateCreated(), result.getDateCreated());
        assertEquals(model.isVoided(), result.isVoided());
        assertEquals(model.getDateVoided(), result.getDateVoided());
        assertEquals(model.getVoidReason(), result.getVoidReason());
        assertEquals(model.getObsDatetime(), result.getObsDatetime());
        assertEquals(model.getAccessionNumber(), result.getAccessionNumber());
        assertEquals(model.getValueGroupId(), result.getValueGroupId());
        assertEquals(model.getValueDatetime(), result.getValueDatetime());
        assertEquals(model.getValueNumeric(), result.getValueNumeric());
        assertEquals(model.getValueModifier(), result.getValueModifier());
        assertEquals(model.getValueText(), result.getValueText());
        assertEquals(model.getValueComplex(), result.getValueComplex());
        assertEquals(model.getComments(), result.getComments());
        assertEquals(model.getFormNamespaceAndPath(), result.getFormNamespaceAndPath());
        assertEquals(model.getStatus(), result.getStatus());
        assertEquals(model.getInterpretation(), result.getInterpretation());
    }

    private ObservationModel getObservationModel() {
        ObservationModel model = new ObservationModel();
        model.setOrderUuid("order");
        model.setObsGroupConceptUuid("concept");
        model.setPersonUuid("person");
        model.setConceptClassUuid("conceptClass");
        model.setOrderOrderTypeUuid("orderType");
        model.setOrderConceptDatatypeUuid("conceptDatatype");
        model.setConceptUuid("concept");
        model.setConceptDatatypeUuid("conceptDatatype");
        model.setOrderConceptUuid("concept");
        model.setPreviousVersionConceptClassUuid("conceptClass");
        model.setPreviousVersionConceptDatatypeUuid("conceptDatatype");
        model.setOrderConceptClassUuid("conceptClass");
        model.setValueCodedUuid("valueCoded");
        model.setOrderPatientUuid("patient");
        model.setLocationUuid("location");
        model.setVoidedByUuid("user");
        model.setOrderEncounterPatientUuid("patient");
        model.setValueDrugUuid("drug");
        model.setObsGroupConceptDatatypeUuid("conceptDatatype");
        model.setObsGroupConceptClassUuid("conceptClass");
        model.setOrderOrdererUuid("orderer");
        model.setOrderCareSettingUuid("careSetting");
        model.setObsGroupUuid("obsGroup");
        model.setValueDrugConceptUuid("concept");
        model.setCreatorUuid("user");
        model.setValueCodedDatatypeUuid("conceptDatatype");
        model.setOrderEncounterUuid("encounter");
        model.setOrderEncounterTypeUuid("encounterType");
        model.setPreviousVersionUuid("previousVersion");
        model.setPreviousVersionPersonUuid("person");
        model.setObsGroupPersonUuid("person");
        model.setValueDrugConceptClassUuid("conceptClass");
        model.setEncounterEncounterTypeUuid("encounterType");
        model.setValueDrugConceptDatatypeUuid("conceptDatatype");
        model.setEncounterPatientUuid("patient");
        model.setValueCodedClassUuid("conceptClass");
        model.setValueCodedNameUuid("valueCodeName");
        model.setPreviousVersionConceptUuid("concept");
        model.setEncounterUuid("encounter");
        model.setUuid("uuid");
        model.setDateCreated(LocalDateTime.of(2012, Month.JANUARY, 1, 0, 0));
        model.setVoided(true);
        model.setDateVoided(LocalDateTime.of(2013, Month.JANUARY, 1, 0, 0));
        model.setVoidReason("voidReason");
        model.setObsDatetime(LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0));
        model.setAccessionNumber("accessionNumber");
        model.setValueGroupId(1L);
        model.setValueDatetime(LocalDateTime.of(2010, Month.JANUARY, 1, 0, 0));
        model.setValueNumeric(1.0);
        model.setValueModifier(1);
        model.setValueText("value");
        model.setValueComplex("valueComplex");
        model.setComments("comments");
        model.setFormNamespaceAndPath("formNamespaceAndPath");
        model.setStatus("status");
        model.setInterpretation("interpretation");

        return model;
    }

    private DrugContext getDrugContext() {
        return DrugContext.builder()
                .conceptClassUuid("conceptClass")
                .conceptDatatypeUuid("conceptDatatype")
                .conceptUuid("concept")
                .build();
    }

    private ObservationContext getObservationContext() {
        return ObservationContext.builder()
                .personUuid("person")
                .conceptDatatypeUuid("conceptDatatype")
                .conceptClassUuid("conceptClass")
                .conceptUuid("concept")
                .build();
    }

    private OrderContext getOrderContext() {
        return OrderContext.builder()
                .orderTypeUuid("orderType")
                .careSettingUuid("careSetting")
                .patientUuid("patient")
                .encounterEncounterTypeUuid("encounterType")
                .encounterPatientUuid("patient")
                .encounterUuid("encounter")
                .providerUuid("orderer")
                .conceptDatatypeUuid("conceptDatatype")
                .conceptClassUuid("conceptClass")
                .conceptUuid("concept")
                .build();
    }

    private EncounterContext getEncounterContext() {
        return EncounterContext.builder()
                .patientUuid("patient")
                .encounterTypeUuid("encounterType")
                .build();
    }

    private ConceptContext getConceptContext() {
        return ConceptContext.builder()
                .conceptDatatypeUuid("conceptDatatype")
                .conceptClassUuid("conceptClass")
                .build();
    }
}
