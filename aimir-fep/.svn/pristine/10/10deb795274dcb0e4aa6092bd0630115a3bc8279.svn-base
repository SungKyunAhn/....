package com.aimir.fep.protocol.fmp.client.gsm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.GSMTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

/**
 * GSMClient factory
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class GSMClientFactory
{
    private static Log log = LogFactory.getLog(
            GSMClientFactory.class);

    /**
     * get GSMClient from client pool
     *
     * @param target <code>GSMTarget</code> GSM packet target
     * @return client <code>GSMClient</code> MCU GSM Client
     * @throws Exception
     */
    public synchronized static GSMClient getClient(
            GSMTarget target,ProcessorHandler handler) 
        throws Exception
    {
        GSMClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new GSMClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
}
