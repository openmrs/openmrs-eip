package org.openmrs.eip.web.receiver;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.openmrs.eip.app.SyncConstants.MGT_DATASOURCE_NAME;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.route.TestUtils.getEntity;
import static org.openmrs.eip.web.RestConstants.PARAM_END_DATE;
import static org.openmrs.eip.web.RestConstants.PARAM_GRP_PROP;
import static org.openmrs.eip.web.RestConstants.PARAM_START_DATE;
import static org.openmrs.eip.web.RestConstants.PATH_RECEIVER_ARCHIVE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.openmrs.eip.app.management.entity.SiteInfo;
import org.openmrs.eip.component.model.PatientModel;
import org.openmrs.eip.component.model.PersonModel;
import org.openmrs.eip.component.model.VisitModel;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class ReceiverSyncArchiveControllerTest extends BaseReceiverWebTest {
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_archive.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldGetAllArchives() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_ARCHIVE);
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(2)));
		result.andExpect(jsonPath("count", equalTo(3)));
		result.andExpect(jsonPath("items.length()", equalTo(3)));
		
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_archive.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldReturnArchivesMatchingTheSpecifiedStartAndEndDates() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_ARCHIVE);
		builder.param(PARAM_START_DATE, "2022-10-25");
		builder.param(PARAM_END_DATE, "2022-10-30");
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(2)));
		result.andExpect(jsonPath("count", equalTo(1)));
		result.andExpect(jsonPath("items.length()", equalTo(1)));
		result.andExpect(jsonPath("items[0].id", equalTo(3)));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_archive.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldReturnArchivesMatchingTheSpecifiedEndDate() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_ARCHIVE);
		builder.param(PARAM_START_DATE, "");
		builder.param(PARAM_END_DATE, "2022-10-23");
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(2)));
		result.andExpect(jsonPath("count", equalTo(2)));
		result.andExpect(jsonPath("items.length()", equalTo(2)));
		result.andExpect(jsonPath("items[0].id", equalTo(1)));
		result.andExpect(jsonPath("items[1].id", equalTo(2)));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_archive.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void shouldReturnArchivesMatchingTheSpecifiedStartDate() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_ARCHIVE);
		builder.param(PARAM_START_DATE, "2022-10-23");
		builder.param(PARAM_END_DATE, "");
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(2)));
		result.andExpect(jsonPath("count", equalTo(2)));
		result.andExpect(jsonPath("items.length()", equalTo(2)));
		result.andExpect(jsonPath("items[0].id", equalTo(2)));
		result.andExpect(jsonPath("items[1].id", equalTo(3)));
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_archive_web.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void get_shouldGetTheArchivesGroupedBySite() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_ARCHIVE);
		builder.param(PARAM_GRP_PROP, "site.name");
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(2)));
		result.andExpect(jsonPath("count", equalTo(4)));
		result.andExpect(jsonPath("items.length()", equalTo(2)));
		result.andExpect(jsonPath("items.['" + getEntity(SiteInfo.class, 1L).getName() + "']", equalTo(3)));
		result.andExpect(jsonPath("items.['" + getEntity(SiteInfo.class, 2L).getName() + "']", equalTo(1)));
		result.andExpect(jsonPath("items.['" + getEntity(SiteInfo.class, 3L).getName() + "']").doesNotHaveJsonPath());
	}
	
	@Test
	@Sql(scripts = { "classpath:mgt_site_info.sql",
	        "classpath:mgt_receiver_sync_archive_web.sql" }, config = @SqlConfig(dataSource = MGT_DATASOURCE_NAME, transactionManager = MGT_TX_MGR))
	public void get_shouldGetTheArchivesGroupedByEntity() throws Exception {
		MockHttpServletRequestBuilder builder = get(PATH_RECEIVER_ARCHIVE);
		builder.param(PARAM_GRP_PROP, "modelClassName");
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		result.andExpect(jsonPath("length()", equalTo(2)));
		result.andExpect(jsonPath("count", equalTo(4)));
		result.andExpect(jsonPath("items.length()", equalTo(2)));
		result.andExpect(jsonPath("items.['" + VisitModel.class.getName() + "']", equalTo(3)));
		result.andExpect(jsonPath("items.['" + PersonModel.class.getName() + "']", equalTo(1)));
		result.andExpect(jsonPath("items.['" + PatientModel.class.getName() + "']").doesNotHaveJsonPath());
	}
	
}
