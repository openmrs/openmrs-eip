package org.openmrs.eip;

import org.openmrs.eip.component.SyncProfiles;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles(SyncProfiles.SENDER)
public abstract class BaseSenderTest extends BaseDbBackedCamelTest {}
