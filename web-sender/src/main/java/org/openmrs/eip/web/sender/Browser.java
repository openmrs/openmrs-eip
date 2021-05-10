package org.openmrs.eip.web.sender;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Browser {
	
	static ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://"+ Constants.host+":61616");
	
	static Connection connection = null;
	
	public static void main(String[] args) throws Exception {
		//cf.setClientID("DB-SYNC-REC");
		connection = cf.createConnection(Constants.user, Constants.pass);
        connection.start();
		browse();
		//Topic topic = session.createTopic("demo");
		//connection.stop();
		connection.close();
	}
	
	private static void browse() throws Exception {
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Queue queue = session.createQueue("openmrs.sync.queue");
		QueueBrowser browser = session.createBrowser(queue);
        int count = 0;
		Enumeration msgs = browser.getEnumeration();
		while (msgs.hasMoreElements()) {
            Object j = msgs.nextElement();
            System.out.println("Message: " + j);
			count++;
		}
		browser.close();
		session.close();
		System.out.println("Message Count: " + count);
	}
	
	private static void consume() throws Exception {
		//Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		//TopicSubscriber consumer = session.createDurableConsumer(topic, "DB-SYNC-RECEIVER");
		//Message m = consumer.receiveNoWait();
	}
	
}
