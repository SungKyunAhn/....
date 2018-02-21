package com.aimir.fep.protocol.fmp.client.resource.ts;

public class TerminalServerPort
{
	private String locationId = null;
    private String ipAddr = null;
    private int port = 0;

    public TerminalServerPort(String locationId, String ipAddr,int port)
    {
    	this.locationId = locationId;
        this.ipAddr = ipAddr;
        this.port = port;
    }
    
    public TerminalServerPort(String ipAddr,int port)
    {
        this.ipAddr = ipAddr;
        this.port = port;
    }

    public String getIpAddr()
    {
        return this.ipAddr;
    }
    
    public void setIpAddr(String ipAddr)
    {
        this.ipAddr = ipAddr;
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
        return "TerminalServerPort LOCATION["+locationId+"] IP["+ipAddr+"] PORT["+port+"]";
    }
}
