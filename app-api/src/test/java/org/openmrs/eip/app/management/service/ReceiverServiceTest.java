package org.openmrs.eip.app.management.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.springframework.aop.framework.AopProxyUtils.getSingletonTarget;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.openmrs.eip.TestConstants;
import org.openmrs.eip.app.management.entity.receiver.ConflictQueueItem;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage.MessageType;
import org.openmrs.eip.app.management.entity.receiver.ReceiverPrunedItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverRetryQueueItem;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncArchive;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncRequest;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncRequest.ReceiverRequestStatus;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage;
import org.openmrs.eip.app.management.entity.receiver.SyncedMessage.SyncOutcome;
import org.openmrs.eip.app.management.repository.ConflictRepository;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.openmrs.eip.app.management.repository.ReceiverPrunedItemRepository;
import org.openmrs.eip.app.management.repository.ReceiverRetryRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncArchiveRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncRequestRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.repository.SyncMessageRepository;
import org.openmrs.eip.app.management.repository.SyncedMessageRepository;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.app.receiver.ReceiverActiveMqMessagePublisher;
import org.openmrs.eip.app.receiver.SyncStatusProcessor;
import org.openmrs.eip.component.Constants;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.management.hash.entity.BaseHashEntity;
import org.openmrs.eip.component.management.hash.entity.PatientHash;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.impl.PatientService;
import org.openmrs.eip.component.utils.HashUtils;
import org.openmrs.eip.component.utils.JsonUtils;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@TestPropertySource(properties = Constants.PROP_URI_ERROR_HANDLER + "=" + TestConstants.URI_ERROR_HANDLER)
public class ReceiverServiceTest extends BaseReceiverTest {
	
	@Autowired
	private SyncMessageRepository syncMsgRepo;
	
	@Autowired
	private SyncedMessageRepository syncedMsgRepo;
	
	@Autowired
	private ReceiverSyncArchiveRepository archiveRepo;
	
	@Autowired
	private ReceiverRetryRepository retryRepo;
	
	@Autowired
	private ConflictRepository conflictRepo;
	
	@Autowired
	private ReceiverPrunedItemRepository prunedRepo;
	
	@Autowired
	private SiteRepository siteRepo;
	
	@Autowired
	private JmsMessageRepository jmsMsgRepo;
	
	@Autowired
	private ReceiverSyncRequestRepository syncReqRepo;
	
	@Autowired
	private ReceiverService service;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private SyncStatusProcessor statusProcessor;
	
	private SyncStatusProcessor mockStatusProcessor;
	
	@Autowired
	private ReceiverActiveMqMessagePublisher publisher;
	
	private ReceiverActiveMqMessagePublisher mockPublisher;
	
	@Before
	public void setup() throws Exception {
		mockStatusProcessor = Mockito.mock(SyncStatusProcessor.class);
		mockPublisher = Mockito.mock(ReceiverActiveMqMessagePublisher.class);
		Whitebox.setInternalState(getSingletonTarget(service), SyncStatusProcessor.class, mockStatusProcessor);
		Whitebox.setInternalState(getSingletonTarget(service), ReceiverActiveMqMessagePublisher.class, mockPublisher);
	}
	
