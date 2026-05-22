package org.openmrs.eip.mysql.watcher.config;

import static org.openmrs.eip.mysql.watcher.WatcherConstants.PROP_LEADER_ELECTION_ENABLED;

import javax.sql.DataSource;

import org.openmrs.eip.Constants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.jdbc.lock.DefaultLockRepository;
import org.springframework.integration.jdbc.lock.JdbcLockRegistry;
import org.springframework.integration.jdbc.lock.LockRepository;
import org.springframework.integration.support.leader.LockRegistryLeaderInitiator;

@Configuration
@ConditionalOnProperty(name = PROP_LEADER_ELECTION_ENABLED, havingValue = "true")
public class LeaderElectionConfig {
	
	@Bean
	public LockRepository lockRepository(@Qualifier(Constants.MGT_DATASOURCE_NAME) DataSource dataSource) {
		return new DefaultLockRepository(dataSource);
	}
	
	@Bean
	public JdbcLockRegistry lockRegistry(LockRepository lockRepository) {
		return new JdbcLockRegistry(lockRepository);
	}
	
	@Bean
	public LockRegistryLeaderInitiator leaderInitiator(JdbcLockRegistry lockRegistry) {
		return new LockRegistryLeaderInitiator(lockRegistry);
	}
	
}
