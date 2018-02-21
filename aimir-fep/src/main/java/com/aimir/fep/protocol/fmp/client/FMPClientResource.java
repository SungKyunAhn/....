package com.aimir.fep.protocol.fmp.client;

import java.util.List;

public interface FMPClientResource
{
    public static final int TERMINAL_SERVER=1; 
    public static final int SERIAL_PORT=2; 

    public Object acquire(int locationId) throws Exception; 
    public Object acquire() throws Exception; 
    public void release(Object obj) throws Exception; 
    public List<?> getActiveResources() throws Exception;
    public List<?> getIdleResources() throws Exception;
    public String getActiveResourcesString() throws Exception;
    public String getIdleResourcesString() throws Exception;
}
