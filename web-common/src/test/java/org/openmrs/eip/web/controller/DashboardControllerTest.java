package org.openmrs.eip.web.controller;

import static org.openmrs.eip.web.RestConstants.PATH_DASHBOARD_CATEGORY;
import static org.openmrs.eip.web.RestConstants.PATH_DASHBOARD_COUNT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.web.BaseWebTest;
import org.openmrs.eip.web.DelegatingDashboardGenerator;
import org.openmrs.eip.web.RestConstants;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class DashboardControllerTest extends BaseWebTest {
	
	@Autowired
	private DelegatingDashboardGenerator generator;
	
	private DashboardGenerator mockDelegate;
	
	@Before
	public void setup() {
		mockDelegate = Mockito.mock(DashboardGenerator.class);
		Whitebox.setInternalState(generator, DashboardGenerator.class, mockDelegate);
	}
	
	@After
	public void tearDown() {
		Whitebox.setInternalState(generator, DashboardGenerator.class, (Object) null);
	}
	
	@Test
	public void getCategories_shouldGetTheListOfCategoryNames() throws Exception {
		final String entityType = "MyEntity";
		MockHttpServletRequestBuilder builder = get(PATH_DASHBOARD_CATEGORY);
		builder.param(RestConstants.PARAM_ENTITY_TYPE, entityType);
		Mockito.when(mockDelegate.getCategories(entityType)).thenReturn(null);
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		Mockito.verify(mockDelegate).getCategories(entityType);
	}
	
	@Test
	public void getCount_shouldGetTheTotalCountOfItemsTheQueue() throws Exception {
		final String entityType = "MyEntity";
		MockHttpServletRequestBuilder builder = get(PATH_DASHBOARD_COUNT);
		builder.param(RestConstants.PARAM_ENTITY_TYPE, entityType);
		Mockito.when(mockDelegate.getCount(entityType, null, null)).thenReturn(null);
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		Mockito.verify(mockDelegate).getCount(entityType, null, null);
	}
	
	@Test
	public void getCount_shouldGetTheQueueItemCountMatchingTheCategoryAndOperation() throws Exception {
		final String entityType = "MyEntity";
		final String category = "MyCategory";
		MockHttpServletRequestBuilder builder = get(PATH_DASHBOARD_COUNT);
		builder.param(RestConstants.PARAM_ENTITY_TYPE, entityType);
		builder.param(RestConstants.PARAM_ENTITY_CATEGORY, category);
		builder.param(RestConstants.PARAM_ENTITY_OPERATION, SyncOperation.u.name());
		Mockito.when(mockDelegate.getCount(entityType, category, SyncOperation.u)).thenReturn(null);
		
		ResultActions result = mockMvc.perform(builder);
		
		result.andExpect(status().isOk());
		Mockito.verify(mockDelegate).getCount(entityType, category, SyncOperation.u);
	}
	
}
