package com.aimir.fep.protocol.fmp.client.resource.serial;

public class ModemPoolPort
{
	private String locationId = null;
    private String devName = null;
    private int port = 0;

    public ModemPoolPort(String locationId, String devName,int port)
    {
    	this.locationId = locationId;
        this.devName = devName;
        this.port = port;
    }
    
    public ModemPoolPort(String devName,int port)
    {
        this.devName = devName;
        this.port = port;
    }

    public String getdevName()
    {
        return this.devName;
    }
    
    public void setdevName(String devName)
    {
        this.devName = devName;
    }

    public int getPort()
    {
        return this.port;
    }

    public void setPort(int port)
    {
        this.port = port;
    }
    
    public String getLocationId()
    {
    	return this.locationId;
    }
    
    public void setLocationId(String locationId)
    {
    	this.locationId = locationId;
    }

    public String toString()
    {
        return "ModemPoolPort LOCATION["+locationId+"] IP["+devName+"] PORT["+port+"]";
    }
}
