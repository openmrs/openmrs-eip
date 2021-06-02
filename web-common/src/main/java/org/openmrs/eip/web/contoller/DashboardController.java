package org.openmrs.eip.web.contoller;

import org.openmrs.eip.web.Dashboard;
import org.openmrs.eip.web.RestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.API_PATH + "/dbsync/dashboard")
public class DashboardController {
	
	private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
	
	private DashboardGenerator generator;
	
	@Autowired
	public DashboardController(DashboardGenerator generator) {
		this.generator = generator;
	}
	
	@GetMapping
	public Dashboard getDashboard() {
		if (log.isDebugEnabled()) {
			log.debug("Fetching dashboard");
		}
		
		return generator.generate();
	}
	
}
