package com.aimir.fep.protocol.rapa;

import java.io.Serializable;

/** 
 * class GwRosterDetailVo.java
 *
 * @author ky.yoon@nuritelecom.com
 * @version 1.0 $Date: 2016. 11. 21.
 */
public class GwRosterDetailVo implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5501219490306173L;

	private String id;
	private String link;
	private String pocs;
	private String mgmtProtocolType;
	private String onlineStatus;
	private String serverCapability;
	private String locTargetDevice;
	private String creationTime;
	private String lastModifiedTime;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getPocs() {
		return pocs;
	}
	public void setPocs(String pocs) {
		this.pocs = pocs;
	}
	public String getMgmtProtocolType() {
		return mgmtProtocolType;
	}
	public void setMgmtProtocolType(String mgmtProtocolType) {
		this.mgmtProtocolType = mgmtProtocolType;
	}	
	public String getOnlineStatus() {
		return onlineStatus;
	}
	public void setOnlineStatus(String onlineStatus) {
		this.onlineStatus = onlineStatus;
	}
	public String getServerCapability() {
		return serverCapability;
	}
	public void setServerCapability(String serverCapability) {
		this.serverCapability = serverCapability;
	}
	public String getLocTargetDevice() {
		return locTargetDevice;
	}
	public void setLocTargetDevice(String locTargetDevice) {
		this.locTargetDevice = locTargetDevice;
	}
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	public String getLastModifiedTime() {
		return lastModifiedTime;
	}
	public void setLastModifiedTime(String lastModifiedTime) {
		this.lastModifiedTime = lastModifiedTime;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}	
}
