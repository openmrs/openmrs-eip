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

@Sql(scripts = "classpath:mgt_sender_sync_message.sql", config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
public class SenderDashboardControllerTest extends BaseSenderWebTest {
	
	@Test
	public void getSyncCountByStatus_shouldGetTheSyncCountByStatus() throws Exception {
		MockHttpServletRequestBuilder builder = get(SenderRestConstants.PATH_COUNT_BY_STATUS);
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(2)));
		result.andExpect(jsonPath("NEW", equalTo(3)));
		result.andExpect(jsonPath("SENT", equalTo(1)));
	}
	
}
