package org.openmrs.eip.app.management.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.sender.DebeziumEvent;
import org.openmrs.eip.app.management.entity.sender.SenderPrunedArchive;
import org.openmrs.eip.app.management.entity.sender.SenderRetryQueueItem;
import org.openmrs.eip.app.management.entity.sender.SenderSyncArchive;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage;
import org.openmrs.eip.app.management.entity.sender.SenderSyncMessage.SenderSyncMessageStatus;
import org.openmrs.eip.app.management.repository.DebeziumEventRepository;
import org.openmrs.eip.app.management.repository.SenderPrunedArchiveRepository;
import org.openmrs.eip.app.management.repository.SenderRetryRepository;
import org.openmrs.eip.app.management.repository.SenderSyncArchiveRepository;
import org.openmrs.eip.app.management.repository.SenderSyncMessageRepository;
import org.openmrs.eip.app.route.sender.SenderTestUtils;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.openmrs.eip.component.entity.Event;
import org.openmrs.eip.component.exception.EIPException;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.SyncMetadata;
import org.openmrs.eip.component.model.SyncModel;
import org.openmrs.eip.component.service.impl.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import com.jayway.jsonpath.JsonPath;

public class SenderServiceTest extends BaseSenderTest {
	
	@Autowired
	private SenderSyncArchiveRepository archiveRepo;
	
	@Autowired
	private SenderPrunedArchiveRepository prunedRepo;
	
	@Autowired
	private DebeziumEventRepository eventRepo;
	
	@Autowired
	private SenderSyncMessageRepository syncRepo;
	
	@Autowired
	private SenderRetryRepository retryRepo;
	
	@Autowired
	private PatientService patientService;
	
	@Autowired
	private SenderService service;
	
	protected Event createEvent(String table, String pkId, String identifier, String op, boolean snapshot) {
		Event event = new Event();
		event.setTableName(table);
		event.setPrimaryKeyId(pkId);
		event.setIdentifier(identifier);
		event.setOperation(op);
		event.setSnapshot(snapshot);
		return event;
	}
	
