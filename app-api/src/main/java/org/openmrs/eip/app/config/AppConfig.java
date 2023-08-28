package org.openmrs.eip.app.config;

import static org.openmrs.eip.app.SyncConstants.BEAN_NAME_SYNC_EXECUTOR;
import static org.openmrs.eip.app.SyncConstants.MGT_TX_MGR;
import static org.openmrs.eip.app.SyncConstants.OPENMRS_TX_MGR;
import static org.openmrs.eip.app.SyncConstants.PROP_THREAD_NUMBER;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.openmrs.eip.app.SyncConstants;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

public class AppConfig {
	
	@Bean(BEAN_NAME_SYNC_EXECUTOR)
	public ThreadPoolExecutor getSyncExecutor(@Value("${" + PROP_THREAD_NUMBER + ":}") Integer threadCount) {
		if (threadCount == null) {
			threadCount = Runtime.getRuntime().availableProcessors();
		}
		
		return (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
	}
	
	@Bean(name = SyncConstants.CHAINED_TX_MGR)
	public PlatformTransactionManager transactionManager(@Qualifier(OPENMRS_TX_MGR) PlatformTransactionManager openmrsTxMgr,
	                                                     @Qualifier(MGT_TX_MGR) PlatformTransactionManager mgtTxMgr) {
		return new ChainedTransactionManager(openmrsTxMgr, mgtTxMgr);
	}
	
}
