package org.openmrs.eip.mysql.watcher;

import static org.mockito.MockitoAnnotations.openMocks;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.actuate.health.Status;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OpenmrsDbReconnectWatchDogTest {
	
	private OpenmrsDbReconnectWatchDog watchDog;
	
	@Mock
	private HealthIndicator mockIndicator;
	
	private AutoCloseable openMocksAutoCloseable;
	
	@BeforeEach
	public void setup() {
		this.openMocksAutoCloseable = openMocks(this);
	}
	
	@AfterAll
	public void tearDown() throws Exception {
		this.openMocksAutoCloseable.close();
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
