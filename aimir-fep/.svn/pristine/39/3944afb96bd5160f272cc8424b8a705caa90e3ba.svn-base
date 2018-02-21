package com.aimir.fep.protocol.fmp.client.resource.ts;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

import com.aimir.fep.protocol.fmp.client.FMPClientResource;
import com.aimir.fep.protocol.fmp.client.resource.Pool;
import com.aimir.fep.util.FMPProperty;

public class TSResource implements FMPClientResource
{
    private static TSResource instance = null;
    private Pool pool = null;
    private boolean isLocationedCircuit = false;

    public synchronized static FMPClientResource getInstance() throws Exception
    {
        if(instance == null)
            instance = new TSResource();

        return instance;
    }

    private TSResource() throws Exception
    {
        init();
    }

    private void init() throws Exception
    {
    	String[] strPorts = null;
    	ArrayList<TerminalServerPort> portList = new ArrayList<TerminalServerPort>();
        String ipAddr = null;
        String locationId = null;
        int port = 0;
        int idx1 = 0;
        int idx2 = 0;
        
        try{
        	strPorts = 
                FMPProperty.getProperty("protocol.circuit.ts.port.location").split(",");
        	isLocationedCircuit = true;
        }catch(Exception e){}

    	
        for(int i = 0 ; strPorts != null && i < strPorts.length ; i++)
        {
            idx1 = strPorts[i].indexOf(":");
            idx2 = strPorts[i].lastIndexOf(":");
            if(idx1 < 0)
                continue;
            locationId = strPorts[i].substring(0,idx1);
            ipAddr = strPorts[i].substring(idx1+1,idx2);
            port = Integer.parseInt(strPorts[i].substring(idx2+1));
            portList.add(new TerminalServerPort(locationId,ipAddr,port));
        }
    	
    	if(strPorts == null || strPorts.length == 0){
    		strPorts = 
                FMPProperty.getProperty("protocol.circuit.ts.port").split(",");
    		
            for(int i = 0 ; strPorts != null && i < strPorts.length ; i++)
            {
                idx1 = strPorts[i].indexOf(":");
                if(idx1 < 0)
                    continue;
                ipAddr = strPorts[i].substring(0,idx1);
                port = Integer.parseInt(strPorts[i].substring(idx1+1));
                portList.add(new TerminalServerPort(ipAddr,port));
            }
    		isLocationedCircuit = false;
    	}

        TerminalServerPort[] ports = 
            (TerminalServerPort[])portList.toArray(new TerminalServerPort[0]);

        pool = new Pool(ports);
    }

    public Object acquire() throws Exception
    {
        if(pool == null)
            return null;
        return pool.getItem();
    }
    
    public Object acquire(int locationId) throws Exception
    {
        if(pool == null)
            return null;
        if(isLocationedCircuit)
        	return pool.getItem(locationId);
        else
        	return pool.getItem();
    }

    public void release(Object resourceObj) throws Exception
    {
        if(pool == null)
            return;
        pool.putItem(resourceObj);
    }

    public List<?> getActiveResources() throws Exception
    {
        return pool.getActiveItems();
    }

    public List<?> getIdleResources() throws Exception
    {
        return pool.getIdleItems();
    }

    public String getActiveResourcesString() throws Exception
    {
        StringBuffer sb = new StringBuffer();
        List<?> list = null;
        Iterator<?> iter = null;
        list = getActiveResources();
        iter = list.iterator();
        sb.append("++++++ Active Reosurces Count["+list.size()+"]+++++\n");
        while(iter.hasNext())
        {
            sb.append(iter.next()+"\n");
        }

        return sb.toString();
    }

    public String getIdleResourcesString() throws Exception
    {
        StringBuffer sb = new StringBuffer();
        List<?> list = null;
        Iterator<?> iter = null;
        list = getIdleResources();
        iter = list.iterator();
        sb.append("++++++ Idle Reosurces Count["+list.size()+"]+++++\n");
        while(iter.hasNext())
        {
            sb.append(iter.next()+"\n");
        }

        return sb.toString();
    }
}
