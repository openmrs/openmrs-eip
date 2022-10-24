package org.openmrs.eip;

import java.util.stream.Stream;

import javax.sql.DataSource;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.openmrs.eip.component.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockitoTestExecutionListener;
import org.springframework.boot.test.mock.mockito.ResetMocksTestExecutionListener;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.lifecycle.Startables;

@RunWith(CamelSpringRunner.class)
@SpringBootTest(classes = { TestConfig.class })
@Transactional
@TestExecutionListeners({ DirtiesContextBeforeModesTestExecutionListener.class, MockitoTestExecutionListener.class,
        DependencyInjectionTestExecutionListener.class, DirtiesContextTestExecutionListener.class,
        ResetMocksTestExecutionListener.class, DeleteDataTestExecutionListener.class, SqlScriptsTestExecutionListener.class,
        TransactionalTestExecutionListener.class })
@TestPropertySource(properties = { "spring.jpa.properties.hibernate.hbm2ddl.auto=update" })
public abstract class BaseDbDrivenTest {
	
	private static final Logger log = LoggerFactory.getLogger(BaseDbDrivenTest.class);
	
	protected static MySQLContainer mysqlContainer = new MySQLContainer("mysql:5.7.31");
	
	@Autowired
	protected ApplicationContext applicationContext;
	
	@Autowired
	protected CamelContext camelContext;
	
	@Autowired
	@Qualifier(Constants.OPENMRS_DATASOURCE_NAME)
	protected DataSource openmrsDataSource;
	
	@Autowired
	protected ProducerTemplate producerTemplate;
	
	@BeforeClass
	public static void startContainers() {
		mysqlContainer.withEnv("MYSQL_ROOT_PASSWORD", "test");
		mysqlContainer.withDatabaseName("openmrs");
		Startables.deepStart(Stream.of(mysqlContainer)).join();
	}
	
}
