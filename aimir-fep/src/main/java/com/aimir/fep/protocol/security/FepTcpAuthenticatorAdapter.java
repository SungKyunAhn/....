package com.aimir.fep.protocol.security;


import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.aimir.fep.util.DataUtil;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FepTcpAuthenticatorAdapter MBean which processing event from MCU 
 * 
 * @author 
 * @version 
 */
public class FepTcpAuthenticatorAdapter implements FepTcpAuthenticatorAdapterMBean, 
       MBeanRegistration 
{ 
	private static Log log = LogFactory.getLog(FepTcpAuthenticatorAdapter.class);
    private ObjectName objectName = null;
    
    private FepTcpAuthenticator trapAdapter;
    String[] states = {"Stopped", "Stopping", "Starting", "Started", 
        "Failed", "Destroyed" };
    int STOPPED  = 0;
    int STOPPING = 1;
    int STARTING = 2;
    int STARTED  = 3;
    int FAILED  = 4;
    int DESTROYED = 5;
    private int state = STOPPED;
//    private Integer protocolType = new Integer(FMPProperty.getProperty("protocol.type.default"));
    
    public FepTcpAuthenticatorAdapter() {
        trapAdapter = (FepTcpAuthenticator)DataUtil.getBean(FepTcpAuthenticator.class);
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
     * start FepTcpAuthenticator
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
     * set FepTcpAuthenticator listen port
     *
     * @param port <code>int</code> listen port
     */
    public void setPort(int port)
    {
        if(trapAdapter == null)
            trapAdapter = new FepTcpAuthenticator();
        trapAdapter.setPort(port);
    }

    /**
     * get FepTcpAuthenticator listen port
     * 
     * @return port <code>int</code> listen port
     */
    public int getPort()
    {
        return trapAdapter.getPort();
    }

    /**
     * get FepTcpAuthenticator ObjectName String
     *
     * @return name <code>String</code> objectName String
     */
    public String getName() {
        return this.objectName.toString();
    }

    /**
     * stop FepTcpAuthenticator
     */
    public void stop()
    {
        log.debug(this.objectName + " stop");
        state=STOPPING;
        trapAdapter.stop();
        state=STOPPED;
    }

    /**
     * get FepTcpAuthenticator State
     * 
     * @return state <code>String</code>
     */
    public String getState()
    {
        return states[state];
    }

}
