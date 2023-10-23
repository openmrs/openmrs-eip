package org.openmrs.eip.app.sender;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.openmrs.eip.app.sender.BinlogUtils.FILE_PLACEHOLDER;
import static org.openmrs.eip.app.sender.BinlogUtils.QUERY_PURGE_LOGS;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_DB_PASSWORD;
import static org.openmrs.eip.app.sender.SenderConstants.PROP_DBZM_DB_USER;
import static org.openmrs.eip.component.Constants.PROP_OPENMRS_DB_HOST;
import static org.openmrs.eip.component.Constants.PROP_OPENMRS_DB_PORT;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.component.SyncContext;
import org.openmrs.eip.component.exception.EIPException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.core.env.Environment;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ BinlogUtils.class, SyncContext.class, DriverManager.class, OffsetUtils.class })
public class BinlogUtilsTest {
	
	@Mock
	private Environment mockEnv;
	
	@Mock
	private Connection mockConnection;
	
	@Mock
	private Statement mockStatement;
	
	@Mock
	private ResultSet mockResultSet;
	
	@Before
	public void setup() {
		PowerMockito.mockStatic(SyncContext.class);
		PowerMockito.mockStatic(DriverManager.class);
		PowerMockito.mockStatic(OffsetUtils.class);
		when(SyncContext.getBean(Environment.class)).thenReturn(mockEnv);
	}
	
	@Test
	public void getConnectionToBinaryLogs_shouldObtainTheConnectionObject() throws Exception {
		final String host = "test_host";
		final String port = "3306";
		final String user = "test_user";
		final String pass = "tes_pass";
		final String url = "jdbc:mysql://" + host + ":" + port + BinlogUtils.URL_QUERY;
		when(mockEnv.getProperty(PROP_OPENMRS_DB_HOST)).thenReturn(host);
		when(mockEnv.getProperty(PROP_OPENMRS_DB_PORT)).thenReturn(port);
		when(mockEnv.getProperty(PROP_DBZM_DB_USER)).thenReturn(user);
		when(mockEnv.getProperty(PROP_DBZM_DB_PASSWORD)).thenReturn(pass);
		when(DriverManager.getConnection(url, user, pass)).thenReturn(mockConnection);
		assertEquals(mockConnection, BinlogUtils.getConnectionToBinaryLogs());
	}
	
	@Test
	public void getBinLogFiles_shouldReturnTheListOfBinlogFiles() throws Exception {
		when(DriverManager.getConnection(any(), any(), any())).thenReturn(mockConnection);
		when(mockConnection.createStatement()).thenReturn(mockStatement);
		when(mockStatement.executeQuery(BinlogUtils.QUERY_SHOW_BIN_LOGS)).thenReturn(mockResultSet);
		when(mockResultSet.next()).thenReturn(true).thenReturn(true).thenReturn(false);
		final String logFile1 = "bin-log.000001";
		final String logFile2 = "bin-log.000002";
		when(mockResultSet.getString(1)).thenReturn(logFile1).thenReturn(logFile2);
		
		List<String> logFiles = BinlogUtils.getBinLogFiles();
		
		assertEquals(2, logFiles.size());
		assertEquals(logFile1, logFiles.get(0));
		assertEquals(logFile2, logFiles.get(1));
	}
	
	@Test
	public void getLastProcessedBinLogFileToKeep_shouldFailIfTheCurrentFileIsNotKnownByTheServer() throws Exception {
		PowerMockito.spy(BinlogUtils.class);
		final String file3 = "bin-log.000003";
		final String file4 = "bin-log.000004";
		final String file5 = "bin-log.000005";
		PowerMockito.doReturn(asList(file4, file5)).when(BinlogUtils.class);
		BinlogUtils.getBinLogFiles();
		Throwable t = assertThrows(EIPException.class, () -> BinlogUtils.getLastProcessedBinLogFileToKeep(file3, 1));
		assertEquals("Debezium offset binlog file " + file3 + " is unknown by MySQL server", t.getMessage());
	}
	
