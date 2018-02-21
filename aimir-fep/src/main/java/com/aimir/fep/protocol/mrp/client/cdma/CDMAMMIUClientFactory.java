package com.aimir.fep.protocol.mrp.client.cdma;

import com.aimir.fep.protocol.fmp.common.CDMATarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * CDMAMMIUClient factory
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class CDMAMMIUClientFactory
{
    private static Log log = LogFactory.getLog(
            CDMAMMIUClientFactory.class);

    /**
     * get CDMAClient from client pool
     *
     * @param target <code>CDMATarget</code> CDMA packet target
     * @return client <code>CDMAClient</code> MCU CDMA Client
     * @throws Exception
     */
    public synchronized static CDMAMMIUClient getClient(
            CDMATarget target,ProcessorHandler handler) 
        throws Exception
    {
        CDMAMMIUClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new CDMAMMIUClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
}
