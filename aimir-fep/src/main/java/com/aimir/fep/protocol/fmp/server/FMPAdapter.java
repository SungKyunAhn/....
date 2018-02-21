package com.aimir.fep.protocol.fmp.server;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.aimir.constants.CommonConstants;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.system.Code;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FMPAdapter MBean which processing event from MCU 
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class FMPAdapter implements FMPAdapterMBean, 
       MBeanRegistration 
{ 
	private static Log log = LogFactory.getLog(FMPAdapter.class);
    private ObjectName objectName = null;
    
    private FMPTrapAdapter trapAdapter;
    String[] states = {"Stopped", "Stopping", "Starting", "Started", 
        "Failed", "Destroyed" };
    int STOPPED  = 0;
    int STOPPING = 1;
    int STARTING = 2;
    int STARTED  = 3;
    int FAILED  = 4;
    int DESTROYED = 5;
    private int state = STOPPED;
    private Integer protocolType = new Integer(FMPProperty.getProperty("protocol.type.default"));
    
    public FMPAdapter() {
        trapAdapter = (FMPTrapAdapter)DataUtil.getBean(FMPTrapAdapter.class);
    }

    /**
     * method of interface MBeanRegistration 
     *
     * @param server <code>MBeanServer</code> MBeanServer
     * @param name <code>ObjectName</code> MBean Object Name
     * @throws java.lang.Exception
     */
    public ObjectName preRegister(MBeanServer server, 
            ObjectName name) throws java.lang.Exception 
    {
        if (name == null) 
        {
            name = new ObjectName(server.getDefaultDomain() 
                    + ":service=" + this.getClass().getName());
        }

        this.objectName = name;
        this.trapAdapter.setName(name.toString());
        return name;
    }

    /**
     * method of interface MBeanRegistration 
     *
     * @param registrationDone <code>Boolean</code> 
     * @throws java.lang.Exception
     */
    public void postRegister(Boolean registrationDone) { }
    /**
     * method of interface MBeanRegistration 
     *
     * @throws java.lang.Exception
     */
    public void preDeregister() throws java.lang.Exception { }
    /**
     * method of interface MBeanRegistration 
     *
     * @throws java.lang.Exception
     */
    public void postDeregister() { }

    /**
     * start FMPAdapter
     *
     * @throws java.lang.Exception
     */
    public void start() throws Exception
    {
        log.debug(this.objectName + " start");
        try {
            state=STARTING;
            trapAdapter.start();
            state=STARTED;
        }catch(Exception ex)
        {
            log.error("objectName["+this.objectName
                    +"] start failed");
            state = STOPPED;
            throw ex;
        }
    }

    /**
     * set FMPAdapter listen port
     *
     * @param port <code>int</code> listen port
     */
    public void setPort(int port)
    {
        if(trapAdapter == null)
            trapAdapter = new FMPTrapAdapter();
        trapAdapter.setPort(port);
    }

    /**
     * get FMPAdapter listen port
     * 
     * @return port <code>int</code> listen port
     */
    public int getPort()
    {
        return trapAdapter.getPort();
    }

    /**
     * get FMPAdapter ObjectName String
     *
     * @return name <code>String</code> objectName String
     */
    public String getName() {
        return this.objectName.toString();
    }

    /**
     * stop FMPAdapter
     */
    public void stop()
    {
        log.debug(this.objectName + " stop");
        state=STOPPING;
        trapAdapter.stop();
        state=STOPPED;
    }

    /**
     * get FMPAdapter State
     * 
     * @return state <code>String</code>
     */
    public String getState()
    {
        return states[state];
    }

    /**
     * set Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @param protocolType <code>Integer</code> Protocol Type
     */
    public void setProtocolType(Integer protocolType)
    {
        this.protocolType = protocolType;
        trapAdapter.setProtocolType(protocolType);
    }

    /**
     * get Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @return protocolType <code>Integer</code> Protocol Type
     */
    public Integer getProtocolType()
    {
        return this.protocolType;
    }

    /**
     * get Protocol Type String(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     * @return protocolType <code>String</code> Protocol Type
     */
    public String getProtocolTypeString()
    {
        int proto = this.protocolType.intValue();
        Code code = CommonConstants.getProtocol(proto+"");
        return "[" + code.getName() + "]";
    }
}
