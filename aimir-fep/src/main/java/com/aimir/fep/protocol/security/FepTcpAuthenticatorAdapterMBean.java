package com.aimir.fep.protocol.security;

public interface FepTcpAuthenticatorAdapterMBean {

	   /**
     * start FepTcpAuthenticator
     */
    public void start() throws Exception;

    /**
     * get FepTcpAuthenticator listen port
     */ 
    public int getPort();

    /**
     * get FepTcpAuthenticatorAdaptor ObjectName String
     */
    public String getName(); 


    /**
     * stop FepTcpAuthenticatorAdaptor
     */
    public void stop();

    /**
     * get FepTcpAuthenticatorAdaptor State
     */ 
    public String getState();
}
