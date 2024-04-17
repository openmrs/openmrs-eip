package org.openmrs.eip.web.receiver;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.web.RestConstants.PATH_RECEIVER_RECONCILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.management.repository.ReceiverReconcileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class ReconcileControllerTest extends BaseReceiverWebTest {
	
	@Autowired
	private ReceiverReconcileRepository reconcileRepo;
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_receiver_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getReconciliation_shouldGetTheReconciliation() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_RECONCILE);
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("identifier", CoreMatchers.equalTo("rec-2")));
	}
	
	@Test
	public void getReconciliation_shouldReturnIfThereIsNoIncompleteReconciliation() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_RECONCILE);
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		result.andExpect(content().string(CoreMatchers.is("")));
	}
	
	@Test
	public void startReconciliation_shouldAddANewReconciliation() throws Exception {
		Assert.assertNull(reconcileRepo.getReconciliation());
		MockHttpServletRequestBuilder builder = post(PATH_RECEIVER_RECONCILE);
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		ReceiverReconciliation rec = reconcileRepo.getReconciliation();
		result.andExpect(jsonPath("identifier", CoreMatchers.equalTo(rec.getIdentifier())));
	}
	
}
