package org.openmrs.eip.component.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.MockedModel;
import org.openmrs.eip.component.entity.MockedEntity;
import org.openmrs.eip.component.mapper.EntityToModelMapper;
import org.openmrs.eip.component.mapper.ModelToEntityMapper;

public class AbstractEntityServiceTest {
	
	@Mock
	private MockedOpenmrsRepository repository;
	
	@Mock
	private EntityToModelMapper<MockedEntity, MockedModel> entityToModelMapper;
	
	@Mock
	private ModelToEntityMapper<MockedModel, MockedEntity> modelToEntityMapper;
	
	private MockedEntityService mockedEntityService;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		mockedEntityService = new MockedEntityService(repository, entityToModelMapper, modelToEntityMapper);
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
		when(entityToModelMapper.apply(mockedEntity1)).thenReturn(mockedModel1);
		when(entityToModelMapper.apply(mockedEntity2)).thenReturn(mockedModel2);
		
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
		MockedEntity mockedEntity = new MockedEntity(null, "uuid");
		MockedEntity mockedEntityInDb = new MockedEntity(1L, "uuid");
		when(repository.findByUuid("uuid")).thenReturn(mockedEntityInDb);
		when(repository.save(mockedEntityInDb)).thenReturn(mockedEntity);
		when(modelToEntityMapper.apply(mockedModel)).thenReturn(mockedEntity);
		when(entityToModelMapper.apply(mockedEntityInDb)).thenReturn(mockedModel);
		
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
		MockedEntity mockedEntity = new MockedEntity(null, "uuid");
		when(repository.findByUuid("uuid")).thenReturn(null);
		when(repository.save(mockedEntity)).thenReturn(mockedEntity);
		when(modelToEntityMapper.apply(mockedModel)).thenReturn(mockedEntity);
		when(entityToModelMapper.apply(mockedEntity)).thenReturn(mockedModel);
		
		// When
		MockedModel result = mockedEntityService.save(mockedModel);
		
		// Then
		assertEquals(mockedModel, result);
		verify(repository).save(mockedEntity);
	}
	
	@Test
	public void getAllModels_should_return_models() {
		// Given
		LocalDateTime lastSyncDate = LocalDateTime.now();
		MockedEntity mockedEntity1 = new MockedEntity(1L, "uuid1");
		MockedEntity mockedEntity2 = new MockedEntity(2L, "uuid2");
		MockedModel mockedModel1 = new MockedModel("uuid1");
		MockedModel mockedModel2 = new MockedModel("uuid2");
		when(repository.findAll()).thenReturn(Arrays.asList(mockedEntity1, mockedEntity2));
		when(entityToModelMapper.apply(mockedEntity1)).thenReturn(mockedModel1);
		when(entityToModelMapper.apply(mockedEntity2)).thenReturn(mockedModel2);
		
		// When
		List<MockedModel> result = mockedEntityService.getAllModels();
		
		// Then
		assertEquals(2, result.size());
		assertTrue(result.stream().anyMatch(model -> model.equals(mockedModel1)));
		assertTrue(result.stream().anyMatch(model -> model.equals(mockedModel2)));
		verify(repository).findAll();
		verify(repository, never()).findModelsChangedAfterDate(lastSyncDate);
	}
	
	@Test
	public void getModel_by_uuid_should_return_model() {
		// Given
		MockedEntity mockedEntity = new MockedEntity(1L, "uuid");
		MockedModel mockedModel = new MockedModel("uuid");
		when(repository.findByUuid("uuid")).thenReturn(mockedEntity);
		when(entityToModelMapper.apply(mockedEntity)).thenReturn(mockedModel);
		
		// When
		MockedModel result = mockedEntityService.getModel("uuid");
		
		// Then
		assertNotNull(result);
		assertEquals(mockedModel, result);
	}
	
	@Test
	public void getModel_shoulReturnNullIfNoMatchIsFound() {
		assertNull(mockedEntityService.getModel("uuid"));
		verify(entityToModelMapper, never()).apply(any());
	}
	
	@Test
	public void getModel_by_id_should_return_model() {
		// Given
		MockedEntity mockedEntity = new MockedEntity(1L, "uuid");
		MockedModel mockedModel = new MockedModel("uuid");
		when(repository.findById(1L)).thenReturn(Optional.of(mockedEntity));
		when(entityToModelMapper.apply(mockedEntity)).thenReturn(mockedModel);
		
		// When
		MockedModel result = mockedEntityService.getModel(1L);
		
		// Then
		assertNotNull(result);
		assertEquals(mockedModel, result);
	}
	
	@Test
	public void getModel_by_id_should_return_null() {
		// Given
		MockedEntity mockedEntity = new MockedEntity(1L, "uuid");
		when(repository.findById(1L)).thenReturn(Optional.empty());
		
		// When
		MockedModel result = mockedEntityService.getModel(1L);
		
		// Then
		assertNull(result);
		verify(entityToModelMapper, never()).apply(any());
	}
	
	@Test
	public void delete_shouldDeleteTheEntityMatchingTheModelUuid() {
		final String uuid = "some-uuid";
		MockedEntity mockedEntity = new MockedEntity(1L, uuid);
		when(repository.findByUuid(uuid)).thenReturn(mockedEntity);
		
		mockedEntityService.delete(uuid);
		
		verify(repository).delete(mockedEntity);
	}
	
	@Test
	public void delete_shouldNotCallDeleteIfThereIsNoEntityMatchingTheModelUuid() {
		final String uuid = "some-uuid";
		when(repository.findByUuid(uuid)).thenReturn(null);
		
		mockedEntityService.delete(uuid);
		
		verify(repository, never()).delete(any());
	}
	
}
