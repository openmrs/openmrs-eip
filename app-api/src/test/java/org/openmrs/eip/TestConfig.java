package org.openmrs.eip;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration(exclude = { LiquibaseAutoConfiguration.class })
@ComponentScan
public class TestConfig {}
