package org.openmrs.eip.app.receiver.reconcile;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation.ReconciliationStatus;
import org.openmrs.eip.app.management.repository.ReceiverReconcileRepository;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ReconcileSchedulerTest {
	
	@Mock
	private ReceiverReconcileRepository mockRepo;
	
	private ReconcileScheduler scheduler;
	
	@Test
	public void execute_shouldAddAReconciliation() {
		scheduler = new ReconcileScheduler(mockRepo);
		long timestamp = System.currentTimeMillis();
		
		scheduler.execute();
		
		ArgumentCaptor<ReceiverReconciliation> argCaptor = ArgumentCaptor.forClass(ReceiverReconciliation.class);
		Mockito.verify(mockRepo).save(argCaptor.capture());
		ReceiverReconciliation rec = argCaptor.getValue();
		Assert.assertEquals(ReconciliationStatus.NEW, rec.getStatus());
		Assert.assertNotNull(rec.getIdentifier());
		assertTrue(rec.getDateCreated().getTime() == timestamp || rec.getDateCreated().getTime() > timestamp);
	}
	
}
