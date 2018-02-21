package com.aimir.fep.protocol.mrp.client.sms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.SMSTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

/**
 * SMSMMIUClient factory
 * 
 * @author Yeon Kyoung Park
 * @version $Rev: 1 $, $Date: 2008-01-05 15:59:15 +0900 $,
 */
public class SMSMMIUClientFactory
{
    private static Log log = LogFactory.getLog(
            SMSMMIUClientFactory.class);

    /**
     * get SMSMMIUClient from client pool
     *
     * @param target <code>SMSTarget</code> SMS packet target
     * @return client <code>SMSMMIUClient</code> MCU SMS Client
     * @throws Exception
     */
    public synchronized static SMSMMIUClient getClient(
            SMSTarget target,ProcessorHandler handler) 
        throws Exception
    {
        SMSMMIUClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new SMSMMIUClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
}
