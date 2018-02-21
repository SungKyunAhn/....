package com.aimir.fep.protocol.mrp.client.lan;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

public class LANSocketClientFactory {

    private static Log log = LogFactory.getLog(
    		LANSocketClientFactory.class);

    /**
     * IF4 프로토콜을 이용하지 않는 장비에 이용한다.
     * get LANSocketClient from client pool
     *
     * @param target <code>LANTarget</code> LAN packet target
     * @return client <code>LANSocketClient</code> LAN Client
     * @throws Exception
     */
    public synchronized static LANSocketClient getClient(
            LANTarget target,ProcessorHandler handler) 
        throws Exception
    {
        LANSocketClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target modemId is null"); 
            throw new Exception("target modemId is null"); 
        }

        client = new LANSocketClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
    
}
