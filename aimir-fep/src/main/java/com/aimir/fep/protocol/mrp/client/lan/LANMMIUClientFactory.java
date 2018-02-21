package com.aimir.fep.protocol.mrp.client.lan;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

public class LANMMIUClientFactory {

    private static Log log = LogFactory.getLog(
    		LANMMIUClientFactory.class);

    /**
     * get LANMMIUClient from client pool
     *
     * @param target <code>LANTarget</code> LAN packet target
     * @return client <code>LANMMIUClient</code> MCU LAN Client
     * @throws Exception
     */
    public synchronized static LANMMIUClient getClient(
            LANTarget target,ProcessorHandler handler) 
        throws Exception
    {
    	LANMMIUClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target modemId is null"); 
            throw new Exception("target modemId is null"); 
        }

        client = new LANMMIUClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
    
}
