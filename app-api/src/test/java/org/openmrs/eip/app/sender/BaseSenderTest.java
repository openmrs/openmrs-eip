package org.openmrs.eip.app.sender;

import org.openmrs.eip.BaseDbBackedCamelTest;
import org.openmrs.eip.component.SyncProfiles;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(SyncProfiles.SENDER)
public abstract class BaseSenderTest extends BaseDbBackedCamelTest {}
