package com.aimir.fep.protocol.fmp.client.plc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

import com.aimir.fep.protocol.fmp.common.PLCTarget;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;

/**
 * TCPClient factory
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class PLCClientFactory
{
    private static Log log = LogFactory.getLog(PLCClientFactory.class);
    private static ProcessorHandler plcHandler = null;
    private static void initPLCProcessor(ApplicationContext ctx)
    {
        if(plcHandler == null)
        {
            try {
                plcHandler = DataUtil.getBean(ProcessorHandler.class);
            }catch(Exception ex)
            {
                log.error("initPLCProcessor failed",ex);
            }
        }
    }
    /**
     * get TCPClient from client pool
     *
     * @param target <code>TcpTarget</code> tcp packet target
     * @return client <code>TCPClient</code> MCU TCP Client
     * @throws Exception
     */
    public synchronized static PLCClient getClient(
            PLCTarget target,ProcessorHandler handler, ApplicationContext ctx)
        throws Exception
    {
    	initPLCProcessor(ctx);
    	PLCClient client = null;
        String mcuId = target.getTargetId();
        if(mcuId == null || mcuId.length() < 1)
        {
            log.error("target mcuId is null");
            throw new Exception("target mcuId is null");
        }

        client = new PLCClient();
        client.setTarget(target);
        client.setLogProcessor(handler);
        client.setPlcProcessor(plcHandler);

        return client;
    }
}
