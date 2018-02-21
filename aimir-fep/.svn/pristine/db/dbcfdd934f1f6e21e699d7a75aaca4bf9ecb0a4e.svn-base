package com.aimir.fep.protocol.fmp.client.gprs;

import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * GPRSClient factory
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class GPRSClientFactory
{
    private static Log log = LogFactory.getLog(
            GPRSClientFactory.class);

    /**
     * get GPRSClient from client pool
     *
     * @param target <code>GPRSTarget</code> GPRS packet target
     * @return client <code>GPRSClient</code> MCU GPRS Client
     * @throws Exception
     */
    public synchronized static GPRSClient getClient(
            GPRSTarget target,ProcessorHandler handler) 
        throws Exception
    {
        GPRSClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new GPRSClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
}
