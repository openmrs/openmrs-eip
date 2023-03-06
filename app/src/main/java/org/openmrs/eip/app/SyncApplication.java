package org.openmrs.eip.app;

import java.security.Security;
import java.time.LocalDateTime;

import javax.annotation.PostConstruct;

import org.apache.camel.CamelContext;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.openmrs.eip.app.config.ActiveMqConfig;
import org.openmrs.eip.app.config.AppConfig;
import org.openmrs.eip.app.config.CamelConfig;
import org.openmrs.eip.app.config.JpaCamelConf;
import org.openmrs.eip.app.config.ManagementDataSourceConfig;
import org.openmrs.eip.app.config.OpenmrsDataSourceConfig;
import org.openmrs.eip.component.camel.StringToLocalDateTimeConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication(scanBasePackages = "org.openmrs.eip")
@Import({ AppConfig.class, ManagementDataSourceConfig.class, OpenmrsDataSourceConfig.class, JpaCamelConf.class,
        ActiveMqConfig.class, CamelConfig.class })
public class SyncApplication {
	
	private CamelContext camelContext;
	
	public SyncApplication(final CamelContext camelContext) {
		this.camelContext = camelContext;
	}
	
	public static void main(final String[] args) {
		SpringApplication.run(SyncApplication.class, args);
	}
	
	@PostConstruct
	private void addTypeConverter() {
		camelContext.getTypeConverterRegistry().addTypeConverter(LocalDateTime.class, String.class,
		    new StringToLocalDateTimeConverter());
	}
	
	@PostConstruct
	private void addBCProvider() {
		Security.addProvider(new BouncyCastleProvider());
	}
	
}
