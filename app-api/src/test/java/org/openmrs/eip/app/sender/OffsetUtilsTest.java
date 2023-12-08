package org.openmrs.eip.app.sender;

import static org.junit.Assert.assertEquals;
import static org.openmrs.eip.app.sender.SenderConstants.OFFSET_PROP_EVENT;
import static org.openmrs.eip.app.sender.SenderConstants.OFFSET_PROP_FILE;
import static org.openmrs.eip.app.sender.SenderConstants.OFFSET_PROP_POSITION;
import static org.openmrs.eip.app.sender.SenderConstants.OFFSET_PROP_ROW;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.kafka.connect.storage.FileOffsetBackingStore;
import org.apache.kafka.connect.storage.MemoryOffsetBackingStore;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.openmrs.eip.app.AppUtils;
import org.openmrs.eip.app.CustomFileOffsetBackingStore;
import org.openmrs.eip.app.sender.OffsetVerifier.OffsetVerificationResult;
import org.openmrs.eip.component.exception.EIPException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.debezium.connector.mysql.MySqlStreamingChangeEventSource.BinlogPosition;

@RunWith(PowerMockRunner.class)
@PrepareForTest(OffsetUtils.class)
public class OffsetUtilsTest {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	private static final ByteBuffer OFFSET_KEY_BYTE_BUFFER = ByteBuffer.wrap("test-key".getBytes());
	
	@Mock
	private OffsetVerifier mockVerifier;
	
	private ByteBuffer createValueByteBuffer(String file, long position, Integer rows, Integer events) throws Exception {
		Map valueMap = new HashMap();
		valueMap.put(OFFSET_PROP_FILE, file);
		valueMap.put(OFFSET_PROP_POSITION, position);
		valueMap.put(SenderConstants.OFFSET_PROP_ROW, rows);
		valueMap.put(SenderConstants.OFFSET_PROP_EVENT, events);
		return ByteBuffer.wrap(MAPPER.writeValueAsBytes(valueMap));
	}
	
	private Map<ByteBuffer, ByteBuffer> createTestOffset(String file, long position, Integer rows, Integer events)
	    throws Exception {
		
		Map<ByteBuffer, ByteBuffer> offset = new HashMap();
		offset.put(OFFSET_KEY_BYTE_BUFFER, createValueByteBuffer(file, position, rows, events));
		return offset;
	}
	
	@Test
	public void verifyOffsetAndResetIfInvalid_shouldSkipVerificationForAnEmptyOffset() throws Exception {
		OffsetUtils.verifyOffsetAndResetIfInvalid(new HashMap());
		Mockito.verifyNoInteractions(mockVerifier);
	}
	
	@Test
	public void verifyOffsetAndResetIfInvalid_shouldFailForAnOffsetWithMoreThanOneEntry() {
		Map offsetData = new HashMap();
		offsetData.put(ByteBuffer.wrap("key1".getBytes()), ByteBuffer.wrap("value".getBytes()));
		offsetData.put(ByteBuffer.wrap("key2".getBytes()), ByteBuffer.wrap("value".getBytes()));
		Exception thrown = Assert.assertThrows(EIPException.class,
		    () -> OffsetUtils.verifyOffsetAndResetIfInvalid(offsetData));
		Assert.assertEquals("Invalid existing offset file content entry size: " + offsetData.size(), thrown.getMessage());
	}
	
	@Test
	public void verifyOffsetAndResetIfInvalid_shouldNotResetAValidOffset() throws Exception {
		final String file = "test-binlog";
		final long position = 350;
		final int rows = 1;
		final int events = 2;
		Map offsetData = createTestOffset(file, position, rows, events);
		BinlogPosition binlogPosition = new BinlogPosition(file, position);
		PowerMockito.whenNew(OffsetVerifier.class).withArguments(binlogPosition).thenReturn(mockVerifier);
		Mockito.when(mockVerifier.verify()).thenReturn(OffsetVerificationResult.PASS);
		
		OffsetUtils.verifyOffsetAndResetIfInvalid(offsetData);
		
		Mockito.verify(mockVerifier).verify();
		Assert.assertEquals(1, offsetData.size());
		Assert.assertEquals(OFFSET_KEY_BYTE_BUFFER, offsetData.keySet().iterator().next());
		Assert.assertEquals(createValueByteBuffer(file, position, rows, events), offsetData.values().iterator().next());
	}
	
