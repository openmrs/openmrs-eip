package org.openmrs.eip.app.receiver.reconcile;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.SyncConstants.PROP_RECONCILE_MSG_BATCH_SIZE;
import static org.openmrs.eip.app.SyncConstants.RECONCILE_MSG_BATCH_SIZE;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.BasePureParallelQueueProcessor;
import org.openmrs.eip.app.management.entity.ReconciliationRequest;
import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation;
import org.openmrs.eip.app.management.entity.receiver.ReceiverReconciliation.ReconciliationStatus;
import org.openmrs.eip.app.management.entity.receiver.ReceiverTableReconciliation;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.openmrs.eip.app.management.repository.MissingEntityRepository;
import org.openmrs.eip.app.management.repository.ReceiverReconcileRepository;
import org.openmrs.eip.app.management.repository.ReceiverTableReconcileRepository;
import org.openmrs.eip.app.management.repository.SiteReconciliationRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.repository.UndeletedEntityRepository;
import org.openmrs.eip.app.receiver.ReceiverUtils;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

/**
 * Processes a receiver Reconciliation item
 */
@Component("receiverReconcileProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReceiverReconcileProcessor extends BasePureParallelQueueProcessor<ReceiverReconciliation> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReceiverReconcileProcessor.class);
	
	private SiteRepository siteRepo;
	
	private ReceiverReconcileRepository reconcileRepo;
	
	private SiteReconciliationRepository siteReconcileRepo;
	
	private ReceiverTableReconcileRepository tableReconcileRepo;
	
	private MissingEntityRepository missingRepo;
	
	private UndeletedEntityRepository unDeletedRepo;
	
	private JmsTemplate jmsTemplate;
	
	@Value("${" + PROP_RECONCILE_MSG_BATCH_SIZE + ":" + RECONCILE_MSG_BATCH_SIZE + "}")
	private int batchSize;
	
	public ReceiverReconcileProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    SiteRepository siteRepo, ReceiverReconcileRepository reconcileRepo, SiteReconciliationRepository siteReconcileRepo,
	    ReceiverTableReconcileRepository tableReconcileRepo, MissingEntityRepository missingRepo,
	    UndeletedEntityRepository unDeletedRepo, JmsTemplate jmsTemplate) {
		super(executor);
		this.siteRepo = siteRepo;
		this.reconcileRepo = reconcileRepo;
		this.siteReconcileRepo = siteReconcileRepo;
		this.tableReconcileRepo = tableReconcileRepo;
		this.missingRepo = missingRepo;
		this.unDeletedRepo = unDeletedRepo;
		this.jmsTemplate = jmsTemplate;
	}
	
	@Override
	public String getProcessorName() {
		return "reconcile";
	}
	
	@Override
	public String getQueueName() {
		return "reconcile";
	}
	
	@Override
	public String getThreadName(ReceiverReconciliation item) {
		return item.getId().toString();
	}
	
	@Override
	public void processItem(ReceiverReconciliation reconciliation) {
		if (reconciliation.getStatus() == ReconciliationStatus.NEW) {
			initialize(reconciliation);
		} else if (reconciliation.getStatus() == ReconciliationStatus.PROCESSING) {
			update(reconciliation);
		}
	}
	
	private void initialize(ReceiverReconciliation reconciliation) {
		missingRepo.deleteAll();
		unDeletedRepo.deleteAll();
		tableReconcileRepo.deleteAll();
		siteReconcileRepo.deleteAll();
		
		for (SiteInfo site : siteRepo.findAll()) {
			if (siteReconcileRepo.getBySite(site) == null) {
				ReconciliationRequest request = new ReconciliationRequest();
				request.setIdentifier(reconciliation.getIdentifier());
				request.setBatchSize(batchSize);
				final String json = JsonUtils.marshall(request);
				jmsTemplate.convertAndSend(ReceiverUtils.getSiteQueueName(site.getIdentifier()), json);
				SiteReconciliation siteRec = new SiteReconciliation();
				siteRec.setSite(site);
				siteRec.setDateCreated(new Date());
				siteReconcileRepo.save(siteRec);
			}
		}
		
		reconciliation.setStatus(ReconciliationStatus.PROCESSING);
		reconcileRepo.save(reconciliation);
	}
	
	private void update(ReceiverReconciliation reconciliation) {
		List<SiteInfo> sites = siteRepo.findAll();
		List<SiteInfo> incompleteSites = new ArrayList<>(sites.size());
		for (SiteInfo site : sites) {
			SiteReconciliation siteRec = siteReconcileRepo.getBySite(site);
			List<String> incompleteTables = AppUtils.getTablesToSync().stream().filter(table -> {
				ReceiverTableReconciliation tableRec = tableReconcileRepo.getBySiteReconciliationAndTableName(siteRec,
				    table);
				return tableRec == null || !tableRec.isCompleted();
			}).toList();
			
			if (incompleteTables.isEmpty()) {
				siteRec.setDateCompleted(LocalDateTime.now());
				if (LOG.isTraceEnabled()) {
					LOG.trace("Saving updates to completed reconciliation for site {}", site.getName());
				}
				
				siteReconcileRepo.save(siteRec);
			} else {
				if (LOG.isTraceEnabled()) {
					LOG.trace("Site {} still has {} incomplete table reconciliation(s)", site.getName(),
					    incompleteTables.size());
				}
				
				incompleteSites.add(site);
			}
		}
		
		if (incompleteSites.isEmpty()) {
			reconciliation.setStatus(ReconciliationStatus.FINALIZING);
			LOG.info("Updating reconciliation status to " + reconciliation.getStatus());
			if (LOG.isDebugEnabled()) {
				LOG.debug("Saving updated reconciliation");
			}
			
			reconcileRepo.save(reconciliation);
		} else {
			if (LOG.isTraceEnabled()) {
				LOG.trace("There is still {} incomplete site reconciliation(s)", incompleteSites.size());
			}
		}
	}
	
}
