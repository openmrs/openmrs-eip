package org.openmrs.eip.component.service.light;

import org.junit.After;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.MockedLightEntity;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

public class AbstractLightServiceTest {
	
	@Mock
	private OpenmrsRepository<MockedLightEntity> repository;
	
	private MockedLightService service;
	
	private static final String UUID = "uuid";
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new MockedLightService(repository);
		UserLight user = new UserLight();
		user.setId(USER_ID);
		SyncContext.setAppUser(user);
	}
	
	@After
	public void tearDown() {
		SyncContext.setAppUser(null);
	}
	
	@Test
	public void getOrInitEntity_should_return_null() {
		// Given
		
		// When
		MockedLightEntity result = service.getOrInitEntity(null);
		
		// Then
		assertNull(result);
	}
	
	@Test
	public void getOrInitEntity_should_create_entity() {
		// Given
		when(repository.findByUuid(UUID)).thenReturn(null);
		MockedLightEntity expectedEntity = getExpectedEntity(UUID);
		when(repository.save(expectedEntity)).thenReturn(expectedEntity);
		
		// When
		MockedLightEntity result = service.getOrInitEntity(UUID);
		
		// Then
		assertEquals(expectedEntity, result);
		verify(repository).save(expectedEntity);
	}
	
	@Test
	public void getOrInitEntity_should_return_entity() {
		// Given
		MockedLightEntity userEty = getExpectedEntity(UUID);
		when(repository.findByUuid(UUID)).thenReturn(userEty);
		when(repository.save(userEty)).thenReturn(userEty);
		
		// When
		MockedLightEntity result = service.getOrInitEntity(UUID);
		
		// Then
		verify(repository, never()).save(any());
		assertEquals(userEty, result);
	}
	
	@Test
	public void getOrInitPlaceholderEntity_should_create_placeholder() {
		// Given
		when(repository.findByUuid(UUID)).thenReturn(null);
		when(repository.findByUuid("PLACEHOLDER_MOCKED_LIGHT_ENTITY")).thenReturn(null);
		MockedLightEntity expectedEntity = getExpectedEntity("PLACEHOLDER_MOCKED_LIGHT_ENTITY");
		when(repository.save(expectedEntity)).thenReturn(expectedEntity);
		
		// When
		MockedLightEntity result = service.getOrInitPlaceholderEntity();
		
		// Then
		assertEquals(expectedEntity, result);
		verify(repository).save(expectedEntity);
	}
	
	@Test
	public void getOrInitPlaceholderEntity_should_return_entity() {
		// Given
		MockedLightEntity userEty = getExpectedEntity("PLACEHOLDER_MOCKED_LIGHT_ENTITY");
		when(repository.findByUuid("PLACEHOLDER_MOCKED_LIGHT_ENTITY")).thenReturn(userEty);
		when(repository.save(userEty)).thenReturn(userEty);
		
		// When
		MockedLightEntity result = service.getOrInitPlaceholderEntity();
		
		// Then
		verify(repository, never()).save(any());
		assertEquals(userEty, result);
	}
	
	private MockedLightEntity getExpectedEntity(final String uuid) {
		MockedLightEntity mockedLightEntity = new MockedLightEntity(1L, uuid);
		mockedLightEntity.setVoided(true);
		mockedLightEntity.setVoidReason(AbstractLightService.DEFAULT_VOID_REASON);
		mockedLightEntity.setDateVoided(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		mockedLightEntity.setVoidedBy(USER_ID);
		return mockedLightEntity;
	}
}
