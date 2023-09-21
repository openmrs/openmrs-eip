package org.openmrs.eip.web;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestWebConfig.class)
@SpringJUnitWebConfig
@TestExecutionListeners(value = { DirtiesContextBeforeModesTestExecutionListener.class, MockitoTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        ResetMocksTestExecutionListener.class })
@TestPropertySource(properties = "logging.level.org.openmrs.eip=DEBUG")
public abstract class BaseWebTest {
	
	@Autowired
	private WebApplicationContext wac;
	
	protected MockMvc mockMvc;
	
	@Before
	public void beforeBaseWebTest() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}
	
}