	@Test
	public void verifyOffsetAndResetIfInvalid_shouldFailForANullVerificationResult() throws Exception {
		final String file = "test-binlog";
		final long position = 350;
		Map<ByteBuffer, ByteBuffer> offsetData = createTestOffset(file, position, null, null);
		BinlogPosition binlogPosition = new BinlogPosition(file, position);
		PowerMockito.whenNew(OffsetVerifier.class).withArguments(binlogPosition).thenReturn(mockVerifier);
		Mockito.when(mockVerifier.verify()).thenReturn(null);
		
		Exception thrown = Assert.assertThrows(EIPException.class,
		    () -> OffsetUtils.verifyOffsetAndResetIfInvalid(offsetData));
		Assert.assertEquals("Failed to verify existing offset: "
		        + MAPPER.readValue(offsetData.values().iterator().next().array(), Map.class),
		    thrown.getMessage());
		
		Mockito.verify(mockVerifier).verify();
	}
	
	@Test
	public void verifyOffsetAndResetIfInvalid_shouldFailForAnErrorVerificationResult() throws Exception {
		final String file = "test-binlog";
		final long position = 350;
		Map<ByteBuffer, ByteBuffer> offsetData = createTestOffset(file, position, null, null);
		BinlogPosition binlogPosition = new BinlogPosition(file, position);
		PowerMockito.whenNew(OffsetVerifier.class).withArguments(binlogPosition).thenReturn(mockVerifier);
		Mockito.when(mockVerifier.verify()).thenReturn(OffsetVerificationResult.ERROR);
		
		Exception thrown = Assert.assertThrows(EIPException.class,
		    () -> OffsetUtils.verifyOffsetAndResetIfInvalid(offsetData));
		Assert.assertEquals("Failed to verify existing offset: "
		        + MAPPER.readValue(offsetData.values().iterator().next().array(), Map.class),
		    thrown.getMessage());
		
		Mockito.verify(mockVerifier).verify();
	}
	
	@Test
	public void verifyOffsetAndResetIfInvalid_shouldResetTheOffsetToStartPositionForResetResult() throws Exception {
		final String file = "test-binlog";
		final long position = 350;
		Map offsetData = createTestOffset(file, position, 1, 2);
		BinlogPosition binlogPosition = new BinlogPosition(file, position);
		PowerMockito.whenNew(OffsetVerifier.class).withArguments(binlogPosition).thenReturn(mockVerifier);
		Mockito.when(mockVerifier.verify()).thenReturn(OffsetVerificationResult.RESET);
		
		OffsetUtils.verifyOffsetAndResetIfInvalid(offsetData);
		
		Mockito.verify(mockVerifier).verify();
		Assert.assertEquals(1, offsetData.size());
		Assert.assertEquals(OFFSET_KEY_BYTE_BUFFER, offsetData.keySet().iterator().next());
		Assert.assertEquals(createValueByteBuffer(file, 4, 0, 0), offsetData.values().iterator().next());
	}
	
	@Test
	public void getBinlogFileName_shouldReturnNullIfTheOffsetFileDoesNotExist() throws Exception {
		Assert.assertNull(OffsetUtils.getBinlogFileName(Mockito.mock(File.class)));
	}
	
	@Test
	public void getBinlogFileName_shouldReturnNullIfTheOffsetFileIsEmpty() throws Exception {
		File file = new File(ClassUtils.getDefaultClassLoader().getResource("empty_dbzm_offset.txt").getFile());
		Assert.assertNull(OffsetUtils.getBinlogFileName(file));
	}
	
	@Test
	public void getBinlogFileName_shouldGetTheCurrentBinlogFile() throws Exception {
		File file = new File(ClassUtils.getDefaultClassLoader().getResource("dbzm_offset.txt").getFile());
		Assert.assertEquals("bin-log.000005", OffsetUtils.getBinlogFileName(file));
	}
	
