package com.aimir.fep.protocol.fmp.client.sms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.SMSTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

/**
 * SMSClient factory
 * 
 * @author Y.K Park (goodjob@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2008-11-11 13:59:15 +0900 $,
 */
public class SMSClientFactory
{
    private static Log log = LogFactory.getLog(
            SMSClientFactory.class);

    /**
     * get SMSClient from client pool
     *
     * @param target <code>SMSTarget</code> SMS packet target
     * @return client <code>SMSClient</code> MCU SMS Client
     * @throws Exception
     */
    public synchronized static SMSClient getClient(
            SMSTarget target,ProcessorHandler handler) 
        throws Exception
    {
        SMSClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new SMSClient();
        client.setTarget(target);
        client.setLogProcessor(handler);

        return client;
    }
}
