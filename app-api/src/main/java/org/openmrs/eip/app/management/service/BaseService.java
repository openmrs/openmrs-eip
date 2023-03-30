package org.openmrs.eip.app.management.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for services
 */
@Transactional(readOnly = true)
public abstract class BaseService implements Service {
	
}
