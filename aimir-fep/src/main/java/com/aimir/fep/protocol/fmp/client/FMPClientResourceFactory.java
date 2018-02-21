package com.aimir.fep.protocol.fmp.client;

import com.aimir.fep.protocol.fmp.client.resource.serial.ModemPoolResource;
import com.aimir.fep.protocol.fmp.client.resource.ts.TSResource;

public class FMPClientResourceFactory
{
    public static FMPClientResource getResource(int type)
        throws Exception
    {
        if(type == FMPClientResource.TERMINAL_SERVER)
        {
            return (FMPClientResource)TSResource.getInstance();
        } 
        else if(type ==  FMPClientResource.SERIAL_PORT)
        { 
            return (FMPClientResource)ModemPoolResource.getInstance();
        }
        return  null;
    }
}
