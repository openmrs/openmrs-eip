package org.openmrs.eip.component.utils;

import org.junit.Test;
import org.openmrs.eip.component.exception.EIPException;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

public class FileUtilsTest {
	
	@Test
	public void getPrivateKeysFromFolder_should_return_private_key() throws IOException {
		// Given
		String folderPath = "/src/test/resources/keys/sender";
		
		// When
		byte[] result = FileUtils.getPrivateKeysFromFolder(folderPath);
		
		// Then
		assertNotNull(result);
	}
	
	@Test
	public void getPrivateKeysFromFolder_should_throw_exception_not_private_key() {
		// Given
		String folderPath = "";
		
		// When
		try {
			FileUtils.getPrivateKeysFromFolder(folderPath);
			
			fail();
		}
		catch (Exception e) {
			// Then
			assertTrue(e instanceof EIPException);
			assertEquals("No private key found", e.getMessage());
		}
	}
	
	@Test
	public void getPrivateKeysFromFolder_should_throw_exception_to_many_private_keys() {
		// Given
		String folderPath = "/src/test/resources/keys/tomanykeys";
		
		// When
		try {
			FileUtils.getPrivateKeysFromFolder(folderPath);
			
			fail();
		}
		catch (Exception e) {
			// Then
			assertTrue(e instanceof EIPException);
			assertEquals("There should be only one private key. 2 found", e.getMessage());
		}
	}
	
	@Test
	public void getPrivateKeysFromFolder_should_return_public_key() throws IOException {
		// Given
		String folderPath = "/src/test/resources/keys/sender";
		
		// When
		List<byte[]> result = FileUtils.getPublicKeysFromFolder(folderPath);
		
		// Then
		assertNotNull(result);
		assertEquals(1, result.size());
	}
}
