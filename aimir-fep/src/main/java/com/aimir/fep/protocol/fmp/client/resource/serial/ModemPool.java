package com.aimir.fep.protocol.fmp.client.resource.serial;

import com.aimir.fep.protocol.fmp.client.resource.Pool;

public class ModemPool
{
    private String devName = null;
    private Pool pool = null;

    public ModemPool()
    {
    }

    public ModemPool(Pool pool, String devName)
    {
        this.pool = pool;
        this.devName = devName;
    }

    public Pool getPool()
    {
        return this.pool;
    }

    public void setPool(Pool pool)
    {
        this.pool = pool;
    }

	public String getDevName() {
		return devName;
	}

	public void setDevName(String devName) {
		this.devName = devName;
	}
}
