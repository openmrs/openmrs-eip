package org.openmrs.eip.web.sender;

import org.junit.Before;
import org.openmrs.eip.app.sender.BaseSenderTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public abstract class BaseSenderWebTest extends BaseSenderTest {
	
	@Autowired
	private WebApplicationContext wac;
	
	protected MockMvc mockMvc;
	
	@Before
	public void setupReceiverWebTest() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
}
