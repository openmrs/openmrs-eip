package org.openmrs.eip.web.sender;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Sender {
	
	static ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://"+ Constants.host+":61616");
	
	static Connection connection = null;
	
	static Session session = null;
	
	public static void main(String[] args) throws Exception {
		connection = cf.createConnection(Constants.user, Constants.pass);
		//connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

		//sendToQueue();
        Thread.sleep(20000);
        //sendToQueue();
        Thread.sleep(Long.MAX_VALUE);
        //sendToQueue();
		//sendToTopic();
		
		session.close();
		//connection.stop();
		connection.close();
	}
	
	private static void sendToQueue() throws Exception {
		Queue queue = session.createQueue("demo");
		MessageProducer p = session.createProducer(queue);
		TextMessage m = session.createTextMessage("Hello: " + new Date().toString());
		p.send(m);
	}
	
	private static void sendToTopic() throws Exception {
		Topic topic = session.createTopic("demo");
		MessageProducer p = session.createProducer(topic);
		TextMessage m = session.createTextMessage("Hello: " + new Date().toString());
		p.send(m);
	}
}
