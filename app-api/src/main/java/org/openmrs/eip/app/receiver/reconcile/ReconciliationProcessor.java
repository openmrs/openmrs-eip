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
import org.openmrs.eip.app.management.entity.receiver.Reconciliation;
import org.openmrs.eip.app.management.entity.receiver.Reconciliation.ReconciliationStatus;
import org.openmrs.eip.app.management.entity.receiver.SiteInfo;
import org.openmrs.eip.app.management.entity.receiver.SiteReconciliation;
import org.openmrs.eip.app.management.entity.receiver.TableReconciliation;
import org.openmrs.eip.app.management.repository.ReconciliationRepository;
import org.openmrs.eip.app.management.repository.SiteReconciliationRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.repository.TableReconciliationRepository;
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
 * Processes a Reconciliation item
 */
@Component("reconciliationProcessor")
@Profile(SyncProfiles.RECEIVER)
public class ReconciliationProcessor extends BasePureParallelQueueProcessor<Reconciliation> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReconciliationProcessor.class);
	
	private SiteRepository siteRepo;
	
	private ReconciliationRepository reconcileRepo;
	
	private SiteReconciliationRepository siteReconcileRepo;
	
	private TableReconciliationRepository tableReconcileRepo;
	
	private JmsTemplate jmsTemplate;
	
	@Value("${" + PROP_RECONCILE_MSG_BATCH_SIZE + ":" + RECONCILE_MSG_BATCH_SIZE + "}")
	private int batchSize;
	
	public ReconciliationProcessor(@Qualifier(BEAN_NAME_SYNC_EXECUTOR) ThreadPoolExecutor executor, SiteRepository siteRepo,
	    ReconciliationRepository reconcileRepo, SiteReconciliationRepository siteReconcileRepo,
	    TableReconciliationRepository tableReconcileRepo, JmsTemplate jmsTemplate) {
		super(executor);
		this.siteRepo = siteRepo;
		this.reconcileRepo = reconcileRepo;
		this.siteReconcileRepo = siteReconcileRepo;
		this.tableReconcileRepo = tableReconcileRepo;
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
	public String getThreadName(Reconciliation item) {
		return item.getId().toString();
	}
	
	@Override
	public void processItem(Reconciliation reconciliation) {
		if (reconciliation.getStatus() == ReconciliationStatus.NEW) {
			initialize(reconciliation);
		} else if (reconciliation.getStatus() == ReconciliationStatus.PROCESSING) {
			update(reconciliation);
		}
	}
	
	private void initialize(Reconciliation reconciliation) {
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
	
	private void update(Reconciliation reconciliation) {
		List<SiteInfo> sites = siteRepo.findAll();
		List<SiteInfo> incompleteSites = new ArrayList<>(sites.size());
		for (SiteInfo site : sites) {
			SiteReconciliation siteRec = siteReconcileRepo.getBySite(site);
			List<String> incompleteTables = AppUtils.getTablesToSync().stream().filter(table -> {
				TableReconciliation tableRec = tableReconcileRepo.getBySiteReconciliationAndTableName(siteRec, table);
				return tableRec == null || !tableRec.isCompleted();
			}).toList();
			
			if (incompleteTables.isEmpty()) {
				siteRec.setDateCompleted(LocalDateTime.now());
				if (LOG.isTraceEnabled()) {
					LOG.trace("Saving updates to completed site reconciliation");
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
			LOG.info("Updating reconciliation status to " + ReconciliationStatus.FINALIZING);
			reconciliation.setStatus(ReconciliationStatus.FINALIZING);
			if (LOG.isDebugEnabled()) {
				LOG.info("Saving updated reconciliation");
			}
			
			reconcileRepo.save(reconciliation);
		} else {
			if (LOG.isTraceEnabled()) {
				LOG.trace("There are still {} incomplete sites reconciliation(s)", incompleteSites.size());
			}
		}
	}
	
}
