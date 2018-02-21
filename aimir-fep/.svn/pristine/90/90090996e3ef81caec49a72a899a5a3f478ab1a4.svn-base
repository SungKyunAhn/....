package com.aimir.fep.protocol.fmp.client;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.Interface;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.fmp.processor.CommLogProcessor;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.util.DataUtil;
/**
 * Client Factory
 *
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class ClientFactory
{
    private static Log log = LogFactory.getLog(ClientFactory.class);
    private static String pkg = null;
    static {
        String clsname = ClientFactory.class.getName();
        int idx = clsname.lastIndexOf(".");
        pkg = clsname.substring(0,idx+1);
    }

    private static ProcessorHandler handler = DataUtil.getBean(ProcessorHandler.class);

    private static Client getClientFromFactory(Target target)
        throws Exception
    {
        String clientFactory = "ClientFactory";
        Protocol protocol = target.getProtocol();
        
        if (target.getInterfaceType() == Interface.AMU) {
            pkg = pkg.replaceAll("mrp", "fmp");
            clientFactory = "AMU"+clientFactory;
            log.info("clientFactory name : " + clientFactory);
        }
        else {
            switch (target.getTargetType()) {
                case MMIU :
                case IEIU : 
                case Converter :
                	pkg = pkg.replaceAll("fmp", "mrp");
                    clientFactory = "MMIU"+clientFactory;                
                    break;                
                default :
                    pkg = pkg.replaceAll("mrp", "fmp");
            }
        }

        String factoryname = pkg+protocol.name().toLowerCase()
                    +"." + protocol.name().toUpperCase()+clientFactory;
        log.info("factoryname["+factoryname+"]");
        Class<?> cls = null;
        Method method = null;
        try{
            cls = Class.forName(factoryname);
            method = cls.getMethod("getClient",new Class[]
                    { target.getClass(), ProcessorHandler.class });
        }catch(Exception ex)
        {
            log.error("getClientFromClient failed",ex);
            return null;
        }

        Client client = (Client)method.invoke(cls,
                new Object[]{ target, handler });
        return client;
    }

    private static Client getClientFromClient(Target target)
        throws Exception
    {

        String clsname = ClientFactory.class.getName();
        int idx = clsname.lastIndexOf(".");
        pkg = clsname.substring(0,idx+1);
        String client = "Client";
        Protocol protocol = target.getProtocol();
        
        switch (target.getTargetType()) {
            case MMIU :
            case IEIU :
                pkg = pkg.replaceAll("fmp", "mrp");
                client = "MMIU"+client;
                break;
            case Converter :
                pkg = pkg.replaceAll("fmp",  "mrp");
                client = "Socket"+client;
                break;
        }

        String clientname = pkg+protocol.name().toLowerCase()
                    +"."+protocol.name().toUpperCase()+client;
        log.info("clientname["+clientname+"]");
        Class<?> cls = null;
        Object obj = null;
        Method method = null;
        try{
            cls = Class.forName(clientname);
            obj = cls.newInstance();
            method = cls.getMethod("setTarget",new Class[]
                    { Target.class });
        }catch(Exception ex)
        {
            log.error("getClientFromClient failed",ex);
            return null;
        }
        method.invoke(obj,new Object[] { target });
        return (Client)obj;
    }

    /**
     * get Client according to target
     *
     * @param target <code>Target</code> target
     * @return client <code>Client</code> MCU Client
     * @throws Exception
     */
    public static Client getClient(Target target) throws Exception
    {
        log.info("Target[" + target.getTargetId() + "]");
        Client client = null; // getClientFromFactory(target);
        if(client == null)
            client = getClientFromClient(target);

        if(client == null)
            throw new Exception("can not Tranport Type["
                    +target.getProtocol()+"] Class");
        return client;
    }
}
