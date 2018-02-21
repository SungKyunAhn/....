package com.aimir.fep.integration.iran;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

//import junit.framework.TestSuite;

import org.springframework.integration.support.MessageBuilder;

public class FtpOutBoundTest {
	private static final Logger LOGGER = LoggerFactory.getLogger(FtpOutBoundTest.class);

	@Test
	public void runDemo() throws Exception{

		ConfigurableApplicationContext ctx =
			new ClassPathXmlApplicationContext("config/spring-iran-integration.xml");

		MessageChannel ftpChannel = ctx.getBean("ftpChannel", MessageChannel.class);

		final File fileToSendA = new File("D:/test-files", "a.txt");
		final File fileToSendB = new File("D:/test-files", "b.txt");
		
		System.out.println(fileToSendA.exists());
		
		final Message<File> messageA = MessageBuilder.withPayload(fileToSendA).build();
		final Message<File> messageB = MessageBuilder.withPayload(fileToSendB).build();
		
		ftpChannel.send(messageA);
		ftpChannel.send(messageB);
//
		Thread.sleep(2000);
//
//		//assertTrue(new File(TestSuite.FTP_ROOT_DIR + File.separator + "a.txt").exists());
//		//assertTrue(new File(TestSuite.FTP_ROOT_DIR + File.separator + "b.txt").exists());
//
//		LOGGER.info("Successfully transfered file 'a.txt' and 'b.txt' to a remote FTP location.");
		ctx.close();
	}

	@After
	public void cleanup() {
		//FileUtils.deleteQuietly(baseFolder);
	}
	
//	public static void main(String[] args) {
//		
//		InputStream inputStreamB = FtpOutBoundTest.class.getResourceAsStream("/test-files/b.txt");
//	
//	}
}
