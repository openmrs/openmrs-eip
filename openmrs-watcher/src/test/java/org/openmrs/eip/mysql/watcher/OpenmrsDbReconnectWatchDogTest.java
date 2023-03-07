package org.openmrs.eip.mysql.watcher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

public class OpenmrsDbReconnectWatchDogTest {
	
	private OpenmrsDbReconnectWatchDog watchDog;
	
	@Mock
	private HealthIndicator mockIndicator;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void run_shouldCheckTheDbConnectionAndDoNothingIfItIsDown() {
		OpenmrsDbReconnectHandler mockHandler = Mockito.mock(OpenmrsDbReconnectHandler.class);
		Health health = new Health.Builder().status(Status.DOWN).build();
		Mockito.when(mockIndicator.health()).thenReturn(health);
		watchDog = new OpenmrsDbReconnectWatchDog(mockHandler, mockIndicator);
		
		watchDog.run();
		
		Mockito.verifyNoInteractions(mockHandler);
	}
	
	@Test
	public void run_shouldCheckTheDbConnectionAndStartTheHandlerThreadIfItIsUp() {
		Health health = new Health.Builder().status(Status.UP).build();
		Mockito.when(mockIndicator.health()).thenReturn(health);
		watchDog = Mockito.spy(new OpenmrsDbReconnectWatchDog(null, mockIndicator));
		
		watchDog.run();
		
		Mockito.verify(watchDog).startReconnectHandler();
	}
	
}
