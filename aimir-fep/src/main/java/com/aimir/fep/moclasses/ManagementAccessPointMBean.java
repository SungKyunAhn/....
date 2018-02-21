
package com.aimir.fep.moclasses;

import java.util.*;

public interface ManagementAccessPointMBean {
	public String getIpaddr();
	public Properties getProperty();
	public String getSNMPPort();
	public String getSNMPCommunity();
	public String getSNMPVersion();
	public void setIpaddr(String ipaddr);
	public void setProperty(Properties property);
	public void setSNMPPort(String port);
	public void setSNMPCommunity(String community);
	public void setSNMPVersion(String version);
	public void printIpaddr();
}
