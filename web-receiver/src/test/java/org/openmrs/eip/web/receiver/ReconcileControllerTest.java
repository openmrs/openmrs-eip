package org.openmrs.eip.web.receiver;

import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.web.RestConstants.PATH_RECEIVER_RECONCILE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class ReconcileControllerTest extends BaseReceiverWebTest {
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_receiver_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldGetTheReconciliation() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_RECONCILE);
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("identifier", CoreMatchers.equalTo("rec-2")));
	}
	
}
