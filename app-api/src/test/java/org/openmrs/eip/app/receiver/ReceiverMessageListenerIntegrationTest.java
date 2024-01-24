package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.JMS_HEADER_SITE;
import static org.openmrs.eip.app.SyncConstants.JMS_HEADER_TYPE;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.management.entity.receiver.BaseJmsMessage.MessageType.RECONCILIATION;
import static org.openmrs.eip.app.management.entity.receiver.BaseJmsMessage.MessageType.SYNC;

import java.util.Arrays;
import java.util.List;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.BytesJmsMessage;
import org.openmrs.eip.app.management.entity.receiver.TextJmsMessage;
import org.openmrs.eip.app.management.repository.BytesMessageRepository;
import org.openmrs.eip.app.management.repository.TextMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import jakarta.jms.BytesMessage;
import jakarta.jms.TextMessage;

@Sql(scripts = {
        "classpath:mgt_site_info.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverMessageListenerIntegrationTest extends BaseReceiverTest {
	
	@Autowired
	private ReceiverMessageListener listener;
	
	@Autowired
	private TextMessageRepository txtMsgRepo;
	
	@Autowired
	private BytesMessageRepository bytesMsgRepo;
	
	@Test
	public void onMessage_shouldAddTheTextMessageToTheDb() throws Exception {
		assertEquals(0, txtMsgRepo.count());
		final String body = "{}";
		final String siteId = "remote1";
		TextMessage textMsg = new ActiveMQTextMessage();
		textMsg.setText(body);
		textMsg.setStringProperty(JMS_HEADER_SITE, siteId);
		textMsg.setStringProperty(JMS_HEADER_TYPE, SYNC.name());
		
		listener.onMessage(textMsg);
		
		List<TextJmsMessage> msgs = txtMsgRepo.findAll();
		assertEquals(1, msgs.size());
		TextJmsMessage msg = msgs.get(0);
		assertEquals(body, msg.getBody());
		assertEquals(siteId, msg.getSiteId());
		assertEquals(SYNC, msg.getType());
	}
	
	@Test
	public void onMessage_shouldAddTheBytesMessageToTheDb() throws Exception {
		assertEquals(0, bytesMsgRepo.count());
		final String body = "{}";
		final String siteId = "remote2";
		BytesMessage bytesMsg = new ActiveMQBytesMessage();
		bytesMsg.writeBytes(body.getBytes());
		bytesMsg.setStringProperty(JMS_HEADER_SITE, siteId);
		bytesMsg.setStringProperty(JMS_HEADER_TYPE, RECONCILIATION.name());
		
		listener.onMessage(bytesMsg);
		
		List<BytesJmsMessage> msgs = bytesMsgRepo.findAll();
		assertEquals(1, msgs.size());
		BytesJmsMessage msg = msgs.get(0);
		assertTrue(Arrays.equals(body.getBytes(), msg.getBody()));
		assertEquals(siteId, msg.getSiteId());
		assertEquals(RECONCILIATION, msg.getType());
	}
	
}
