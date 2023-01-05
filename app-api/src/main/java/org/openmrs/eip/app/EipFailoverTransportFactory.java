package org.openmrs.eip.app;

import java.io.IOException;
import java.net.URI;

import org.apache.activemq.transport.Transport;
import org.apache.activemq.transport.failover.FailoverTransportFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom {@link FailoverTransportFactory} that keeps hold of the reference to the {@link Transport}
 * instance so that it can be stopped during application shutdown.
 */
public class EipFailoverTransportFactory extends FailoverTransportFactory {
	
	protected static final Logger log = LoggerFactory.getLogger(EipFailoverTransportFactory.class);
	
	private static Transport activeMqTransport;
	
	/**
	 * @see FailoverTransportFactory#doConnect(URI)
	 */
	@Override
	public Transport doConnect(URI location) throws IOException {
		activeMqTransport = super.doConnect(location);
		return activeMqTransport;
	}
	
	/**
	 * Stops the transport instance
	 */
	public static void stopTransport() {
		if (activeMqTransport != null && !activeMqTransport.isConnected()) {
			log.info("Stopping eip failover transport");
			
			//We need to stop the failover transport otherwise the application will fail to shut down indefinitely.
			try {
				activeMqTransport.stop();
			}
			catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
