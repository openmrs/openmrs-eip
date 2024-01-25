package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.JMS_HEADER_MSG_ID;
import static org.openmrs.eip.app.SyncConstants.JMS_HEADER_SITE;
import static org.openmrs.eip.app.SyncConstants.JMS_HEADER_TYPE;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.management.entity.receiver.JmsMessage.MessageType.SYNC;

import java.util.Arrays;
import java.util.List;

import org.apache.activemq.command.ActiveMQBytesMessage;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import jakarta.jms.BytesMessage;

@Sql(scripts = {
        "classpath:mgt_site_info.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverMessageListenerTest extends BaseReceiverTest {
	
	@Autowired
	private ReceiverMessageListener listener;
	
	@Autowired
	private JmsMessageRepository repo;
	
	@Test
	public void onMessage_shouldAddTheJmsMessageToTheDb() throws Exception {
		assertEquals(0, repo.count());
		final String body = "{}";
		final String siteId = "remote1";
		final String msgId = "jms-msg-uuid";
		BytesMessage bytesMsg = new ActiveMQBytesMessage();
		bytesMsg.writeBytes(body.getBytes());
		bytesMsg.setStringProperty(JMS_HEADER_MSG_ID, msgId);
		bytesMsg.setStringProperty(JMS_HEADER_SITE, siteId);
		bytesMsg.setStringProperty(JMS_HEADER_TYPE, SYNC.name());
		
		listener.onMessage(bytesMsg);
		
		List<JmsMessage> msgs = repo.findAll();
		assertEquals(1, msgs.size());
		JmsMessage msg = msgs.get(0);
		assertTrue(Arrays.equals(body.getBytes(), msg.getBody()));
		assertEquals(msgId, msg.getMessageId());
		assertEquals(siteId, msg.getSiteId());
		assertEquals(SYNC, msg.getType());
	}
	
}
