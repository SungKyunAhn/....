package com.aimir.fep.protocol.fmp.client.bypass;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.client.gprs.GPRSClient;
import com.aimir.fep.protocol.fmp.common.BypassTarget;
import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

public class BypassClientFactory {
    private static Log log = LogFactory.getLog(
    		BypassClientFactory.class);
    
    /**
     * get GPRSClient from client pool
     *
     * @param target <code>GPRSTarget</code> GPRS packet target
     * @return client <code>GPRSClient</code> MCU GPRS Client
     * @throws Exception
     */
    public synchronized static BYPASSClient getClient(
            BypassTarget target,ProcessorHandler handler) 
        throws Exception
    {
    	BYPASSClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new BYPASSClient();
        client.setTarget(target);
        client.setLogProcessor(handler);
        
        return client;
    }
     
}
