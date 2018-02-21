package com.aimir.fep.iot.request;

import javax.servlet.http.HttpServletRequest;

import com.aimir.fep.iot.service.action.OperationUtilAction;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;

public class ReqContentInstance extends Request {

	private String cseBase;
	private String remoteCSEName;
	private String containerName;
	private String contentInstanceName;
	private String labelTag;
	
	public ReqContentInstance(String cseBase, String remoteCSEName, String containerName,
			String seq, HttpServletRequest request, RESOURCE_TYPE resoureType,
			OperationUtilAction operUtil) {
		super(seq, request, resoureType, operUtil);
		this.cseBase = cseBase;
		this.remoteCSEName = remoteCSEName;
		this.containerName = containerName;
	}
	
	public ReqContentInstance(String cseBase, String remoteCSEName, String containerName, String contentInstanceName,
			String seq, HttpServletRequest request, OperationUtilAction operUtil) {
		super(seq, request, RESOURCE_TYPE.UNKNOW, operUtil);
		this.cseBase = cseBase;
		this.remoteCSEName = remoteCSEName;
		this.containerName = containerName;
		this.contentInstanceName = contentInstanceName;
	}
	
	public ReqContentInstance(String cseBase, String remoteCSEName, String containerName, String contentInstanceName, String labelTag,
			String seq, HttpServletRequest request, OperationUtilAction operUtil) {
		super(seq, request, RESOURCE_TYPE.UNKNOW, operUtil);
		this.cseBase = cseBase;
		this.remoteCSEName = remoteCSEName;
		this.containerName = containerName;
		this.contentInstanceName = contentInstanceName;
		this.labelTag = labelTag;
	}
	
	public String getCseBase() {
		return cseBase;
	}
	public void setCseBase(String cseBase) {
		this.cseBase = cseBase;
	}
	public String getRemoteCSEName() {
		return remoteCSEName;
	}
	public void setRemoteCSEName(String remoteCSEName) {
		this.remoteCSEName = remoteCSEName;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

	public String getContentInstanceName() {
		return contentInstanceName;
	}

	public void setContentInstanceName(String contentInstanceName) {
		this.contentInstanceName = contentInstanceName;
	}

	public String getLabelTag() {
		return labelTag;
	}

	public void setLabelTag(String labelTag) {
		this.labelTag = labelTag;
	}
	
}
