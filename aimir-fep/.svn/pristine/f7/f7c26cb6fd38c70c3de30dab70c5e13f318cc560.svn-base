package com.aimir.fep.protocol.mrp.client.gprs;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

/**
 * GSMMMIUClient factory
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class GPRSMMIUClientFactory
{
    private static Log log = LogFactory.getLog(
            GPRSMMIUClientFactory.class);

    /**
     * get GSMMMIUClient from client pool
     *
     * @param target <code>GSMTarget</code> GSM packet target
     * @return client <code>GSMMMIUClient</code> MCU GSM Client
     * @throws Exception
     */
    public synchronized static GPRSMMIUClient getClient(
            GPRSTarget target,ProcessorHandler handler) 
        throws Exception
    {
        GPRSMMIUClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new GPRSMMIUClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
}
