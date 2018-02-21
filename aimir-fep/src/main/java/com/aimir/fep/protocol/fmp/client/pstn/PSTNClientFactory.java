package com.aimir.fep.protocol.fmp.client.pstn;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.PSTNTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

/**
 * PSTNClient factory
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class PSTNClientFactory
{
    private static Log log = LogFactory.getLog(PSTNClientFactory.class);

    /**
     * get PSTNClient from client pool
     *
     * @param target <code>PSTNTarget</code> PSTN packet target
     * @return client <code>PSTNClient</code> MCU PSTN Client
     * @throws Exception
     */
    public synchronized static PSTNClient getClient(
            PSTNTarget target,ProcessorHandler handler) 
        throws Exception
    {
        PSTNClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new PSTNClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
}
