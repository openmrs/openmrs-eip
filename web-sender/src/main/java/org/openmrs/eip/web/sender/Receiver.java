package org.openmrs.eip.web.sender;

import java.util.Date;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Receiver {
	
	static ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://"+ Constants.host+":61616");
	
	static Connection connection = null;
	
	static Session session = null;
	
	public static void main(String[] args) throws Exception {
		connection = cf.createConnection(Constants.user, Constants.pass);
		connection.start();
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        receiveFromQueue();
		//receiveFromTopic();
		
		session.close();
		//connection.stop();
		connection.close();
	}
	
	private static void receiveFromQueue() throws Exception {
		Queue queue = session.createQueue("demo");
		MessageConsumer c = session.createConsumer(queue);
		c.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    System.out.println("On Message: " + ((TextMessage)message).getText());
                } catch (JMSException e) {
                    e.printStackTrace();
                }
            }
        });
		Thread.sleep(Long.MAX_VALUE);
        //c.receive();
        //c.receiveNoWait();
        //System.out.println("Received Message: " + ((TextMessage)c.receive()).getText());
	}
	
	private static void receiveFromTopic() throws Exception {
		Topic topic = session.createTopic("demo");
		MessageProducer p = session.createProducer(topic);
		TextMessage m = session.createTextMessage("Hello: " + new Date().toString());
		p.send(m);
	}
}
