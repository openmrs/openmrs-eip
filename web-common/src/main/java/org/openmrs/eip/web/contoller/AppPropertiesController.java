package org.openmrs.eip.web.contoller;

import java.util.Collections;
import java.util.Map;

import org.openmrs.eip.web.RestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.API_PATH + "/dbsync/properties")
public class AppPropertiesController {
	
	protected static final Logger log = LoggerFactory.getLogger(AppPropertiesController.class);
	
	@Autowired
	private Environment env;
	
	/**
	 * Gets the sync mode i.e. receiver vs sender
	 * 
	 * @return
	 */
	@GetMapping
	public Map<String, Object> getSyncMode() {
		return Collections.singletonMap("syncMode", env.getActiveProfiles()[0].toUpperCase());
	}
	
}
