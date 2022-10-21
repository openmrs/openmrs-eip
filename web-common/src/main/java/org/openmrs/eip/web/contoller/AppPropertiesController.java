package org.openmrs.eip.web.contoller;

import java.util.HashMap;
import java.util.Map;

import org.openmrs.eip.app.AppUtils;
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
	 * Gets the application properties
	 * 
	 * @return
	 */
	@GetMapping
	public Map<String, Object> getAppProperties() {
		HashMap<String, Object> properties = new HashMap();
		properties.put("version", AppUtils.getVersion());
		properties.put("syncMode", env.getActiveProfiles()[0].toUpperCase());
		return properties;
	}
	
}
