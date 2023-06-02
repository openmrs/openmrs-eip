package org.openmrs.eip.app.management.service;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for services
 */
@Transactional(readOnly = true, transactionManager = MGT_TX_MGR)
public abstract class BaseService implements Service {
	
}
