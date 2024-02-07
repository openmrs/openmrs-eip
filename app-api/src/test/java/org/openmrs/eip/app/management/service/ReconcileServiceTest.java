package org.openmrs.eip.app.management.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncRequest;
import org.openmrs.eip.app.management.entity.receiver.ReceiverSyncRequest.ReceiverRequestStatus;
import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.openmrs.eip.app.management.repository.ReceiverSyncRequestRepository;
import org.openmrs.eip.app.management.repository.ReconciliationMsgRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.component.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = {
        "classpath:mgt_site_info.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReconcileServiceTest extends BaseReceiverTest {
	
	@Autowired
	private ReconcileService service;
	
	@Autowired
	private ReconciliationMsgRepository reconcileMsgRep;
	
	@Autowired
	private JmsMessageRepository jmsMsgRepo;
	
	@Autowired
	private SiteRepository siteRepo;
	
	@Autowired
	private ReceiverSyncRequestRepository requestRepo;
	
	@Test
	public void processSyncJmsMessage_shouldProcessAndSaveAReconcileMessage() {
		assertEquals(0, reconcileMsgRep.count());
		final String table = "person";
		final String data = "person-uuid-1,person-uuid-2";
		final int batchSize = 10;
		final boolean last = true;
		ReconciliationResponse resp = new ReconciliationResponse();
		resp.setTableName(table);
		resp.setBatchSize(batchSize);
		resp.setData(data);
		resp.setLastTableBatch(last);
		String payLoad = JsonUtils.marshall(resp);
		JmsMessage jmsMsg = new JmsMessage();
		SiteInfo site = siteRepo.getReferenceById(1L);
		jmsMsg.setSiteId(site.getIdentifier());
		jmsMsg.setType(JmsMessage.MessageType.SYNC);
		jmsMsg.setBody(payLoad.getBytes(UTF_8));
		jmsMsg.setDateCreated(new Date());
		jmsMsgRepo.save(jmsMsg);
		assertEquals(1, jmsMsgRepo.count());
		Long timestamp = System.currentTimeMillis();
		
		service.processSyncJmsMessage(jmsMsg);
		
		List<ReconciliationMessage> msgs = reconcileMsgRep.findAll();
		assertEquals(1, msgs.size());
		ReconciliationMessage msg = msgs.get(0);
		assertEquals(site, msg.getSite());
		assertEquals(table, msg.getTableName());
		assertEquals(batchSize, msg.getBatchSize().intValue());
		assertTrue(msg.isLastTableBatch());
		assertEquals(data, msg.getData());
		assertEquals(0, msg.getProcessedCount());
		assertTrue(msg.getDateCreated().getTime() == timestamp || msg.getDateCreated().getTime() > timestamp);
		assertEquals(0, jmsMsgRepo.count());
	}
	
	@Test
	public void updateReconciliationMessage_shouldProcessFoundUuidsAndUpdateTheProcessedCount() {
		final String uuid1 = "uuid-1";
		final String uuid2 = "uuid-2";
		ReconciliationMessage msg = new ReconciliationMessage();
		msg.setSite(siteRepo.getReferenceById(1L));
		msg.setTableName("");
		msg.setBatchSize(10);
		msg.setData("uuid1");
		msg.setDateCreated(new Date());
		reconcileMsgRep.save(msg);
		assertEquals(0, msg.getProcessedCount());
		
		service.updateReconciliationMessage(msg, true, List.of(uuid1, uuid2));
		
		assertEquals(2, msg.getProcessedCount());
	}
	
	@Test
	public void updateReconciliationMessage_shouldRequestForNotFoundUuidsAndUpdateTheProcessedCount() {
		assertEquals(0, requestRepo.count());
		final String uuid1 = "uuid-1";
		final String uuid2 = "uuid-2";
		final String table = "person";
		ReconciliationMessage msg = new ReconciliationMessage();
		final SiteInfo site = siteRepo.getReferenceById(1L);
		msg.setSite(site);
		msg.setTableName(table);
		msg.setBatchSize(10);
		msg.setData("uuid1");
		msg.setDateCreated(new Date());
		reconcileMsgRep.save(msg);
		assertEquals(0, msg.getProcessedCount());
		long timestamp = System.currentTimeMillis();
		
		service.updateReconciliationMessage(msg, false, List.of(uuid1, uuid2));
		
		assertEquals(2, msg.getProcessedCount());
		List<ReceiverSyncRequest> requests = requestRepo.findAll();
		assertEquals(2, requests.size());
		List<String> entityUuids = requests.stream().map(r -> r.getIdentifier()).collect(Collectors.toList());
		assertTrue(entityUuids.contains(uuid1));
		assertTrue(entityUuids.contains(uuid2));
		for (ReceiverSyncRequest r : requests) {
			assertEquals(ReceiverRequestStatus.NEW, r.getStatus());
			assertEquals(site, r.getSite());
			assertEquals(table, r.getTableName());
			assertTrue(r.getDateCreated().getTime() == timestamp || r.getDateCreated().getTime() > timestamp);
		}
	}
	
}
