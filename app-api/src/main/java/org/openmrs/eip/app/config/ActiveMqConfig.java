package org.openmrs.eip.app.config;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQSslConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.jms.connection.CachingConnectionFactory;

public class ActiveMqConfig {
	
	private static final long REDELIVERY_DELAY = 300000;
	
	@Value("${max.reconnect.delay:1800000}")
	private int maxReconnectDelay;
	
	@Value("${spring.artemis.host}")
	private String artemisHost;
	
	@Value("${spring.artemis.port}")
	private int artemisPort;
	
	@Value("${spring.artemis.user}")
	private String artemisUser;
	
	@Value("${spring.artemis.password}")
	private String artemisPassword;
	
	@Value("${artemis.ssl.enabled:false}")
	private boolean artemisSslEnabled;
	
	@Bean("activeMqConnFactory")
	public ConnectionFactory getConnectionFactory(Environment env) {
		ActiveMQConnectionFactory cf;
		if (artemisSslEnabled) {
			cf = new ActiveMQSslConnectionFactory();
		} else {
			cf = new org.apache.activemq.spring.ActiveMQConnectionFactory();
		}
		
		String url = (artemisSslEnabled ? "ssl" : "tcp") + "://" + artemisHost + ":" + artemisPort;
		String failoverUrl = "eip-failover:(" + url
		        + ")?initialReconnectDelay=60000&reconnectDelayExponent=5&maxReconnectDelay=" + maxReconnectDelay
		        + "&maxReconnectAttempts=-1&warnAfterReconnectAttempts=2";
		cf.setBrokerURL(failoverUrl);
		cf.setUserName(artemisUser);
		cf.setPassword(artemisPassword);
		final String clientId = env.getProperty("activemq.clientId");
		if (StringUtils.isNotBlank(clientId)) {
			cf.setClientID(clientId);
		}
		
		RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
		redeliveryPolicy.setMaximumRedeliveries(RedeliveryPolicy.NO_MAXIMUM_REDELIVERIES);
		redeliveryPolicy.setInitialRedeliveryDelay(REDELIVERY_DELAY);
		redeliveryPolicy.setRedeliveryDelay(REDELIVERY_DELAY);
		cf.setRedeliveryPolicy(redeliveryPolicy);
		
		return new CachingConnectionFactory(cf);
	}
	
}
