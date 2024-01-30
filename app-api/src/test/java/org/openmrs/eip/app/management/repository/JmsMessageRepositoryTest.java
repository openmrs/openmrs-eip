package org.openmrs.eip.app.management.repository;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_jms_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class JmsMessageRepositoryTest extends BaseReceiverTest {
	
	@Autowired
	private JmsMessageRepository repo;
	
	@Test
	public void existsByMessageId_shouldReturnTrueIfAMessageWithTheSameIdExists() {
		Assert.assertTrue(repo.existsByMessageId("1cef940e-32dc-491f-8038-a8f3afe3e37d"));
	}
	
	@Test
	public void existsByMessageId_shouldReturnFalseIfNoMessageWithTheSameIdExists() {
		Assert.assertFalse(repo.existsByMessageId("some-uuid"));
	}
	
	@Test
	public void getAllOrderByDateCreatedAsc_shouldGetJmsMessages() {
		List<JmsMessage> msgs = repo.findAllByOrderByDateCreatedAsc(Pageable.ofSize(5));
		assertEquals(3, msgs.size());
		assertEquals(3, msgs.get(0).getId().longValue());
		assertEquals(1, msgs.get(1).getId().longValue());
		assertEquals(2, msgs.get(2).getId().longValue());
		
		msgs = repo.findAllByOrderByDateCreatedAsc(Pageable.ofSize(2));
		assertEquals(2, msgs.size());
		assertEquals(3, msgs.get(0).getId().longValue());
		assertEquals(1, msgs.get(1).getId().longValue());
	}
	
}
