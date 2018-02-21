package com.aimir.fep.protocol.fmp.common;

import com.aimir.constants.CommonConstants.Protocol;

public class BypassTarget extends Target 
{
	/**
     * constructor
     *
     * @param ipaddr <code>String</code> IP address
     */
    public BypassTarget(String ipAddr)
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
    public BypassTarget(String ipAddr,int port,Protocol protocal)
    {
        this.ipAddr = ipAddr;
        this.port = port;
        this.protocol = protocal;
    }
}
