
package com.aimir.fep.adapter;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.remoting.jaxws.SimpleHttpServerJaxWsServiceExporter;
import org.springframework.stereotype.Service;

import com.aimir.fep.util.DataUtil;

/**
 * MOA Startup class
 *
 * 2003.11.17
 */
@Service
public class FepProcessorDebug {
    private static Log logger = LogFactory.getLog(FepProcessorDebug.class);

    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";
    
    private String fepName;
    
    public void init()
    {
        fepName = System.getProperty("name");
        System.setProperty("fepName", fepName);
        
        logger.info("\t" + fepName + " FEPd is Ready for Service...\n");
    }

    public static void main(String[] args) {
		Date startDate = new Date();
		long startTime = startDate.getTime();
		
    	logger.debug("## FepProcessDebug Start!");
    	
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

        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring-fepd.xml"}));
        FepProcessorDebug fep = DataUtil.getBean(FepProcessorDebug.class);
        fep.init();
        if (!enableWS) {
            SimpleHttpServerJaxWsServiceExporter exporter = DataUtil.getBean(SimpleHttpServerJaxWsServiceExporter.class);
            exporter.destroy();
        }
        
		long endTime = System.currentTimeMillis();
		logger.info("######## FepProcessorDebug Elapse Time : " + (endTime - startTime) / 1000.0f + "s");
    }
}