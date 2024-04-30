package org.openmrs.eip.web.sender;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class SenderReconcileControllerTest extends BaseSenderWebTest {
	
	@Test
	@Sql(scripts = {
	        "classpath:mgt_sender_table_reconcile.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void getIncompleteTableReconciliations_shouldReturnTheIncompleteTableReconciliation() throws Exception {
		MockHttpServletRequestBuilder builder = get(SenderRestConstants.PATH_REC_TABLE_RECONCILE);
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(3)));
		result.andExpect(jsonPath("[0].id", equalTo(1)));
		result.andExpect(jsonPath("[1].id", equalTo(2)));
		result.andExpect(jsonPath("[2].id", equalTo(4)));
	}
	
}
