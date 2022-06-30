package org.openmrs.eip.app;

import java.io.File;

import javax.activation.FileDataSource;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.attachment.AttachmentMessage;
import org.apache.camel.attachment.DefaultAttachment;
import org.openmrs.eip.component.camel.utils.CamelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component("shutdownNotificationProcessor")
public class ShutdownNotificationProcessor implements Processor {
	
	private static Logger log = LoggerFactory.getLogger(ShutdownNotificationProcessor.class);
	
	@Value("${shutdown.notice.email.attachment.log.file}")
	private String logFilePath;
	
	@Value("${smtp.host.name}")
	private String host;
	
	@Value("${smtp.host.port}")
	private String port;
	
	@Value("${smtp.auth.user}")
	private String user;
	
	@Value("${smtp.auth.pass}")
	private String pass;
	
	@Value("${shutdown.notice.email.recipients}")
	private String recipients;
	
	@Override
	public void process(Exchange exchange) throws Exception {
		log.info("Sending failure notification email with the log file attached");
		
		try {
			String siteName = exchange.getProperty(SyncConstants.EX_PROP_APP_ID, String.class);
			AttachmentMessage in = exchange.getMessage(AttachmentMessage.class);
			in.setHeader("subject", "DB sync application at " + siteName + " site has stopped");
			in.setHeader("to", recipients);
			in.setHeader("from", user);
			in.setHeader("mail.smtp.auth", true);
			in.setHeader("mail.smtp.starttls.enable", true);
			
			in.setBody("The Db sync application at " + siteName
			        + " site has stopped due to an exception, please see attached log file");
			
			File file = new File(logFilePath);
			
			if (file.exists()) {
				in.addAttachmentObject("dbsync-logs", new DefaultAttachment(new FileDataSource(logFilePath)));
			}
			
			CamelUtils.send("smtps://" + host + ":" + port + "?username=" + user + "&password=" + pass, exchange);
			
			log.info("Successfully sent failure notification email");
		}
		catch (Throwable t) {
			log.error("An error occurred while sending shutdown email notification", t);
		}
	}
	
}