	@Test
	public void getLastProcessedBinLogFileToKeep_shouldReturnTheLastFileToKeep() throws Exception {
		PowerMockito.spy(BinlogUtils.class);
		final String file1 = "bin-log.000001";
		final String file2 = "bin-log.000002";
		final String file3 = "bin-log.000003";
		final String file4 = "bin-log.000004";
		final String file5 = "bin-log.000005";
		PowerMockito.doReturn(asList(file1, file2, file3, file4, file5)).when(BinlogUtils.class);
		BinlogUtils.getBinLogFiles();
		final int keepCount = 2;
		assertEquals(file3, BinlogUtils.getLastProcessedBinLogFileToKeep(file5, keepCount));
		assertEquals(file2, BinlogUtils.getLastProcessedBinLogFileToKeep(file4, keepCount));
		assertEquals(file1, BinlogUtils.getLastProcessedBinLogFileToKeep(file3, keepCount));
		assertNull(BinlogUtils.getLastProcessedBinLogFileToKeep(file2, keepCount));
		assertNull(BinlogUtils.getLastProcessedBinLogFileToKeep(file1, keepCount));
	}
	
	@Test
	public void purgeBinLogsTo_shouldPurgeTheBinlogFilesUpToTheFileBeforeTheSpecifiedFile() throws Exception {
		when(DriverManager.getConnection(any(), any(), any())).thenReturn(mockConnection);
		when(mockConnection.createStatement()).thenReturn(mockStatement);
		final String file = "bin-log.000001";
		
		BinlogUtils.purgeBinLogsTo(file);
		
		Mockito.verify(mockStatement).executeUpdate(QUERY_PURGE_LOGS.replace(FILE_PLACEHOLDER, file));
	}
	
	@Test
	public void purgeBinLogs_shouldDoNothingIfNoActiveBinlogFileIsFound() throws Exception {
		File mockOffsetFile = Mockito.mock(File.class);
		PowerMockito.spy(BinlogUtils.class);
		
		BinlogUtils.purgeBinLogs(mockOffsetFile, 0);
		
		Mockito.verifyNoInteractions(mockConnection);
	}
	
	@Test
	public void purgeBinLogs_shouldDoNothingIfNoLastProcessedBinlogFileIsFound() throws Exception {
		File mockOffsetFile = Mockito.mock(File.class);
		final int keepCount = 1;
		final String binlogFile = "test_file";
		PowerMockito.spy(BinlogUtils.class);
		when(OffsetUtils.getBinlogFileName(mockOffsetFile)).thenReturn(binlogFile);
		PowerMockito.doReturn(null).when(BinlogUtils.class);
		BinlogUtils.getLastProcessedBinLogFileToKeep(binlogFile, keepCount);
		
		BinlogUtils.purgeBinLogs(mockOffsetFile, keepCount);
		
		Mockito.verifyNoInteractions(mockConnection);
	}
	
	@Test
	public void purgeBinLogs_shouldPurgeTheBinlogFilesBasedOnTheOffsetFile() throws Exception {
		File mockOffsetFile = Mockito.mock(File.class);
		final int keepCount = 1;
		final String binlogFile = "test_file";
		final String toBinlogFile = "processed_file";
		PowerMockito.spy(BinlogUtils.class);
		when(OffsetUtils.getBinlogFileName(mockOffsetFile)).thenReturn(binlogFile);
		PowerMockito.doReturn(toBinlogFile).when(BinlogUtils.class);
		BinlogUtils.getLastProcessedBinLogFileToKeep(binlogFile, keepCount);
		PowerMockito.doNothing().when(BinlogUtils.class);
		BinlogUtils.purgeBinLogsTo(toBinlogFile);
		
		BinlogUtils.purgeBinLogs(mockOffsetFile, keepCount);
		
		PowerMockito.verifyStatic(BinlogUtils.class);
		BinlogUtils.purgeBinLogsTo(toBinlogFile);
	}
	
}
