package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.component.utils.HashUtils.computeHash;
import static org.openmrs.eip.component.utils.HashUtils.getStoredHash;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.TestConstants;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.repository.SyncMessageRepository;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.app.receiver.processor.SyncMessageProcessor;
import org.openmrs.eip.app.receiver.task.Synchronizer;
import org.openmrs.eip.app.route.TestUtils;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.entity.light.UserLight;
import org.openmrs.eip.component.management.hash.entity.PersonHash;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.repository.light.UserLightRepository;
import org.openmrs.eip.component.service.impl.PersonService;
import org.openmrs.eip.component.utils.DateUtils;
import org.openmrs.eip.component.utils.HashUtils;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Sql(scripts = "classpath:openmrs_core_data.sql")
@TestPropertySource(properties = "spring.openmrs-datasource.maximum-pool-size=34")
@TestPropertySource(properties = "spring.mngt-datasource.maximum-pool-size=34")
public class SyncMessageProcessorBehaviorTest extends BaseReceiverTest {
	
	private static final int MSG_COUNT = 32;
	
	@Autowired
	private SiteRepository siteRepo;
	
	@Autowired
	private PersonService personService;
	
	@Autowired
	private UserLightRepository userLightRepo;
	
	@Autowired
	private SyncedMessageRepository syncedMsgRepo;
	
	@Autowired
	private SyncMessageRepository syncMsgRepo;
	
	@Before
	public void setup() throws Exception {
		SyncContext.setAppUser(userLightRepo.findById(1L).get());
	}
	
	@After
	public void tearDown() {
		SyncContext.setAppUser(null);
	}
	
	private PersonModel createPersonModel(String personUuid, LocalDateTime dateCreated) {
		PersonModel person = new PersonModel();
		person.setUuid(personUuid);
		person.setCreatorUuid(UserLight.class.getName() + "(" + TestConstants.USER_UUID + ")");
		person.setDateCreated(dateCreated);
		return person;
	}
	
	private SyncMessage createMessage(int msgId, String identifier, LocalDateTime dateCreated, SiteInfo siteInfo) {
		SyncMessage m = new SyncMessage();
		m.setMessageUuid("msg-uuid-" + msgId);
		m.setModelClassName(PersonModel.class.getName());
		m.setIdentifier(identifier);
		m.setSite(siteInfo);
		m.setSnapshot(false);
		m.setOperation(SyncOperation.u);
		m.setDateSentBySender(LocalDateTime.now());
		m.setDateCreated(new Date());
		PersonModel person = createPersonModel(identifier, dateCreated);
		person.setVoidReason("Reason-" + msgId);
		SyncModel syncModel = new SyncModel(person.getClass(), person, new SyncMetadata());
		m.setEntityPayload(JsonUtils.marshall(syncModel));
		return m;
	}
	
	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void run_shouldProcessUpdateEventsFromDifferentSitesForTheSameEntitySeriallyWithNoConflictsReported()
	    throws Exception {
		assertEquals(0, syncedMsgRepo.count());
		final String personUuid = "person-uuid";
		final LocalDateTime dateCreated = DateUtils.stringToDate("2023-05-16 10:00:00");
		PersonModel person = createPersonModel(personUuid, dateCreated);
		personService.save(person);
		PersonHash existingHash = new PersonHash();
		existingHash.setIdentifier(personUuid);
		existingHash.setHash(computeHash(personService.getModel(personUuid)));
		existingHash.setDateCreated(LocalDateTime.now());
		HashUtils.saveHash(existingHash, producerTemplate, false);
		assertNotNull(getStoredHash(personUuid, PersonHash.class, producerTemplate));
		ExecutorService executor = Executors.newFixedThreadPool(MSG_COUNT);
		List<CompletableFuture<Void>> futures = new ArrayList(MSG_COUNT);
		List<SiteInfo> sites = new ArrayList(MSG_COUNT);
		for (int i = 0; i < MSG_COUNT; i++) {
			int index = i + 1;
			SiteInfo siteInfo = new SiteInfo();
			siteInfo.setIdentifier("site-" + index);
			siteInfo.setName("site-name-" + index);
			siteInfo.setSiteInstanceName("site-instance-name-" + index);
			siteInfo.setSiteDistrict("district-" + index);
			siteInfo.setDisabled(false);
			siteInfo.setDateCreated(new Date());
			siteRepo.save(siteInfo);
			sites.add(siteInfo);
			syncMsgRepo.save(createMessage(index, personUuid, dateCreated, siteInfo));
		}
		
		for (int i = 0; i < MSG_COUNT; i++) {
			SiteInfo siteInfo = sites.get(i);
			futures.add(CompletableFuture.runAsync(new Synchronizer(siteInfo), executor));
		}
		
		CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).get();
		Set<String> processingMsgQueue = Whitebox.getInternalState(SyncMessageProcessor.class, "PROCESSING_MSG_QUEUE");
		assertTrue(processingMsgQueue.isEmpty());
		assertTrue(TestUtils.getEntities(ConflictQueueItem.class).isEmpty());
		assertTrue(TestUtils.getEntities(ReceiverRetryQueueItem.class).isEmpty());
		assertEquals(MSG_COUNT, syncedMsgRepo.count());
	}
	
}
