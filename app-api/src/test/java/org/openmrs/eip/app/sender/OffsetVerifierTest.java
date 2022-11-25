package org.openmrs.eip.app.sender;

import static com.mysql.cj.exceptions.MysqlErrorNumbers.ER_MASTER_FATAL_ERROR_READING_BINLOG;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.sender.OffsetVerifier.OffsetVerificationResult;
import org.openmrs.eip.component.exception.EIPException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.network.ServerException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SenderUtils.class)
public class OffsetVerifierTest {
	
	@Mock
	private BinaryLogClient mockClient;
	
	private OffsetVerifier verifier;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SenderUtils.class);
		verifier = new OffsetVerifier(null);
		Whitebox.setInternalState(verifier, BinaryLogClient.class, mockClient);
	}
	
	private OffsetVerificationResult getResult() {
		return Whitebox.getInternalState(verifier, OffsetVerificationResult.class);
	}
	
	@Test
	public void verify_shouldConnectTheBinLogClientToStartTheVerification() throws Exception {
		verifier.verify();
		Mockito.verify(mockClient).connect();
	}
	
	@Test
	public void onCommunicationFailure_shouldSetResultToResetForAServerExceptionWithBinlogErrorCode() throws Exception {
		assertNull(getResult());
		
		verifier.onCommunicationFailure(null, new ServerException("", ER_MASTER_FATAL_ERROR_READING_BINLOG, "HY000"));
		
		Mockito.verify(mockClient).disconnect();
		assertEquals(OffsetVerificationResult.RESET, getResult());
	}
	
	@Test
	public void onCommunicationFailure_shouldSetResultToErrorForAServerExceptionWithNonBinlogErrorCode() throws Exception {
		assertNull(getResult());
		
		verifier.onCommunicationFailure(null, new ServerException("", 1237, "HY000"));
		
		Mockito.verify(mockClient).disconnect();
		assertEquals(OffsetVerificationResult.ERROR, getResult());
	}
	
	@Test
	public void onCommunicationFailure_shouldSetResultToErrorForANonServerExceptionAndDisconnect() throws Exception {
		assertNull(getResult());
		
		verifier.onCommunicationFailure(null, new EIPException("test"));
		
		Mockito.verify(mockClient).disconnect();
		assertEquals(OffsetVerificationResult.ERROR, getResult());
	}
	
	@Test
	public void onEventDeserializationFailure_shouldSetResultToErrorAndDisconnect() throws Exception {
		assertNull(getResult());
		
		verifier.onEventDeserializationFailure(null, null);
		
		Mockito.verify(mockClient).disconnect();
		assertEquals(OffsetVerificationResult.ERROR, getResult());
	}
	
	@Test
	public void onEvent_shouldSetResultToPassAndDisconnect() throws Exception {
		assertNull(getResult());
		
		verifier.onEvent(null);
		
		Mockito.verify(mockClient).disconnect();
		assertEquals(OffsetVerificationResult.PASS, getResult());
	}
	
	@Test
	public void onConnectionFailure_shouldSetResultToError() {
		assertNull(getResult());
		
		verifier.onConnectionFailure();
		
		assertEquals(OffsetVerificationResult.ERROR, getResult());
	}
	
	@Test
	public void onDisconnectionFailure_shouldSetResultToError() {
		assertNull(getResult());
		
		verifier.onDisconnectionFailure();
		
		assertEquals(OffsetVerificationResult.ERROR, getResult());
	}
	
}
