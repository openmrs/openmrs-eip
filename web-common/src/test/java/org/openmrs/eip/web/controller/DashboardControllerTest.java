package org.openmrs.eip.web.controller;

import static org.openmrs.eip.web.RestConstants.RES_DASHBOARD_CATEGORIES;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
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
	public void getDashboard_shouldGetTheDashboard() throws Exception {
		MockHttpServletRequestBuilder builder = get(RestConstants.RES_DASHBOARD);
		
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		Mockito.verify(mockDelegate).generate();
	}
	
	@Test
	public void getCategories_shouldGetTheCategories() throws Exception {
		final String entityName = "MyEntity";
		MockHttpServletRequestBuilder builder = get(RES_DASHBOARD_CATEGORIES);
		builder.param(RestConstants.PARAM_ENTITY_NAME, entityName);
		Mockito.when(mockDelegate.getCategories(entityName)).thenReturn(null);
		
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		Mockito.verify(mockDelegate).getCategories(entityName);
	}
	
}
