package org.openmrs.eip.app.sender;

import org.openmrs.eip.BaseDbBackedCamelTest;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles(SyncProfiles.SENDER)
@TestPropertySource(properties = "debezium.offsetFilename=./target/offset")
public abstract class BaseSenderTest extends BaseDbBackedCamelTest {}
