package org.openmrs.eip.component.service.light;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.entity.light.MockedAttributeTypeLight;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.repository.OpenmrsRepository;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.Assert.assertEquals;

public class AbstractAttributeTypeLightServiceTest {
	
	@Mock
	private OpenmrsRepository<MockedAttributeTypeLight> repository;
	
	private MockedAttributeTypeLightService service;
	
	private static final Long USER_ID = 6L;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		service = new MockedAttributeTypeLightService(repository);
		UserLight user = new UserLight();
		user.setId(USER_ID);
		SyncContext.setAppUser(user);
	}
	
	@Test
	public void createPlaceholderEntity_should_return_entity() {
		// Given
		String uuid = "uuid";
		
		// When
		MockedAttributeTypeLight result = service.createPlaceholderEntity(uuid);
		
		// Then
		assertEquals(getExpectedAttributeType(), result);
	}
	
	private MockedAttributeTypeLight getExpectedAttributeType() {
		MockedAttributeTypeLight attributeType = new MockedAttributeTypeLight(null, null);
		attributeType.setName("[Default]");
		attributeType.setMinOccurs(0);
		attributeType.setCreator(USER_ID);
		attributeType.setDateCreated(LocalDateTime.of(1970, Month.JANUARY, 1, 0, 0));
		return attributeType;
	}
}
