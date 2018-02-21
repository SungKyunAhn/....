package com.aimir.fep.protocol.fmp.common;

import com.aimir.constants.CommonConstants.Protocol;


/**
 * this class is representd for target information to access over GPRS/Ip 
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class GPRSTarget extends Target
{
    /**
     * constructor
     *
     * @param ipaddr <code>String</code> IP address
     */
    public GPRSTarget(String ipAddr)
    {
        this.ipAddr = ipAddr;
        this.protocol = Protocol.GPRS;
    }

    /**
     * constructor
     *
     * @param ipaddr <code>String</code> IP address
     * @param port <code>int</code> port number
     */
    public GPRSTarget(String ipAddr,int port)
    {
        this.ipAddr = ipAddr;
        this.port = port;
        this.protocol = Protocol.GPRS;
    }
}
