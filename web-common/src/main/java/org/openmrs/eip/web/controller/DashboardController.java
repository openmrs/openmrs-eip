package org.openmrs.eip.web.controller;

import static org.openmrs.eip.web.RestConstants.PARAM_ENTITY_CATEGORY;
import static org.openmrs.eip.web.RestConstants.PARAM_ENTITY_OPERATION;
import static org.openmrs.eip.web.RestConstants.PARAM_ENTITY_TYPE;

import java.util.List;

import org.openmrs.eip.component.SyncOperation;
import org.openmrs.eip.web.RestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(RestConstants.RES_DASHBOARD)
public class DashboardController {
	
	private static final Logger log = LoggerFactory.getLogger(DashboardController.class);
	
	private DashboardHelper helper;
	
	@Autowired
	public DashboardController(DashboardHelper helper) {
		this.helper = helper;
	}
	
	@GetMapping("/" + RestConstants.PATH_NAME_CATEGORY)
	public List<String> getCategories(@RequestParam(PARAM_ENTITY_TYPE) String entityType) {
		if (log.isDebugEnabled()) {
			log.debug("Getting categories for type: " + entityType);
		}
		
		return helper.getCategories(entityType);
	}
	
	@GetMapping("/" + RestConstants.PATH_NAME_COUNT)
	public Integer getCount(@RequestParam(PARAM_ENTITY_TYPE) String entityType,
	                        @RequestParam(value = PARAM_ENTITY_CATEGORY, required = false) String category,
	                        @RequestParam(value = PARAM_ENTITY_OPERATION, required = false) String operation) {
		
		if (log.isDebugEnabled()) {
			log.debug("Getting item count of type: " + entityType + ", category: " + category + ", operation: " + operation);
		}
		
		SyncOperation op = null;
		if (operation != null) {
			op = SyncOperation.valueOf(operation);
		}
		
		return helper.getCount(entityType, category, op);
	}
	
}
