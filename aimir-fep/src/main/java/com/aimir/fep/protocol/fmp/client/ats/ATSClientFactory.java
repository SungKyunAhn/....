package com.aimir.fep.protocol.fmp.client.ats;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.LANTarget;

/**
 * TCPClient factory
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class ATSClientFactory
{
    private static Log log = LogFactory.getLog(ATSClientFactory.class);

    /**
     * get TCPClient from client pool
     *
     * @param target <code>TcpTarget</code> tcp packet target
     * @return client <code>TCPClient</code> MCU TCP Client
     * @throws Exception
     */
    public synchronized static ATSClient getClient(String ipaddr, int port, String mcuId) 
        throws Exception
    {
        ATSClient client = new ATSClient(ipaddr, port, mcuId);
        return client;
    }
    
    /**
     * get TCPClient from client pool
     *
     * @param target <code>TcpTarget</code> tcp packet target
     * @return client <code>TCPClient</code> MCU TCP Client
     * @throws Exception
     */
    public synchronized static ATSClient getClient(LANTarget target) 
        throws Exception
    {
        ATSClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null"); 
            throw new Exception("target mcuId is null"); 
        }

        client = new ATSClient();
        client.setTarget(target);

        return client;
    }
}
