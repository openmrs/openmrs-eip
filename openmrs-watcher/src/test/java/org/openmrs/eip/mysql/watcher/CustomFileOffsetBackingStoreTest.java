/*
 * Copyright (C) Amiyul LLC - All Rights Reserved
 *
 * This source code is protected under international copyright law. All rights
 * reserved and protected by the copyright holder.
 *
 * This file is confidential and only available to authorized individuals with the
 * permission of the copyright holder. If you encounter this file and do not have
 * permission, please contact the copyright holder and delete this file.
 */
package org.openmrs.eip.mysql.watcher;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openmrs.eip.EIPException;
import org.openmrs.eip.Utils;
import org.powermock.reflect.Whitebox;

@ExtendWith(MockitoExtension.class)
public class CustomFileOffsetBackingStoreTest {
	
	private MockedStatic<OffsetUtils> mockOffsetUtils;
	
	private MockedStatic<Utils> mockUtils;
	
	private CustomFileOffsetBackingStore store;
	
	@BeforeEach
	public void setup() {
		mockOffsetUtils = Mockito.mockStatic(OffsetUtils.class);
		mockUtils = Mockito.mockStatic(Utils.class);
		store = Mockito.spy(new CustomFileOffsetBackingStore());
	}
	
	@AfterEach
	public void tearDown() {
		mockOffsetUtils.close();
		mockUtils.close();
		Whitebox.setInternalState(CustomFileOffsetBackingStore.class, "disabled", false);
	}
	
	@Test
	public void start_shouldTransformAndVerifyTheExistingOffset() throws Exception {
		Mockito.doNothing().when(store).doStart();
		Map mockData = new HashMap();
		Whitebox.setInternalState(store, Map.class, mockData);
		
		store.start();
		
		Mockito.verify(store).doStart();
		Mockito.verify(OffsetUtils.class);
		OffsetUtils.transformOffsetIfNecessary(mockData);
	}
	
	@Test
	public void start_shouldFailIfAnExceptionIsThrownWhenVerifyingTheOffset() throws Exception {
		Mockito.doNothing().when(store).doStart();
		Map mockData = new HashMap();
		Whitebox.setInternalState(store, Map.class, mockData);
		Mockito.doThrow(new EIPException("test")).when(OffsetUtils.class);
		OffsetUtils.transformOffsetIfNecessary(mockData);
		
		store.start();
		
		Mockito.verify(store).doStart();
		Mockito.verify(Utils.class);
		Utils.shutdown();
	}
	
}