	protected DebeziumEvent createDebeziumEvent(String table, String pkId, String uuid, String op, boolean snapshot) {
		DebeziumEvent dbzmEvent = new DebeziumEvent();
		dbzmEvent.setEvent(createEvent(table, pkId, uuid, op, snapshot));
		dbzmEvent.setDateCreated(new Date());
		return eventRepo.save(dbzmEvent);
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_sender_sync_archive.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void prune_shouldMoveAnArchiveToThePrunedTable() {
		final Long id = 1L;
		SenderSyncArchive archive = archiveRepo.findById(id).get();
		assertEquals(0, prunedRepo.count());
		
		service.prune(archive);
		
		assertFalse(archiveRepo.findById(id).isPresent());
		List<SenderPrunedArchive> prunedItems = prunedRepo.findAll();
		assertEquals(1, prunedItems.size());
		assertEquals(archive.getMessageUuid(), prunedItems.get(0).getMessageUuid());
	}
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	@Sql(scripts = "classpath:mgt_debezium_event_queue.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void moveEventToSyncQueue_shouldMoveAnItemFromTheEventToTheSyncQueue() {
		final String table = "patient";
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		final String op = "c";
		final boolean snapshot = true;
		DebeziumEvent dbzmEvent = createDebeziumEvent(table, "101", uuid, op, snapshot);
		PatientModel patientModel = patientService.getModel(uuid);
		SyncModel syncModel = SyncModel.builder().tableToSyncModelClass(PatientModel.class).model(patientModel)
		        .metadata(new SyncMetadata()).build();
		assertEquals(0, syncRepo.count());
		
		service.moveEventToSyncQueue(dbzmEvent, syncModel);
		
		assertFalse(eventRepo.findById(dbzmEvent.getId()).isPresent());
		List<SenderSyncMessage> syncMsgs = syncRepo.findAll();
		assertEquals(1, syncMsgs.size());
		SenderSyncMessage msg = syncMsgs.get(0);
		assertEquals(table, msg.getTableName());
		assertEquals(uuid, msg.getIdentifier());
		assertEquals(op, msg.getOperation());
		assertEquals(snapshot, msg.getSnapshot());
		assertEquals(SenderSyncMessageStatus.NEW, msg.getStatus());
		assertNotNull(msg.getMessageUuid());
		assertNotNull(msg.getDateCreated());
		assertEquals(msg.getEventDate().getTime(), dbzmEvent.getDateCreated().getTime());
		assertNull(msg.getRequestUuid());
		assertNull(msg.getDateSent());
		assertEquals(PatientModel.class.getName(), JsonPath.read(msg.getData(), "tableToSyncModelClass"));
		assertEquals(uuid, JsonPath.read(msg.getData(), "model.uuid"));
		assertEquals(op, JsonPath.read(msg.getData(), "metadata.operation"));
		assertEquals(msg.getMessageUuid(), JsonPath.read(msg.getData(), "metadata.messageUuid"));
		assertTrue(JsonPath.read(msg.getData(), "metadata.snapshot"));
		assertNull(JsonPath.read(msg.getData(), "metadata.sourceIdentifier"));
		assertNull(JsonPath.read(msg.getData(), "metadata.dateSent"));
		assertNull(JsonPath.read(msg.getData(), "metadata.requestUuid"));
	}
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	@Sql(scripts = "classpath:mgt_debezium_event_queue.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void moveEventToSyncQueue_shouldMoveARequestEventFromTheEventToTheSyncQueue() {
		final String table = "person";
		final String uuid = "some-person-uuid";
		final String op = "r";
		DebeziumEvent dbzmEvent = createDebeziumEvent(table, "101", uuid, op, false);
		assertEquals(0, syncRepo.count());
		
		service.moveEventToSyncQueue(dbzmEvent, null);
		
		assertFalse(eventRepo.findById(dbzmEvent.getId()).isPresent());
		List<SenderSyncMessage> syncMsgs = syncRepo.findAll();
		assertEquals(1, syncMsgs.size());
		SenderSyncMessage msg = syncMsgs.get(0);
		assertEquals(table, msg.getTableName());
		assertEquals(uuid, msg.getIdentifier());
		assertEquals(op, msg.getOperation());
		assertFalse(msg.getSnapshot());
		assertEquals(SenderSyncMessageStatus.NEW, msg.getStatus());
		assertNotNull(msg.getMessageUuid());
		assertNotNull(msg.getDateCreated());
		assertEquals(msg.getEventDate().getTime(), dbzmEvent.getDateCreated().getTime());
		assertEquals(op, JsonPath.read(msg.getData(), "metadata.operation"));
		assertEquals(msg.getMessageUuid(), JsonPath.read(msg.getData(), "metadata.messageUuid"));
		assertFalse(JsonPath.read(msg.getData(), "metadata.snapshot"));
		assertNull(msg.getRequestUuid());
		assertNull(msg.getDateSent());
		assertNull(JsonPath.read(msg.getData(), "tableToSyncModelClass"));
		assertNull(JsonPath.read(msg.getData(), "model"));
		assertNull(JsonPath.read(msg.getData(), "metadata.sourceIdentifier"));
		assertNull(JsonPath.read(msg.getData(), "metadata.dateSent"));
		assertNull(JsonPath.read(msg.getData(), "metadata.requestUuid"));
	}
	
	@Test
	@Sql(scripts = { "classpath:openmrs_core_data.sql", "classpath:openmrs_patient.sql" })
	@Sql(scripts = "classpath:mgt_debezium_event_queue.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void moveRetryToSyncQueue_shouldMoveAnItemFromTheRetryToTheSyncQueue() {
		final String table = "patient";
		final String uuid = "abfd940e-32dc-491f-8038-a8f3afe3e35b";
		final String op = "c";
		SenderRetryQueueItem retry = new SenderRetryQueueItem();
		retry.setEvent(createEvent(table, "101", uuid, op, false));
		retry.setDateCreated(new Date());
		retry.setAttemptCount(1);
		retry.setExceptionType(EIPException.class.getName());
		retry.setEventDate(new Date());
		retry = retryRepo.save(retry);
		PatientModel patientModel = patientService.getModel(uuid);
		SyncModel syncModel = SyncModel.builder().tableToSyncModelClass(PatientModel.class).model(patientModel)
		        .metadata(new SyncMetadata()).build();
		assertEquals(0, syncRepo.count());
		
		service.moveRetryToSyncQueue(retry, syncModel);
		
		assertFalse(retryRepo.findById(retry.getId()).isPresent());
		List<SenderSyncMessage> syncMsgs = syncRepo.findAll();
		assertEquals(1, syncMsgs.size());
		SenderSyncMessage msg = syncMsgs.get(0);
		assertEquals(table, msg.getTableName());
		assertEquals(uuid, msg.getIdentifier());
		assertEquals(op, msg.getOperation());
		assertFalse(msg.getSnapshot());
		assertEquals(SenderSyncMessageStatus.NEW, msg.getStatus());
		assertNotNull(msg.getMessageUuid());
		assertNotNull(msg.getDateCreated());
		assertEquals(msg.getEventDate().getTime(), retry.getEventDate().getTime());
		assertNull(msg.getRequestUuid());
		assertNull(msg.getDateSent());
		assertEquals(PatientModel.class.getName(), JsonPath.read(msg.getData(), "tableToSyncModelClass"));
		assertEquals(uuid, JsonPath.read(msg.getData(), "model.uuid"));
		assertEquals(op, JsonPath.read(msg.getData(), "metadata.operation"));
		assertEquals(msg.getMessageUuid(), JsonPath.read(msg.getData(), "metadata.messageUuid"));
		assertFalse(JsonPath.read(msg.getData(), "metadata.snapshot"));
		assertNull(JsonPath.read(msg.getData(), "metadata.sourceIdentifier"));
		assertNull(JsonPath.read(msg.getData(), "metadata.dateSent"));
		assertNull(JsonPath.read(msg.getData(), "metadata.requestUuid"));
	}
	
	@Test
	@Sql(scripts = "classpath:mgt_debezium_event_queue.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void moveToRetryQueue_shouldMoveAnItemFromTheEventToTheRetryQueue() {
		final Long debeziumEventId = 1L;
		final String errorMsg = "test error";
		DebeziumEvent dbzmEvent = eventRepo.getReferenceById(debeziumEventId);
		Event event = dbzmEvent.getEvent();
		assertFalse(SenderTestUtils.hasRetryItem(event.getTableName(), event.getPrimaryKeyId()));
		assertEquals(0, retryRepo.count());
		
		service.moveToRetryQueue(dbzmEvent, EIPException.class.getName(), errorMsg);
		
		assertFalse(eventRepo.findById(dbzmEvent.getId()).isPresent());
		List<SenderRetryQueueItem> retries = retryRepo.findAll();
		assertEquals(1, retries.size());
		SenderRetryQueueItem errorItem = retries.get(0);
		assertEquals(event.getTableName(), errorItem.getEvent().getTableName());
		assertEquals(event.getPrimaryKeyId(), errorItem.getEvent().getPrimaryKeyId());
		assertEquals(event.getIdentifier(), errorItem.getEvent().getIdentifier());
		assertEquals(event.getOperation(), errorItem.getEvent().getOperation());
		assertEquals(event.getSnapshot(), errorItem.getEvent().getSnapshot());
		assertEquals(event.getRequestUuid(), errorItem.getEvent().getRequestUuid());
		assertEquals(1, errorItem.getAttemptCount().intValue());
		assertEquals(dbzmEvent.getDateCreated(), errorItem.getEventDate());
		assertNotNull(errorItem.getDateCreated());
		assertNull(errorItem.getDateChanged());
		assertEquals(EIPException.class.getName(), errorItem.getExceptionType());
		assertEquals(errorMsg, errorItem.getMessage());
	}
	
}
