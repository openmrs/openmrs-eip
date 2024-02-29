package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_ACTIVEMQ_ENDPOINT;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.repository.PersonRepository;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SyncContext.class)
public class SenderUtilsTest {
	
	@Mock
	private Environment mockEnv;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		when(SyncContext.getBean(Environment.class)).thenReturn(mockEnv);
	}
	
	@Test
	public void mask_shouldFailForAValueOfATypeThatIsNotSupported() {
		SyncModel model = new SyncModel();
		Exception thrown = Assert.assertThrows(EIPException.class, () -> SenderUtils.mask(model));
		Assert.assertEquals("Don't know how mask a value of type: " + model.getClass(), thrown.getMessage());
	}
	
	@Test
	public void mask_shouldReturnNullForANullValue() {
		Assert.assertNull(SenderUtils.mask(null));
	}
	
	@Test
	public void mask_shouldReturnTheCorrectMaskValueForAString() {
		Assert.assertEquals(SenderConstants.MASK, SenderUtils.mask("test"));
	}
	
	@Test
	public void getQueueName_shouldReturnTheNameOfTheJmsQueue() {
		final String queueName = "activemq:openmrs.sync";
		final String endpoint = "activemq:" + queueName;
		when(mockEnv.getProperty(PROP_ACTIVEMQ_ENDPOINT)).thenReturn(endpoint);
		assertEquals(queueName, SenderUtils.getQueueName());
	}
	
	@Test
	public void getUuidFromParentTable_shouldLookUpThePatientUuid() {
		final String expectedUuid = "test-uuid";
		final String table = "patient";
		final Long patientId = 2L;
		PersonRepository mockRepo = Mockito.mock(PersonRepository.class);
		when(SyncContext.getBean(PersonRepository.class)).thenReturn(mockRepo);
		when(mockRepo.getUuid(patientId)).thenReturn(expectedUuid);
		assertEquals(expectedUuid, SenderUtils.getUuidFromParentTable(table, patientId));
	}
	
}
