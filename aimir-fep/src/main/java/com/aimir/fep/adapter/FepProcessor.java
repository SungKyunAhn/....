
package com.aimir.fep.adapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.util.DataUtil;

/**
 * FEPd Startup class
 *
 * 2003.11.17
 */
@Service
public class FepProcessor {
    private static Log logger = LogFactory.getLog(FepProcessor.class);

    public final static String SERVICE_DOMAIN = "Service";
    public final static String ADAPTER_DOMAIN = "Adapter";
    
    private String fepName;
    
    public void init()
    {
        fepName = System.getProperty("name");
        System.setProperty("fepName", fepName);
        
        CommonConstants.refreshContractStatus();
        CommonConstants.refreshDataSvc();
        CommonConstants.refreshGasMeterAlarmStatus();
        CommonConstants.refreshGasMeterStatus();
        CommonConstants.refreshHeaderSvc();
        CommonConstants.refreshInterface();
        CommonConstants.refreshMcuType();
        CommonConstants.refreshMeterStatus();
        CommonConstants.refreshMeterType();
        CommonConstants.refreshModemPowerType();
        CommonConstants.refreshModemType();
        CommonConstants.refreshProtocol();
        CommonConstants.refreshSenderReceiver();
        CommonConstants.refreshWaterMeterAlarmStatus();
        CommonConstants.refreshWaterMeterStatus();
        
        logger.info("\t" + fepName + " FEPd is Ready for Service...\n");
    }

    public static void main(String[] args) {
    	DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring-fepd.xml"}));
        FepProcessor fep = DataUtil.getBean(FepProcessor.class);
        fep.init();
    }
}