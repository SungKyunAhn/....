/**
 * Copyright (c) 2015, Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com >.
   All rights reserved.

   Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
   1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
   2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.
   3. The name of the author may not be used to endorse or promote products derived from this software without specific prior written permission.
   
   THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package com.aimir.fep.iot.domain.resources;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * ListOfResource domain.
 * @author <ul>
 *         <li>Sang June Lee < blue4444eye@hanmail.net > < blue4444eye@gmail.com ></li>
 *         </ul>
 */

@XmlRootElement(name="m2m:listOfResources")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ListOfResource implements Serializable {

	private static final long serialVersionUID = 1l;

	@XmlAttribute
	private String name;
	private String resultCode;
	private String resultMsg;
	private String accessToken;
	private String totalListCount;
	private String countPerPage;
	private String startIndex;
	private List<String> deviceIdList;
	
	@javax.xml.bind.annotation.XmlElement(name = "CSEBase")
	private CSEBase cseBase;
	
	@javax.xml.bind.annotation.XmlElement(name = "AE")
	private AE ae;
	
	@javax.xml.bind.annotation.XmlElement(name = "remoteCSE")
	private RemoteCSE remoteCSE;

	@javax.xml.bind.annotation.XmlElement(name = "container")
	private Container container;

	@javax.xml.bind.annotation.XmlElement(name = "contentInstance")
	private ContentInstance contentInstance;
	
	@javax.xml.bind.annotation.XmlElement(name = "contentInstance")
	private List<ContentInstance> contentInstanceList;

	@javax.xml.bind.annotation.XmlElement(name = "mgmtCmd")
	private MgmtCmd mgmtCmd;

	@javax.xml.bind.annotation.XmlElement(name = "execInstance")
	private ExecInstance execInstance;
	
	@javax.xml.bind.annotation.XmlElement(name = "deviceInfo")
	private DeviceInfo deviceInfo;
	
	@javax.xml.bind.annotation.XmlElement(name = "firmware")
	private Firmware firmware;
	
	@javax.xml.bind.annotation.XmlElement(name = "group")
	private Group group;
	
	@javax.xml.bind.annotation.XmlElement(name = "locationPolicy")
	private LocationPolicy locationPolicy;
	
	@javax.xml.bind.annotation.XmlElement(name = "node")
	private Node node;
	
	@javax.xml.bind.annotation.XmlElement(name = "software")
	private Software software;
	
	@javax.xml.bind.annotation.XmlElement(name = "subscription")
	private Subscription subscription;
	
	@javax.xml.bind.annotation.XmlElementWrapper(name = "mashupList")
	@javax.xml.bind.annotation.XmlElement(name = "mashup")
	private List<Mashup> mashupList; 
	
	@javax.xml.bind.annotation.XmlElement(name = "container")
	private List<ContainerOnlyId> containerList;

	@javax.xml.bind.annotation.XmlElement(name = "mgmtCmd")
	private List<MgmtCmdOnlyId> mgmtCmdList;
	
	@javax.xml.bind.annotation.XmlElement(name = "execInstance")
	private List<ExecInstance> execInstanceList;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTotalListCount() {
		return totalListCount;
	}

	public void setTotalListCount(String totalListCount) {
		this.totalListCount = totalListCount;
	}

	public String getCountPerPage() {
		return countPerPage;
	}

	public void setCountPerPage(String countPerPage) {
		this.countPerPage = countPerPage;
	}

	public String getStartIndex() {
		return startIndex;
	}

	public void setStartIndex(String startIndex) {
		this.startIndex = startIndex;
	}

	public List<String> getDeviceIdList() {
		return deviceIdList;
	}

	public void setDeviceIdList(List<String> deviceIdList) {
		this.deviceIdList = deviceIdList;
	}

	public CSEBase getCseBase() {
		return cseBase;
	}

	public void setCseBase(CSEBase cseBase) {
		this.cseBase = cseBase;
	}

	public AE getAe() {
		return ae;
	}

	public void setAe(AE ae) {
		this.ae = ae;
	}

	public RemoteCSE getRemoteCSE() {
		return remoteCSE;
	}

	public void setRemoteCSE(RemoteCSE remoteCSE) {
		this.remoteCSE = remoteCSE;
	}

	public Container getContainer() {
		return container;
	}

	public void setContainer(Container container) {
		this.container = container;
	}

	public ContentInstance getContentInstance() {
		return contentInstance;
	}

	public void setContentInstance(ContentInstance contentInstance) {
		this.contentInstance = contentInstance;
	}

	public List<ContentInstance> getContentInstanceList() {
		return contentInstanceList;
	}

	public void setContentInstanceList(List<ContentInstance> contentInstanceList) {
		this.contentInstanceList = contentInstanceList;
	}

	public MgmtCmd getMgmtCmd() {
		return mgmtCmd;
	}

	public void setMgmtCmd(MgmtCmd mgmtCmd) {
		this.mgmtCmd = mgmtCmd;
	}

	public ExecInstance getExecInstance() {
		return execInstance;
	}

	public void setExecInstance(ExecInstance execInstance) {
		this.execInstance = execInstance;
	}

	public DeviceInfo getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(DeviceInfo deviceInfo) {
		this.deviceInfo = deviceInfo;
	}

	public Firmware getFirmware() {
		return firmware;
	}

	public void setFirmware(Firmware firmware) {
		this.firmware = firmware;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	public LocationPolicy getLocationPolicy() {
		return locationPolicy;
	}

	public void setLocationPolicy(LocationPolicy locationPolicy) {
		this.locationPolicy = locationPolicy;
	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public Software getSoftware() {
		return software;
	}

	public void setSoftware(Software software) {
		this.software = software;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public List<Mashup> getMashupList() {
		return mashupList;
	}

	public void setMashupList(List<Mashup> mashupList) {
		this.mashupList = mashupList;
	}

	public List<ContainerOnlyId> getContainerList() {
		return containerList;
	}

	public void setContainerList(List<ContainerOnlyId> containerList) {
		this.containerList = containerList;
	}

	public List<MgmtCmdOnlyId> getMgmtCmdList() {
		return mgmtCmdList;
	}

	public void setMgmtCmdList(List<MgmtCmdOnlyId> mgmtCmdList) {
		this.mgmtCmdList = mgmtCmdList;
	}

	public List<ExecInstance> getExecInstanceList() {
		return execInstanceList;
	}

	public void setExecInstanceList(List<ExecInstance> execInstanceList) {
		this.execInstanceList = execInstanceList;
	}

	@Override
	public String toString() {
		return "CommonMApiVO [resultCode=" + resultCode + ", resultMsg=" + resultMsg + ", accessToken=" + accessToken + ", totalListCount=" + totalListCount + ", countPerPage=" + countPerPage + ", startIndex=" + startIndex + ", deviceIdList=" + deviceIdList + ", cseBase=" + cseBase + ", ae=" + ae + ", remoteCSE=" + remoteCSE + ", container=" + container
				+ ", contentInstance=" + contentInstance + ", contentInstanceList=" + contentInstanceList + ", mgmtCmd=" + mgmtCmd + ", execInstance=" + execInstance + ", deviceInfo=" + deviceInfo + ", firmware=" + firmware + ", group=" + group + ", locationPolicy=" + locationPolicy + ", node=" + node + ", software=" + software
				+ ", subscription=" + subscription + ", mashupList=" + mashupList + ", containerList=" + containerList + ", mgmtCmdList=" + mgmtCmdList + ", execInstanceList=" + execInstanceList + "]";
	}
	
}