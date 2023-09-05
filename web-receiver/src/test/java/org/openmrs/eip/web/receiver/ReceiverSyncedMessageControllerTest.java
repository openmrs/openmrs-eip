package org.openmrs.eip.web.receiver;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.route.TestUtils.getEntity;
import static org.openmrs.eip.web.RestConstants.PARAM_GRP_PROP;
import static org.openmrs.eip.web.RestConstants.PATH_RECEIVER_SYNCED_MSG;
import static org.openmrs.eip.web.RestConstants.PATH_VAR_ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.VisitModel;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

public class ReceiverSyncedMessageControllerTest extends BaseReceiverWebTest {
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_synced_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void get_shouldGetAllSyncedMessages() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_SYNCED_MSG);
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(2)));
		result.andExpect(jsonPath("count", equalTo(35)));
		result.andExpect(jsonPath("items.length()", equalTo(35)));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_synced_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void get_shouldGetTheSyncedMessageMatchingTheSpecifiedId() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_SYNCED_MSG + "/{" + PATH_VAR_ID + "}", 2L);
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("messageUuid", equalTo("47beb8bd-287c-47f2-9786-a7b98c933c05")));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_synced_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void get_shouldGetTheSyncedMessagesGroupedBySite() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_SYNCED_MSG);
		builder.param(PARAM_GRP_PROP, "site.name");
		ResultActions result = mockMvc.perform(builder);
		result.andDo(MockMvcResultHandlers.print());
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(2)));
		result.andExpect(jsonPath("count", equalTo(35)));
		result.andExpect(jsonPath("items.length()", equalTo(5)));
		result.andExpect(jsonPath("items.['" + getEntity(SiteInfo.class, 1L).getName() + "']", equalTo(4)));
		result.andExpect(jsonPath("items.['" + getEntity(SiteInfo.class, 2L).getName() + "']", equalTo(4)));
		result.andExpect(jsonPath("items.['" + getEntity(SiteInfo.class, 3L).getName() + "']", equalTo(7)));
		result.andExpect(jsonPath("items.['" + getEntity(SiteInfo.class, 4L).getName() + "']", equalTo(9)));
		result.andExpect(jsonPath("items.['" + getEntity(SiteInfo.class, 5L).getName() + "']", equalTo(11)));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_synced_msg.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void get_shouldGetTheSyncedMessagesGroupedByEntity() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_SYNCED_MSG);
		builder.param(PARAM_GRP_PROP, "modelClassName");
		ResultActions result = mockMvc.perform(builder);
		result.andDo(MockMvcResultHandlers.print());
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(2)));
		result.andExpect(jsonPath("count", equalTo(35)));
		result.andExpect(jsonPath("items.length()", equalTo(2)));
		result.andExpect(jsonPath("items.['" + PersonModel.class.getName() + "']", equalTo(5)));
		result.andExpect(jsonPath("items.['" + PatientModel.class.getName() + "']", equalTo(30)));
		result.andExpect(jsonPath("items.['" + VisitModel.class.getName() + "']").doesNotHaveJsonPath());
	}
	
}
