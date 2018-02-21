
package com.aimir.fep.protocol.fmp.client.resource;

import java.util.ArrayList;
import java.util.List;

//import EDU.oswego.cs.dl.util.concurrent.Semaphore;
//import java.util.concurrent.Semaphore;
import java.util.concurrent.Semaphore;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.client.resource.ts.TerminalServerPort;

public class Pool 
{

	static Log cat = LogFactory.getLog(Pool.class.getName());
	static int MAX_AVAILABLE;
	private Semaphore available;

	public Pool(Object[] objs) {
		MAX_AVAILABLE = objs.length;
		available = new Semaphore(MAX_AVAILABLE);

		cat.debug("constructor: SEM NUM: "+MAX_AVAILABLE);

		items = objs;
		used = new boolean[MAX_AVAILABLE];
		for (int i=0; i<MAX_AVAILABLE; i++) {
			used[i] = false;
		}
	}

	public Object getItem() throws InterruptedException { // no synch
		cat.debug("getItem");
		//available.acquire();
		available.tryAcquire();

		return getNextAvailableItem();
	}
	
	public Object getItem(int locationId) throws InterruptedException { // no synch
		cat.debug("getItem");
		//available.acquire();
		available.tryAcquire();
		return getNextAvailableItem(locationId);
	}

	public void putItem(Object x) { // no synch
        cat.debug("putItem["+x+"]");
		if (markAsUnused(x))
			available.release();
	}

    public List<Object> getActiveItems()
    {
        ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0; i < MAX_AVAILABLE; ++i) 
        {
            if(used[i])
            {
                list.add(items[i]);
            }
        }
        return list;
    }

    public List<Object> getIdleItems()
    {
        ArrayList<Object> list = new ArrayList<Object>();
		for (int i = 0; i < MAX_AVAILABLE; ++i) 
        {
            if(!used[i])
            {
                list.add(items[i]);
            }
        }

        return list;
    }

	// Not a particularly efficient data structure; just for demo
	protected Object[] items = null;
	protected boolean[] used = null;

	protected synchronized Object getNextAvailableItem() 
    { 
		for (int i = 0; i < MAX_AVAILABLE; ++i) {
			if (!used[i]) {
				used[i] = true;
				return items[i];
			}
		}
		return null; // not reached 
	}
	
	protected synchronized Object getNextAvailableItem(int locationId) 
    { 
		for (int i = 0; i < MAX_AVAILABLE; ++i) {
			if (!used[i]) {
				if(((TerminalServerPort)items[i]).getLocationId() != null && ((TerminalServerPort)items[i]).getLocationId().equals(locationId)){
					used[i] = true;
					return items[i];
				}
			}
		}
		return null; // not reached 
	}

	protected synchronized boolean markAsUnused(Object item) 
    {
		for (int i = 0; i < MAX_AVAILABLE; ++i) 
        {
			if (item == items[i]) {
				if (used[i]) {
					used[i] = false;
					return true;
				} else {
					return false;
				}
			}
		}
		return false;
	}
}
