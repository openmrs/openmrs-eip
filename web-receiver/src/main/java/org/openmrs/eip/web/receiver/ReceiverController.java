package org.openmrs.eip.web.receiver;

import java.util.Collection;

import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.receiver.ReceiverContext;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.web.RestConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Profile(SyncProfiles.RECEIVER)
@RequestMapping(RestConstants.SUB_PATH_RECEIVER)
public class ReceiverController {
	
	private static final Logger log = LoggerFactory.getLogger(ReceiverController.class);
	
	@GetMapping(RestConstants.RES_SITE)
	public Collection<SiteInfo> getSites() {
		if (log.isDebugEnabled()) {
			log.debug("Fetching sites");
		}
		
		return ReceiverContext.getSites();
	}
	
}
