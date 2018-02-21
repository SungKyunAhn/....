package com.aimir.fep.protocol.nip.server;

import javax.management.MBeanServer;
import javax.management.ObjectName;

/**
 * NiUdpDtlsAdapterMBean MBean  Interface
 * 
 * @author 
 * @version
 */
public interface UdpDtlsAdapterMBean
{
    /**
     * start FMPAdapter
     */
    public void start() throws Exception;

    /**
     * get FMPAdapter listen port
     */ 
    public int getPort();

    /**
     * get FMPAdapter ObjectName String
     */
    public String getName(); 

    /**
     * get Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     */
    public Integer getProtocolType();

    /**
     * get Protocol Type(1:CDMA,2:GSM,3:GPRS,4:PSTN,5:LAN)
     */
    public String getProtocolTypeString();

    /**
     * stop FMPAdapter
     */
    public void stop();

    /**
     * get FMPAdapter State
     */ 
    public String getState();

}
