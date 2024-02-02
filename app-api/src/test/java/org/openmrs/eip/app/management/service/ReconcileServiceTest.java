package org.openmrs.eip.app.management.service;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.openmrs.eip.app.management.repository.ReconciliationMessageRepository;
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
	private ReconciliationMessageRepository reconcileMsgRep;
	
	@Autowired
	private JmsMessageRepository jmsMsgRepo;
	
	@Autowired
	private SiteRepository siteRepo;
	
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
	
}
