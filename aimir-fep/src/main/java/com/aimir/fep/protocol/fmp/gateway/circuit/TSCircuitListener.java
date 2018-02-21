package com.aimir.fep.protocol.fmp.gateway.circuit;

import java.util.ArrayList;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.FMPProperty;

/**
 * Terminal Server Circuit Listener MBean which processing event from MCU 
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class TSCircuitListener implements TSCircuitListenerMBean, 
       MBeanRegistration 
{ 
	private static Log log = LogFactory.getLog(TSCircuitListener.class);
    @SuppressWarnings("unused")
	private MBeanServer server = null;
    private ObjectName objectName = null;
    private ArrayList<CircuitListener> circuitListener = new ArrayList<CircuitListener>();
    String[] states = {"Stopped", "Stopping", "Starting", "Started", 
        "Failed", "Destroyed" };
    int STOPPED  = 0;
    int STOPPING = 1;
    int STARTING = 2;
    int STARTED  = 3;
    int FAILED  = 4;
    int DESTROYED = 5;
    private int state = STOPPED;

    public TSCircuitListener(int clport) 
    { 
        log.info("TSCircuitListener("+clport+")");
        String listenerList = 
            FMPProperty.getProperty("protocol.circuit.ts.listener.port");
        log.info("TSCircuitListener:: listenerList["+listenerList+"]");
        if(listenerList != null && listenerList.length() > 0)
        {
            String[] listeners = listenerList.split(",");
            String ipAddr = null;
            int port = 0;
            int idx = 0;
            for(int i = 0 ; i < listeners.length ; i++)
            {
                try
                {
                    idx = listeners[i].indexOf(":");
                    if(idx < 0)
                        continue;
                    ipAddr = listeners[i].substring(0,idx);
                    port = Integer.parseInt(listeners[i].substring(idx+1));
                    circuitListener.add( 
                            new CircuitListener(ipAddr,port,clport));
                }catch(Exception ex) {
                    log.error(ex,ex);
                    continue;
                }
            }
        }
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

        this.server = server;
        this.objectName = name;
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
     * start TSCircuitListener
     *
     * @throws java.lang.Exception
     */
    public void start() throws Exception
    {
        log.debug(this.objectName + " start");
        try {
            state=STARTING;
            CircuitListener[] cls = (CircuitListener[])
                circuitListener.toArray(new CircuitListener[0]);
            for(int i = 0 ; i < cls.length ; i++)
            {
                try { cls[i].start(); }catch(Exception exx) 
                { log.error(exx,exx); }
            }
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
     * get TSCircuitListener ObjectName String
     *
     * @return name <code>String</code> objectName String
     */
    public String getName() {
        return this.objectName.toString();
    }

    /**
     * stop TSCircuitListener
     */
    public void stop()
    {
        log.debug(this.objectName + " stop");
        state=STOPPING;
        CircuitListener[] cls = (CircuitListener[])
            circuitListener.toArray(new CircuitListener[0]);
        for(int i = 0 ; i < cls.length ; i++)
        {
            cls[i].stop();
        }
        state=STOPPED;
    }

    /**
     * get TSCircuitListener State
     * 
     * @return state <code>String</code>
     */
    public String getState()
    {
        return states[state];
    }
}
