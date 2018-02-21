
package com.aimir.fep.moclasses;

import java.util.Properties;

import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ManagementAccessPoint implements ManagementAccessPointMBean, MBeanRegistration {
	private static Log logger = LogFactory.getLog(ManagementAccessPoint.class);

	private static long mbeanSize; //MBean
	@SuppressWarnings("unused")
	private MBeanServer server = null;
	@SuppressWarnings("unused")
	private ObjectName objectName = null;

	String ipaddr;
	Properties property = null;

	public ManagementAccessPoint() { 

	}

	public ManagementAccessPoint(String ipaddr, Properties p) { 
		this.ipaddr = ipaddr;
		this.property = p;
	}

	public String getIpaddr() {
		return ipaddr;
	}

	public Properties getProperty() {
		return property;
	}

	public String getSNMPPort() {
		String port = null;
		String length = property.getProperty("port.size");
	
		if (Integer.parseInt(length) == 1) {
			port = property.getProperty("port.0");
		} else {
			logger.debug("getSNMPPort: length != 1");
			port = new String("161");
		}

		return port;
	}
	
	public String getSNMPCommunity() {
		String community = null;
		String length = property.getProperty("community.size");
	
		if (Integer.parseInt(length) == 1) {
			community = property.getProperty("community.0");
		} else {
			logger.debug("getSNMPCommunity: length != 1");
			community = new String("public");
		}

		return community;
	}

	public String getSNMPVersion() {
		String version = null;
		String length = property.getProperty("version.size");
	
		if (Integer.parseInt(length) == 1) {
			version = (String)property.getProperty("version.0");
		} else {
			logger.debug("getSNMPVersion: length != 1");
			version = new String("1");
		}

		return version;
	}

	public void setIpaddr(String ipaddr) {
		this.ipaddr = ipaddr;
	}
	
	public void setProperty(Properties property) {
		this.property = property;
	}

	public void setSNMPPort(String port) {
		property.setProperty("port.size", "1");
		property.setProperty("port.0", port);
	}
	
	public void setSNMPCommunity(String community) {
		property.setProperty("community.size", "1");
		property.setProperty("community.0", community);
	}
	
	public void setSNMPVersion(String version) {
		property.setProperty("version.size", "1");
		property.setProperty("version.0", version);
	}

	public void printIpaddr() {
		logger.info("ipaddr=" + ipaddr); 
	}
	
	public void postRegister(Boolean registrationDone) {
	}

	public void preDeregister() {}

	public void postDeregister() {
		mbeanSize--;
	}

	public ObjectName preRegister(MBeanServer server, ObjectName name) {
		this.server = server;
		this.objectName = name;
		mbeanSize++;
		return name;
	}
	
	public static long getMBeanSize() {
		return mbeanSize;
	}
	
}

