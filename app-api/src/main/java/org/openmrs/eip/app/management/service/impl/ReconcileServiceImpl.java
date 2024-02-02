package org.openmrs.eip.app.management.service.impl;

import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;

import java.util.Date;

import org.openmrs.eip.app.management.entity.ReconciliationResponse;
import org.openmrs.eip.app.management.entity.receiver.JmsMessage;
import org.openmrs.eip.app.management.entity.receiver.ReconciliationMessage;
import org.openmrs.eip.app.management.repository.JmsMessageRepository;
import org.openmrs.eip.app.management.repository.ReconciliationMessageRepository;
import org.openmrs.eip.app.management.repository.SiteRepository;
import org.openmrs.eip.app.management.service.BaseService;
import org.openmrs.eip.app.management.service.ReconcileService;
import org.openmrs.eip.component.SyncProfiles;
import org.openmrs.eip.component.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("reconcileService")
@Profile(SyncProfiles.RECEIVER)
public class ReconcileServiceImpl extends BaseService implements ReconcileService {
	
	private static final Logger LOG = LoggerFactory.getLogger(ReconcileServiceImpl.class);
	
	private SiteRepository siteRepo;
	
	private ReconciliationMessageRepository reconcileMsgRep;
	
	private JmsMessageRepository jmsMsgRepo;
	
	public ReconcileServiceImpl(SiteRepository siteRepo, ReconciliationMessageRepository reconcileMsgRep,
	    JmsMessageRepository jmsMsgRepo) {
		this.siteRepo = siteRepo;
		this.reconcileMsgRep = reconcileMsgRep;
		this.jmsMsgRepo = jmsMsgRepo;
	}
	
	@Override
	@Transactional(transactionManager = MGT_TX_MGR)
	public void processSyncJmsMessage(JmsMessage jmsMessage) {
		ReconciliationMessage msg = new ReconciliationMessage();
		msg.setSite(siteRepo.getByIdentifier(jmsMessage.getSiteId()));
		ReconciliationResponse resp = JsonUtils.unmarshalBytes(jmsMessage.getBody(), ReconciliationResponse.class);
		msg.setTableName(resp.getTableName());
		msg.setBatchSize(resp.getBatchSize());
		msg.setLastTableBatch(resp.isLastTableBatch());
		msg.setData(resp.getData());
		msg.setDateCreated(new Date());
		if (LOG.isDebugEnabled()) {
			LOG.debug("Saving reconciliation message");
		}
		
		reconcileMsgRep.save(msg);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Removing reconciliation message");
		}
		
		jmsMsgRepo.delete(jmsMessage);
	}
	
}
