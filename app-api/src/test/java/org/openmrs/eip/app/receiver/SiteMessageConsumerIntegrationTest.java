package org.openmrs.eip.app.receiver;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.camel.ProducerTemplate;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.app.management.entity.SyncMessage;
import org.openmrs.eip.app.route.TestUtils;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SiteMessageConsumerIntegrationTest extends BaseReceiverTest {
	
	@Autowired
	private ProducerTemplate producerTemplate;
	
	@Autowired
	@Qualifier(BEAN_NAME_SYNC_EXECUTOR)
	private ThreadPoolExecutor syncExecutor;
	
	@Test
	public void fetchNextSyncMessageBatch_shouldGetOnlyNewMessages() throws Exception {
		SiteInfo site = TestUtils.getEntity(SiteInfo.class, 1L);
		SiteMessageConsumer consumer = new SiteMessageConsumer(null, site, syncExecutor);
		Whitebox.setInternalState(consumer, ProducerTemplate.class, producerTemplate);
		
		List<SyncMessage> syncMessages = consumer.fetchNextSyncMessageBatch();
		
		assertEquals(3, syncMessages.size());
		assertEquals(1l, syncMessages.get(0).getId().longValue());
		assertEquals(2l, syncMessages.get(1).getId().longValue());
		assertEquals(3l, syncMessages.get(2).getId().longValue());
	}
	
}
