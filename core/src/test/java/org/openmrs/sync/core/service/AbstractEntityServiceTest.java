package org.openmrs.sync.core.service;

import org.openmrs.sync.core.entity.MockedEntity;
import org.openmrs.sync.core.mapper.EntityMapper;
import org.openmrs.sync.core.model.MockedModel;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class AbstractEntityServiceTest {

    @Mock
    private MockedOpenMrsRepository repository;

    @Mock
    private EntityMapper<MockedEntity, MockedModel> mapper;

    private MockedEntityService mockedEntityService;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        mockedEntityService = new MockedEntityService(repository, mapper);
    }

    @Test
    public void getModels() {
        // Given
        LocalDateTime lastSyncDate = LocalDateTime.now();
        MockedEntity mockedEntity1 = new MockedEntity(1L, "uuid1");
        MockedEntity mockedEntity2 = new MockedEntity(2L, "uuid2");
        MockedModel mockedModel1 = new MockedModel("uuid1");
        MockedModel mockedModel2 = new MockedModel("uuid2");
        when(repository.findModelsChangedAfterDate(lastSyncDate)).thenReturn(Arrays.asList(mockedEntity1, mockedEntity2));
        when(mapper.entityToModel(mockedEntity1)).thenReturn(mockedModel1);
        when(mapper.entityToModel(mockedEntity2)).thenReturn(mockedModel2);

        // When
        List<MockedModel> result = mockedEntityService.getModels(lastSyncDate);

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(model -> model.equals(mockedModel1)));
        assertTrue(result.stream().anyMatch(model -> model.equals(mockedModel2)));
        verify(repository, never()).findAll();
        verify(repository).findModelsChangedAfterDate(lastSyncDate);
    }

    @Test
    public void save_entity_exists() {
        // Given
        MockedModel mockedModel = new MockedModel("uuid");
        MockedEntity mockedEntity= new MockedEntity(null, "uuid");
        MockedEntity mockedEntityInDb = new MockedEntity(1L, "uuid");
        when(repository.findByUuid("uuid")).thenReturn(mockedEntityInDb);
        when(repository.save(mockedEntityInDb)).thenReturn(mockedEntity);
        when(mapper.modelToEntity(mockedModel)).thenReturn(mockedEntity);
        when(mapper.entityToModel(mockedEntityInDb)).thenReturn(mockedModel);

        // When
        MockedModel result = mockedEntityService.save(mockedModel);

        // Then
        assertEquals(mockedModel, result);
        verify(repository).save(mockedEntityInDb);
    }

    @Test
    public void save_entity_does_not_exist() {
        // Given
        MockedModel mockedModel = new MockedModel("uuid");
        MockedEntity mockedEntity= new MockedEntity(null, "uuid");
        when(repository.findByUuid("uuid")).thenReturn(null);
        when(repository.save(mockedEntity)).thenReturn(mockedEntity);
        when(mapper.modelToEntity(mockedModel)).thenReturn(mockedEntity);
        when(mapper.entityToModel(mockedEntity)).thenReturn(mockedModel);

        // When
        MockedModel result = mockedEntityService.save(mockedModel);

        // Then
        assertEquals(mockedModel, result);
        verify(repository).save(mockedEntity);
    }

    @Test
    public void save_entity_exists_and_date_changed_after() {
        // Given
        MockedModel mockedModel = new MockedModel("uuid");
        MockedEntity mockedEntity = new MockedEntity(null, "uuid");
        mockedEntity.setDateChanged(LocalDateTime.of(2019, 6, 1, 0, 0));
        MockedEntity mockedEntityInDb = new MockedEntity(null, "uuid");
        mockedEntityInDb.setDateChanged(LocalDateTime.of(2019, 6, 2, 0, 0));
        when(repository.findByUuid("uuid")).thenReturn(mockedEntityInDb);
        when(repository.save(mockedEntity)).thenReturn(mockedEntity);
        when(mapper.modelToEntity(mockedModel)).thenReturn(mockedEntity);
        when(mapper.entityToModel(mockedEntity)).thenReturn(mockedModel);

        // When
        MockedModel result = mockedEntityService.save(mockedModel);

        // Then
        assertEquals(mockedModel, result);
        verify(repository, never()).save(mockedEntity);
    }
}
