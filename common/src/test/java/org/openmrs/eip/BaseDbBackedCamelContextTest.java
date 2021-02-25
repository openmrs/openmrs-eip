package org.openmrs.eip;

import static org.testcontainers.utility.DockerImageName.parse;

import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.openmrs.eip.app.management.config.ManagementDataSourceConfig;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.lifecycle.Startables;

import io.debezium.testing.testcontainers.DebeziumContainer;

/**
 * Base class for tests for routes that require access to the management and OpenMRS databases.
 */
@Import(ManagementDataSourceConfig.class)
public abstract class BaseDbBackedCamelContextTest extends BaseCamelContextTest {
	
	protected static Network network = Network.newNetwork();
	
	protected static MySQLContainer mysqlContainer = new MySQLContainer<>(parse("mysql:5.6")).withNetwork(network)
	        .withNetworkAliases("mysql");
	
	private static KafkaContainer kafkaContainer = new KafkaContainer(parse("confluentinc/cp-kafka:5.4.3"))
	        .withNetwork(network);
	
	protected static DebeziumContainer debeziumContainer = new DebeziumContainer("debezium/connect:1.5.0.Beta1")
	        .withNetwork(network).withKafka(kafkaContainer).dependsOn(kafkaContainer, mysqlContainer);
	
	@BeforeClass
	public static void startContainers() {
		Startables.deepStart(Stream.of(kafkaContainer, mysqlContainer, debeziumContainer)).join();
	}
	
}
