package com.aimir.fep.protocol.fmp.server;

/**
 * FMPAdapter MBean  Interface
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public interface FMPAdapterMBean
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
