package com.aimir.fep.protocol.nip.server;

/**
 * NiTcpCmdAdapter MBean  Interface
 * 
 * @author Elevas Park
 * @version $Rev: 1 $, $Date: 2016-05-27 15:59:15 +0900 $,
 */
public interface NiTcpCmdAdapterMBean
{
    /**
     * start NiTcpCmdAdapter
     */
    public void start() throws Exception;

    /**
     * get NiTcpCmdAdapter listen port
     */ 
    public int getPort();

    /**
     * get NiTcpCmdAdapter ObjectName String
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
     * stop NiTcpCmdAdapter
     */
    public void stop();

    /**
     * get NiTcpCmdAdapter State
     */ 
    public String getState();
}
