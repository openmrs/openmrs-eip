package org.openmrs.eip.app.receiver;

import org.openmrs.eip.BaseDbBackedCamelTest;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles(SyncProfiles.RECEIVER)
@TestPropertySource(properties = ReceiverConstants.PROP_CAMEL_OUTPUT_ENDPOINT + "=")
@TestPropertySource(properties = ReceiverConstants.PROP_SYNC_QUEUE + "=")
@TestPropertySource(properties = "openmrs.baseUrl=test")
@TestPropertySource(properties = "openmrs.username=")
@TestPropertySource(properties = "openmrs.password=test")
public abstract class BaseReceiverTest extends BaseDbBackedCamelTest {}
