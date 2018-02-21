package com.aimir.fep.protocol.fmp.common;

import com.aimir.constants.CommonConstants.Protocol;


/**
 * this class is representd for target information to access over Tcp/Ip 
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class PLCTarget extends Target
{
    /**
     * constructor
     *
     * @param ipaddr <code>String</code> IP address
     */
    public PLCTarget(String ipAddr)
    {
        this.ipAddr = ipAddr;
        this.protocol = Protocol.LAN;
    }

    /**
     * constructor
     *
     * @param ipaddr <code>String</code> IP address
     * @param port <code>int</code> port number
     */
    public PLCTarget(String ipAddr,int port)
    {
        this.ipAddr = ipAddr;
        this.port = port;
        this.protocol = Protocol.LAN;
    }
}
