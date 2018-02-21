package com.aimir.fep.protocol.mrp.client.gsm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.GSMTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

/**
 * GSMMMIUClient factory
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class GSMMMIUClientFactory
{
    private static Log log = LogFactory.getLog(
            GSMMMIUClientFactory.class);

    /**
     * get GSMMMIUClient from client pool
     *
     * @param target <code>GSMTarget</code> GSM packet target
     * @return client <code>GSMMMIUClient</code> MCU GSM Client
     * @throws Exception
     */
    public synchronized static GSMMMIUClient getClient(
            GSMTarget target,ProcessorHandler handler) 
        throws Exception
    {
        GSMMMIUClient client = null;
        String modemId = target.getTargetId();
        if(modemId == null || modemId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new GSMMMIUClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
}
