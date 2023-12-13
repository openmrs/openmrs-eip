package org.openmrs.eip.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.component.SyncOperation.c;

import java.util.List;

import org.apache.camel.ProducerTemplate;
import org.junit.Before;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.SyncMessage;
import org.openmrs.eip.app.receiver.BaseReceiverTest;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.web.controller.DashboardHelper;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

@Import(TestWebConfig.class)
@Sql(scripts = { "classpath:mgt_site_info.sql",
        "classpath:mgt_receiver_sync_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class ReceiverBaseDashboardHelperTest extends BaseReceiverTest {
	
	private static final String ENTITY_TYPE = SyncMessage.class.getSimpleName();
	
	@Autowired
	private ProducerTemplate producerTemplate;
	
	@Autowired
	private DelegatingDashboardHelper helper;
	
	private TestDashboardHelper delegate;
	
	@Before
	public void setup() {
		delegate = new TestDashboardHelper(producerTemplate, true);
		Whitebox.setInternalState(helper, DashboardHelper.class, delegate);
	}
	
	@Test
	public void getCategories_shouldGetTheUniqueModelClassNamesInTheQueue() {
		List<String> modelNames = helper.getCategories(ENTITY_TYPE);
		assertEquals(2, modelNames.size());
		assertTrue(modelNames.contains(PersonModel.class.getName()));
		assertTrue(modelNames.contains(PatientModel.class.getName()));
	}
	
	@Test
	public void getCount_shouldGetTheTotalCountOfItemsTheQueue() {
		assertEquals(6, helper.getCount(ENTITY_TYPE, null, null).intValue());
	}
	
	@Test
	public void getCount_shouldGetTheQueueItemCountMatchingTheCategoryAndOperation() {
		assertEquals(4, helper.getCount(ENTITY_TYPE, PersonModel.class.getName(), c).intValue());
	}
	
}