	@Test
	public void transformOffsetIfNecessary_shouldTransformTheOffsetFileKeyItIsAMap() throws Exception {
		File file = new File(ClassUtils.getDefaultClassLoader().getResource("old_dbzm_offset.txt").getFile());
		CustomFileOffsetBackingStore store = new CustomFileOffsetBackingStore();
		AppUtils.setFieldValue(store, FileOffsetBackingStore.class.getDeclaredField("file"), file);
		AppUtils.invokeMethod(store, FileOffsetBackingStore.class.getDeclaredMethod("load"));
		Map<ByteBuffer, ByteBuffer> offset = AppUtils.getFieldValue(store,
		    MemoryOffsetBackingStore.class.getDeclaredField("data"));
		ObjectMapper mapper = new ObjectMapper();
		Map keyMap = mapper.readValue(offset.keySet().iterator().next().array(), Map.class);
		Assert.assertTrue(keyMap.containsKey("schema"));
		final String extract = "extract";
		final String server = "Nsambya";
		assertEquals(extract, ((List) keyMap.get("payload")).get(0));
		assertEquals(server, ((Map) ((List) keyMap.get("payload")).get(1)).get("server"));
		final int ts = 1697696629;
		final String binlogFile = "bin-log.000012";
		final int position = 1192;
		final int row = 1;
		final int event = 2;
		final int serverId = 2;
		Map valueMap = mapper.readValue(offset.values().iterator().next().array(), Map.class);
		assertEquals(ts, valueMap.get("ts_sec"));
		assertEquals(binlogFile, valueMap.get(OFFSET_PROP_FILE));
		assertEquals(position, valueMap.get(OFFSET_PROP_POSITION));
		assertEquals(row, valueMap.get(OFFSET_PROP_ROW));
		assertEquals(event, valueMap.get(OFFSET_PROP_EVENT));
		assertEquals(serverId, valueMap.get("server_id"));
		
		OffsetUtils.transformOffsetIfNecessary(offset);
		
		List keyList = mapper.readValue(offset.keySet().iterator().next().array(), List.class);
		assertEquals(extract, keyList.get(0));
		assertEquals(server, ((Map) keyList.get(1)).get("server"));
		valueMap = mapper.readValue(offset.values().iterator().next().array(), Map.class);
		assertEquals(ts, valueMap.get("ts_sec"));
		assertEquals(binlogFile, valueMap.get(OFFSET_PROP_FILE));
		assertEquals(position, valueMap.get(OFFSET_PROP_POSITION));
		assertEquals(row, valueMap.get(OFFSET_PROP_ROW));
		assertEquals(event, valueMap.get(OFFSET_PROP_EVENT));
		assertEquals(serverId, valueMap.get("server_id"));
	}
	
	@Test
	public void transformOffsetIfNecessary_shouldSkipIfOffsetIsEmpty() throws Exception {
		Map mockOffset = Mockito.mock(Map.class);
		Mockito.when(mockOffset.isEmpty()).thenReturn(true);
		
		OffsetUtils.transformOffsetIfNecessary(mockOffset);
		
		Mockito.verify(mockOffset, Mockito.never()).keySet();
	}
	
	@Test
	public void transformOffsetIfNecessary_shouldNotTransformTheOffsetFileKeyIfItIsAList() throws Exception {
		File file = new File(ClassUtils.getDefaultClassLoader().getResource("dbzm_offset.txt").getFile());
		CustomFileOffsetBackingStore store = new CustomFileOffsetBackingStore();
		AppUtils.setFieldValue(store, FileOffsetBackingStore.class.getDeclaredField("file"), file);
		AppUtils.invokeMethod(store, FileOffsetBackingStore.class.getDeclaredMethod("load"));
		Map<ByteBuffer, ByteBuffer> offset = AppUtils.getFieldValue(store,
		    MemoryOffsetBackingStore.class.getDeclaredField("data"));
		ObjectMapper mapper = new ObjectMapper();
		final String extract = "extract";
		final String server = "Nsambya";
		List keyList = mapper.readValue(offset.keySet().iterator().next().array(), List.class);
		assertEquals(extract, keyList.get(0));
		assertEquals(server, ((Map) keyList.get(1)).get("server"));
		final int ts = 1701953000;
		final String binlogFile = "bin-log.000005";
		final int position = 4660;
		final int row = 1;
		final int event = 2;
		final int serverId = 2;
		Map valueMap = mapper.readValue(offset.values().iterator().next().array(), Map.class);
		assertEquals(ts, valueMap.get("ts_sec"));
		assertEquals(binlogFile, valueMap.get(OFFSET_PROP_FILE));
		assertEquals(position, valueMap.get(OFFSET_PROP_POSITION));
		assertEquals(row, valueMap.get(OFFSET_PROP_ROW));
		assertEquals(event, valueMap.get(OFFSET_PROP_EVENT));
		assertEquals(serverId, valueMap.get("server_id"));
		
		OffsetUtils.transformOffsetIfNecessary(offset);
		
		keyList = mapper.readValue(offset.keySet().iterator().next().array(), List.class);
		assertEquals(extract, keyList.get(0));
		assertEquals(server, ((Map) keyList.get(1)).get("server"));
		valueMap = mapper.readValue(offset.values().iterator().next().array(), Map.class);
		assertEquals(ts, valueMap.get("ts_sec"));
		assertEquals(binlogFile, valueMap.get(OFFSET_PROP_FILE));
		assertEquals(position, valueMap.get(OFFSET_PROP_POSITION));
		assertEquals(row, valueMap.get(OFFSET_PROP_ROW));
		assertEquals(event, valueMap.get(OFFSET_PROP_EVENT));
		assertEquals(serverId, valueMap.get("server_id"));
	}
	
}
