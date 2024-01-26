package org.openmrs.eip.app.sender;

import static org.openmrs.eip.app.sender.SenderConstants.PROP_SENDER_ID;

import org.openmrs.eip.BaseDbBackedCamelTest;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@ActiveProfiles(SyncProfiles.SENDER)
@TestPropertySource(properties = PROP_SENDER_ID + "=")
@TestPropertySource(properties = SenderConstants.PROP_ACTIVEMQ_ENDPOINT + "=")
public abstract class BaseSenderTest extends BaseDbBackedCamelTest {}
