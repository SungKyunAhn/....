package com.aimir.fep.bypass;

public interface BypassAdapterMBean {
	public void start() throws Exception;
	public void stop();
	public String getName();
	public String getState();
	public int getPort();
}
