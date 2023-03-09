package org.openmrs.eip.app.receiver;

import org.openmrs.eip.BaseDbBackedCamelTest;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles(SyncProfiles.RECEIVER)
@TestPropertySource(properties = ReceiverConstants.PROP_CAMEL_OUTPUT_ENDPOINT + "=")
public abstract class BaseReceiverTest extends BaseDbBackedCamelTest {}
