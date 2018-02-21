package com.aimir.fep.iot.domain.resources;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlElement;

public class ABCD {
    protected BigInteger resourceType;
    protected String resourceID;
    protected String resourceName;
    
	public BigInteger getResourceType() {
		return resourceType;
	}
	public void setResourceType(BigInteger resourceType) {
		this.resourceType = resourceType;
	}
	public String getResourceID() {
		return resourceID;
	}
	public void setResourceID(String resourceID) {
		this.resourceID = resourceID;
	}
	public String getResourceName() {
		return resourceName;
	}
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}
    
    
}
