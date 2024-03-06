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
import org.openmrs.eip.app.management.entity.receiver.ReconcileTableSummary;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.openmrs.eip.app.management.repository.MissingEntityRepository;
import org.openmrs.eip.app.management.repository.ReceiverReconcileRepository;
import org.openmrs.eip.app.management.repository.ReceiverTableReconcileRepository;
import org.openmrs.eip.app.management.repository.ReconcileTableSummaryRepository;
import org.openmrs.eip.app.management.repository.SiteReconciliationRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.repository.UndeletedEntityRepository;
import org.openmrs.eip.app.receiver.ReceiverUtils;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.exception.EIPException;
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
	
	private ReconcileTableSummaryRepository summaryRepo;
	
	private JmsTemplate jmsTemplate;
	
	@Value("${" + PROP_RECONCILE_MSG_BATCH_SIZE + ":" + RECONCILE_MSG_BATCH_SIZE + "}")
	private int batchSize;
	
	public ReceiverReconcileProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor,
	    SiteRepository siteRepo, ReceiverReconcileRepository reconcileRepo, SiteReconciliationRepository siteReconcileRepo,
	    ReceiverTableReconcileRepository tableReconcileRepo, MissingEntityRepository missingRepo,
	    UndeletedEntityRepository unDeletedRepo, ReconcileTableSummaryRepository summaryRepo, JmsTemplate jmsTemplate) {
		super(executor);
		this.siteRepo = siteRepo;
		this.reconcileRepo = reconcileRepo;
		this.siteReconcileRepo = siteReconcileRepo;
		this.tableReconcileRepo = tableReconcileRepo;
		this.missingRepo = missingRepo;
		this.unDeletedRepo = unDeletedRepo;
		this.summaryRepo = summaryRepo;
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
		switch (reconciliation.getStatus()) {
			case NEW:
				initialize(reconciliation);
				break;
			case PROCESSING:
				process(reconciliation);
				break;
			case POST_PROCESSING:
				postProcess(reconciliation);
				break;
			case COMPLETED:
				throw new EIPException("Reconciliation is already completed");
		}
	}
	
	private void initialize(ReceiverReconciliation reconciliation) {
		LOG.info("Initializing reconciliation {}", reconciliation.getIdentifier());
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
		
		updateStatus(reconciliation, ReconciliationStatus.PROCESSING);
		LOG.info("Successfully initialized reconciliation {}", reconciliation.getIdentifier());
	}
	
	private void process(ReceiverReconciliation reconciliation) {
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
			updateStatus(reconciliation, ReconciliationStatus.POST_PROCESSING);
		} else {
			if (LOG.isTraceEnabled()) {
				LOG.trace("There is still {} incomplete site reconciliation(s)", incompleteSites.size());
			}
		}
	}
	
	private void postProcess(ReceiverReconciliation rec) {
		LOG.info("Generating reconciliation report");
		List<SiteInfo> sites = siteRepo.findAll();
		sites.forEach(site -> {
			AppUtils.getTablesToSync().forEach(t -> {
				ReconcileTableSummary s = new ReconcileTableSummary();
				s.setReconciliation(rec);
				s.setSite(site);
				s.setTableName(t);
				s.setMissingCount(missingRepo.countBySiteAndTableNameIgnoreCase(site, t));
				s.setMissingSyncCount(missingRepo.countBySiteAndTableNameIgnoreCaseAndInSyncQueueTrue(site, t));
				s.setMissingErrorCount(missingRepo.countBySiteAndTableNameIgnoreCaseAndInErrorQueueTrue(site, t));
				s.setUndeletedCount(unDeletedRepo.countBySiteAndTableNameIgnoreCase(site, t));
				s.setUndeletedSyncCount(unDeletedRepo.countBySiteAndTableNameIgnoreCaseAndInSyncQueueTrue(site, t));
				s.setUndeletedErrorCount(unDeletedRepo.countBySiteAndTableNameIgnoreCaseAndInErrorQueueTrue(site, t));
				s.setDateCreated(new Date());
				if (LOG.isDebugEnabled()) {
					LOG.debug("Saving reconciliation summary for site {}, table {}", site.getName(), t);
				}
				
				summaryRepo.save(s);
			});
		});
		
		updateStatus(rec, ReconciliationStatus.COMPLETED);
		LOG.info("Successfully generated reconciliation report");
	}
	
	private void updateStatus(ReceiverReconciliation rec, ReconciliationStatus newStatus) {
		rec.setStatus(newStatus);
		LOG.info("Updating reconciliation status to " + rec.getStatus());
		if (LOG.isDebugEnabled()) {
			LOG.debug("Saving updated reconciliation");
		}
		
		reconcileRepo.save(rec);
	}
	
}
