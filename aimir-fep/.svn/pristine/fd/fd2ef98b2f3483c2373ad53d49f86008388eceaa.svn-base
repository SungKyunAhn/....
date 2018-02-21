package com.aimir.fep.adapter;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.remoting.jaxws.SimpleHttpServerJaxWsServiceExporter;
import org.springframework.stereotype.Service;

import com.aimir.fep.util.DataUtil;

@Service
public class EMVProcessor {
	private static Logger logger = LoggerFactory.getLogger(EMVProcessor.class);

	public final static String SERVICE_DOMAIN = "Service";
	public final static String ADAPTER_DOMAIN = "Adapter";

	private String fepName;

	public void init() {
		fepName = System.getProperty("name");
		System.setProperty("fepName", fepName);

		logger.info("\t" + fepName + " EMVd is Ready for Service...\n");
	}

	public static void main(String[] args) {
		Date startDate = new Date();
		long startTime = startDate.getTime();
		
		logger.debug("## EMVProcessor Start!");

		//        boolean enableWS = false;
		//
		//        for (int i=0; i < args.length; i+=2) {
		//
		//            String nextArg = args[i];
		//
		//            if (nextArg.startsWith("-enableWS")) {
		//                enableWS = Boolean.parseBoolean(args[i+1]);
		//            }
		//        }
		boolean enableWS = true;

		DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[] { "/config/spring-fepd.xml" }));
		EMVProcessor fep = DataUtil.getBean(EMVProcessor.class);
		fep.init();
	
		if (!enableWS) {
			SimpleHttpServerJaxWsServiceExporter exporter = DataUtil.getBean(SimpleHttpServerJaxWsServiceExporter.class);
			exporter.destroy();
		}
		
		long endTime = System.currentTimeMillis();
		logger.info("######## EMVProcessor Starting Elapse Time : {}", (endTime - startTime) / 1000.0f + "s");
	}
}