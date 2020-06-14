package org.openmrs.eip.component.service.facade;

import org.openmrs.eip.component.entity.Patient;
import org.openmrs.eip.component.entity.Person;
import org.openmrs.eip.component.model.BaseModel;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.service.AbstractEntityService;
import org.openmrs.eip.component.service.TableToSyncEnum;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class EntityServiceFacadeTest {

    @Mock
    private AbstractEntityService<Person, PersonModel> personService;

    @Mock
    private AbstractEntityService<Patient, PatientModel> patientService;

    private EntityServiceFacade facade;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        facade = new EntityServiceFacade(Arrays.asList(personService, patientService));
    }

    @Test
    public void getAllModels_should_return_all_models() {
        // Given
        when(personService.getTableToSync()).thenReturn(TableToSyncEnum.PERSON);

        // When
        facade.getAllModels(TableToSyncEnum.PERSON);

        // Then
        verify(personService).getAllModels();
    }

    @Test
    public void getModel_by_uuid_should_return_model() {
        // Given
        when(personService.getTableToSync()).thenReturn(TableToSyncEnum.PERSON);

        // When
        facade.getModel(TableToSyncEnum.PERSON, "uuid");

        // Then
        verify(personService).getModel("uuid");
    }

    @Test
    public void getModel_by_id_should_return_model() {
        // Given
        when(personService.getTableToSync()).thenReturn(TableToSyncEnum.PERSON);

        // When
        facade.getModel(TableToSyncEnum.PERSON, 1L);

        // Then
        verify(personService).getModel(1L);
    }

    @Test
    public void getModelsAfterDate_should_return_models() {
        // Given
        PersonModel personModel1 = new PersonModel();
        PersonModel personModel2 = new PersonModel();
        LocalDateTime lastSyncDate = LocalDateTime.now();
        when(personService.getTableToSync()).thenReturn(TableToSyncEnum.PERSON);
        when(patientService.getTableToSync()).thenReturn(TableToSyncEnum.VISIT);
        when(personService.getModels(lastSyncDate)).thenReturn(Arrays.asList(personModel1, personModel2));

        // When
        List<? extends BaseModel> result = facade.getModelsAfterDate(TableToSyncEnum.PERSON, lastSyncDate);

        // Then
        assertEquals(2, result.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void getModels_unknown_service() {
        // Given
        LocalDateTime lastSyncDate = LocalDateTime.now();
        facade = new EntityServiceFacade(Collections.singletonList(personService));
        when(personService.getTableToSync()).thenReturn(TableToSyncEnum.PERSON);
        when(patientService.getTableToSync()).thenReturn(TableToSyncEnum.VISIT);

        // When
        facade.getModelsAfterDate(TableToSyncEnum.VISIT, lastSyncDate);

        // Then
        // BOOM
    }

    @Test
    public void saveModel_should_save_model() {
        // Given
        PersonModel personModel = new PersonModel();
        when(personService.getTableToSync()).thenReturn(TableToSyncEnum.PERSON);

        // When
        facade.saveModel(TableToSyncEnum.PERSON, personModel);

        // Then
        verify(personService).save(personModel);
    }
}
