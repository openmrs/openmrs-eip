package org.openmrs.eip.web.controller;

import static org.openmrs.eip.web.RestConstants.RES_DASHBOARD_GROUPS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.openmrs.eip.web.BaseWebTest;
import org.openmrs.eip.web.RestConstants;
import org.openmrs.eip.web.contoller.DashboardGenerator;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

public class DashboardControllerTest extends BaseWebTest {
	
	@Autowired
	private DashboardGenerator generator;
	
	private DashboardGenerator mockDelegateGenerator;
	
	@Before
	public void setup() {
		mockDelegateGenerator = Mockito.mock(DashboardGenerator.class);
		Whitebox.setInternalState(generator, DashboardGenerator.class, mockDelegateGenerator);
	}
	
	@After
	public void tearDown() {
		Whitebox.setInternalState(generator, DashboardGenerator.class, (Object) null);
	}
	
	@Test
	public void shouldGetTheDashboard() throws Exception {
		MockHttpServletRequestBuilder builder = get(RestConstants.RES_DASHBOARD);
		
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		Mockito.verify(mockDelegateGenerator).generate();
	}
	
	@Test
	public void shouldGetTheGroups() throws Exception {
		MockHttpServletRequestBuilder builder = get(RES_DASHBOARD_GROUPS);
		Mockito.when(mockDelegateGenerator.getGroups()).thenReturn(null);
		
		ResultActions result = mockMvc.perform(builder);
		result.andExpect(status().isOk());
		Mockito.verify(mockDelegateGenerator).getGroups();
	}
	
}