	@After
	public void tearDown() throws Exception {
		Whitebox.setInternalState(getSingletonTarget(service), SyncStatusProcessor.class, statusProcessor);
		Whitebox.setInternalState(getSingletonTarget(service), ReceiverActiveMqMessagePublisher.class, publisher);
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_archive.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void prune_shouldMoveAnArchiveToThePrunedQueue() {
		final Long id = 1L;
		ReceiverSyncArchive archive = archiveRepo.findById(id).get();
		assertEquals(0, prunedRepo.count());
		
		service.prune(archive);
		
		assertFalse(archiveRepo.findById(id).isPresent());
		List<ReceiverPrunedItem> prunedItems = prunedRepo.findAll();
		assertEquals(1, prunedItems.size());
		assertEquals(archive.getMessageUuid(), prunedItems.get(0).getMessageUuid());
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void moveToSyncedQueue_shouldMoveTheMessageToTheSyncedQueue() {
		final Long id = 1L;
		SyncMessage msg = syncMsgRepo.findById(id).get();
		assertEquals(0, syncedMsgRepo.count());
		
		service.moveToSyncedQueue(msg, SyncOutcome.SUCCESS);
		
		assertFalse(syncMsgRepo.findById(id).isPresent());
		List<SyncedMessage> syncedItems = syncedMsgRepo.findAll();
		assertEquals(1, syncedItems.size());
		assertEquals(msg.getMessageUuid(), syncedItems.get(0).getMessageUuid());
		assertEquals(SyncOutcome.SUCCESS, syncedItems.get(0).getOutcome());
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_synced_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void archiveSyncedMessage_shouldMoveTheSyncedMessageToTheArchiveQueue() {
		final Long id = 1L;
		SyncedMessage msg = syncedMsgRepo.findById(id).get();
		assertEquals(0, archiveRepo.count());
		long timestamp = System.currentTimeMillis();
		
		service.archiveSyncedMessage(msg);
		
		assertFalse(syncedMsgRepo.findById(id).isPresent());
		List<ReceiverSyncArchive> archives = archiveRepo.findAll();
		assertEquals(1, archives.size());
		ReceiverSyncArchive a = archives.get(0);
		assertEquals(msg.getMessageUuid(), a.getMessageUuid());
		assertTrue(a.getDateCreated().getTime() == timestamp || a.getDateCreated().getTime() > timestamp);
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_retry_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void archiveRetry_shouldMoveTheSyncedMessageToTheArchiveQueue() {
		final Long id = 1L;
		ReceiverRetryQueueItem retry = retryRepo.findById(id).get();
		assertEquals(0, archiveRepo.count());
		long timestamp = System.currentTimeMillis();
		
		service.archiveRetry(retry);
		
		assertFalse(retryRepo.findById(id).isPresent());
		List<ReceiverSyncArchive> archives = archiveRepo.findAll();
		assertEquals(1, archives.size());
		ReceiverSyncArchive a = archives.get(0);
		assertEquals(retry.getMessageUuid(), a.getMessageUuid());
		assertTrue(a.getDateCreated().getTime() == timestamp || a.getDateCreated().getTime() > timestamp);
	}
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	public void moveToArchiveQueue_shouldMoveTheConflictToTheArchiveQueueAndUpdateTheHash() {
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		BaseHashEntity hashEntity = new PatientHash();
		hashEntity.setIdentifier(uuid);
		final String expectedNewHash = HashUtils.computeHash(patientService.getModel(uuid));
		final String currentHash = "current-hash";
		assertNotEquals(currentHash, expectedNewHash);
		hashEntity.setHash(currentHash);
		hashEntity.setDateCreated(LocalDateTime.now());
		HashUtils.saveHash(hashEntity, producerTemplate, false);
		Assert.assertNull(hashEntity.getDateChanged());
		final long timestamp = System.currentTimeMillis();
		
		service.updateHash(PatientModel.class.getName(), uuid);
		
		hashEntity = HashUtils.getStoredHash(uuid, PatientHash.class, producerTemplate);
		assertEquals(expectedNewHash, hashEntity.getHash());
		long dateChangedMillis = hashEntity.getDateChanged().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
		assertTrue(dateChangedMillis == timestamp || dateChangedMillis > timestamp);
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void hasSyncItem_shouldReturnTrueIfAnEntityHasASyncItem() {
		assertTrue(service.hasSyncItem("4bfd940e-32dc-491f-8038-a8f3afe3e36c", PersonModel.class.getName()));
		assertTrue(service.hasSyncItem("4bfd940e-32dc-491f-8038-a8f3afe3e36c", PatientModel.class.getName()));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void hasSyncItem_shouldReturnFalseIfAnEntityHasNoSyncItem() {
		assertFalse(service.hasSyncItem("some-uuid", PersonModel.class.getName()));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_retry_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void hasRetryItem_shouldReturnTrueIfAnEntityHasARetryItem() {
		assertTrue(service.hasRetryItem("uuid-1", PersonModel.class.getName()));
		assertTrue(service.hasRetryItem("uuid-1", PatientModel.class.getName()));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_retry_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void hasRetryItem_shouldReturnFalseIfAnEntityHasNoRetryItem() {
		assertFalse(service.hasRetryItem("some-uuid", PersonModel.class.getName()));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void processFailedSyncItem_shouldAddTheItemToTheErrorAndSyncedQueues() {
		final Long id = 1L;
		SyncMessage msg = syncMsgRepo.findById(id).get();
		assertEquals(0, syncedMsgRepo.count());
		assertEquals(0, retryRepo.count());
		final String exception = EIPException.class.getName();
		final String errMsg = "test error msg";
		
		service.processFailedSyncItem(msg, exception, errMsg);
		
		assertFalse(syncMsgRepo.findById(id).isPresent());
		List<SyncedMessage> syncedItems = syncedMsgRepo.findAll();
		assertEquals(1, syncedItems.size());
		assertEquals(msg.getMessageUuid(), syncedItems.get(0).getMessageUuid());
		assertEquals(SyncOutcome.ERROR, syncedItems.get(0).getOutcome());
		List<ReceiverRetryQueueItem> retryItems = retryRepo.findAll();
		assertEquals(1, retryItems.size());
		assertEquals(msg.getMessageUuid(), retryItems.get(0).getMessageUuid());
		assertEquals(exception, retryItems.get(0).getExceptionType());
		assertEquals(errMsg, retryItems.get(0).getMessage());
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void processConflictedSyncItem_shouldAddTheItemToTheConflictAndSyncedQueues() {
		final Long id = 1L;
		SyncMessage msg = syncMsgRepo.findById(id).get();
		assertEquals(0, syncedMsgRepo.count());
		assertEquals(0, conflictRepo.count());
		
		service.processConflictedSyncItem(msg);
		
		assertFalse(syncMsgRepo.findById(id).isPresent());
		List<SyncedMessage> syncedItems = syncedMsgRepo.findAll();
		assertEquals(1, syncedItems.size());
		assertEquals(msg.getMessageUuid(), syncedItems.get(0).getMessageUuid());
		assertEquals(SyncOutcome.CONFLICT, syncedItems.get(0).getOutcome());
		List<ConflictQueueItem> conflictItems = conflictRepo.findAll();
		assertEquals(1, conflictItems.size());
		assertEquals(msg.getMessageUuid(), conflictItems.get(0).getMessageUuid());
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_retry_queue.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void moveToConflictQueue_shouldMoveTheRetryItemToTheConflictQueue() {
		final Long id = 1L;
		ReceiverRetryQueueItem retry = retryRepo.findById(id).get();
		assertEquals(0, conflictRepo.count());
		long timestamp = System.currentTimeMillis();
		
		service.moveToConflictQueue(retry);
		
		assertFalse(retryRepo.findById(id).isPresent());
		List<ConflictQueueItem> conflicts = conflictRepo.findAll();
		assertEquals(1, conflicts.size());
		ConflictQueueItem c = conflicts.get(0);
		assertEquals(retry.getMessageUuid(), c.getMessageUuid());
		assertTrue(c.getDateCreated().getTime() == timestamp || c.getDateCreated().getTime() > timestamp);
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_request.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void processJmsMessage_shouldProcessAndSaveASyncMessage() throws Exception {
		final LocalDateTime dateSent = LocalDateTime.now();
		final String uuid = "person-uuid";
		final String msgUuid = "msg-uuid";
		final SyncOperation op = SyncOperation.u;
		assertEquals(0, syncMsgRepo.count());
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		PersonModel personModel = new PersonModel();
		personModel.setUuid(uuid);
		syncModel.setModel(personModel);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setMessageUuid(msgUuid);
		metadata.setSnapshot(false);
		metadata.setOperation(op.toString());
		SiteInfo siteInfo = siteRepo.findById(1L).get();
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		metadata.setDateSent(dateSent);
		syncModel.setMetadata(metadata);
		String payLoad = JsonUtils.marshall(syncModel);
		JmsMessage jmsMsg = new JmsMessage();
		jmsMsg.setType(MessageType.SYNC);
		jmsMsg.setBody(payLoad.getBytes(UTF_8));
		jmsMsg.setDateCreated(new Date());
		jmsMsgRepo.save(jmsMsg);
		assertEquals(1, jmsMsgRepo.count());
		Long timestamp = System.currentTimeMillis();
		
		service.processJmsMessage(jmsMsg);
		
		List<SyncMessage> msgs = syncMsgRepo.findAll();
		assertEquals(1, msgs.size());
		SyncMessage msg = msgs.get(0);
		assertEquals(PersonModel.class.getName(), msg.getModelClassName());
		assertEquals(uuid, msg.getIdentifier());
		assertEquals(op, msg.getOperation());
		assertEquals(msgUuid, msg.getMessageUuid());
		assertEquals(JsonUtils.marshall(syncModel), msg.getEntityPayload());
		assertEquals(siteInfo, msg.getSite());
		assertEquals(dateSent, msg.getDateSentBySender());
		assertFalse(msg.getSnapshot());
		assertTrue(msg.getDateCreated().getTime() == timestamp || msg.getDateCreated().getTime() > timestamp);
		assertEquals(0, jmsMsgRepo.count());
		Mockito.verify(mockStatusProcessor).process(ArgumentMatchers.any(SyncMetadata.class));
		Mockito.verifyNoInteractions(mockPublisher);
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_request.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void processJmsMessage_shouldProcessAndSaveASyncMessageLinkedToASyncRequest() throws Exception {
		final LocalDateTime dateSent = LocalDateTime.now();
		final String uuid = "person-uuid";
		final String msgUuid = "msg-uuid";
		final SyncOperation op = SyncOperation.r;
		final String requestUuid = "46beb8bd-287c-47f2-9786-a7b98c933c04";
		ReceiverSyncRequest request = syncReqRepo.findById(4L).get();
		assertEquals(requestUuid, request.getRequestUuid());
		assertEquals(ReceiverRequestStatus.SENT, request.getStatus());
		assertEquals(0, syncMsgRepo.count());
		SyncModel syncModel = new SyncModel();
		syncModel.setTableToSyncModelClass(PersonModel.class);
		PersonModel personModel = new PersonModel();
		personModel.setUuid(uuid);
		syncModel.setModel(personModel);
		SyncMetadata metadata = new SyncMetadata();
		metadata.setMessageUuid(msgUuid);
		metadata.setSnapshot(false);
		metadata.setOperation(op.toString());
		metadata.setRequestUuid(requestUuid);
		SiteInfo siteInfo = siteRepo.findById(1L).get();
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		metadata.setDateSent(dateSent);
		syncModel.setMetadata(metadata);
		String payLoad = JsonUtils.marshall(syncModel);
		JmsMessage jmsMsg = new JmsMessage();
		jmsMsg.setType(MessageType.SYNC);
		jmsMsg.setBody(payLoad.getBytes(UTF_8));
		jmsMsg.setDateCreated(new Date());
		jmsMsgRepo.save(jmsMsg);
		assertEquals(1, jmsMsgRepo.count());
		
		service.processJmsMessage(jmsMsg);
		
		assertEquals(1, syncMsgRepo.count());
		assertEquals(ReceiverRequestStatus.RECEIVED, syncReqRepo.findByRequestUuid(requestUuid).getStatus());
		assertEquals(0, jmsMsgRepo.count());
		Mockito.verify(mockStatusProcessor).process(ArgumentMatchers.any(SyncMetadata.class));
		Mockito.verifyNoInteractions(mockPublisher);
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_request.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void processJmsMessage_shouldProcessAndSaveASyncMessageLinkedToASyncRequestAndTheEntityWasNotFound()
	    throws Exception {
		final LocalDateTime dateSent = LocalDateTime.now();
		final String msgUuid = "msg-uuid";
		final String op = "r";
		final String requestUuid = "46beb8bd-287c-47f2-9786-a7b98c933c04";
		ReceiverSyncRequest request = syncReqRepo.findById(4L).get();
		assertEquals(requestUuid, request.getRequestUuid());
		assertEquals(ReceiverRequestStatus.SENT, request.getStatus());
		assertEquals(0, syncMsgRepo.count());
		SyncModel syncModel = new SyncModel();
		SyncMetadata metadata = new SyncMetadata();
		metadata.setMessageUuid(msgUuid);
		metadata.setSnapshot(false);
		metadata.setOperation(op);
		metadata.setRequestUuid(requestUuid);
		SiteInfo siteInfo = siteRepo.findById(1L).get();
		metadata.setSourceIdentifier(siteInfo.getIdentifier());
		metadata.setDateSent(dateSent);
		syncModel.setMetadata(metadata);
		String payLoad = JsonUtils.marshall(syncModel);
		JmsMessage jmsMsg = new JmsMessage();
		jmsMsg.setType(MessageType.SYNC);
		jmsMsg.setBody(payLoad.getBytes(UTF_8));
		jmsMsg.setDateCreated(new Date());
		jmsMsgRepo.save(jmsMsg);
		assertEquals(1, jmsMsgRepo.count());
		
		service.processJmsMessage(jmsMsg);
		
		assertEquals(0, syncMsgRepo.count());
		assertEquals(ReceiverRequestStatus.RECEIVED, syncReqRepo.findByRequestUuid(requestUuid).getStatus());
		assertEquals(0, jmsMsgRepo.count());
		Mockito.verify(mockStatusProcessor).process(ArgumentMatchers.any(SyncMetadata.class));
		Mockito.verify(mockPublisher).sendSyncResponse(request, msgUuid);
	}
	
}
