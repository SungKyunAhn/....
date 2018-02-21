package com.aimir.fep.adapter;

/**
 * Bems Adapter MBean  Interface
 * 
 * @author elevas (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2012-07-11 $,
 */
public interface BemsAdapterMBean
{
    /**
     * start Adapter
     */
    public void start() throws Exception;

    /**
     * get local listen port
     */ 
    public int getPort();

    /**
     * get ObjectName String
     */
    public String getName();
    
    
    public void cmdDR(String tagName, int level);
}
