package com.aimir.fep.iot.request;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.aimir.fep.iot.service.action.OperationUtilAction;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;

public class Request implements Serializable {

	private String seq;
	private HttpServletRequest request;
	private RESOURCE_TYPE resoureType;
	private OperationUtilAction operUtil;
	
	public Request(String seq, HttpServletRequest request, 
			RESOURCE_TYPE resoureType, OperationUtilAction operUtil) {
		this.seq = seq;
		this.request = request;
		this.resoureType = resoureType;
		this.operUtil = operUtil;
	}
	
	public String getSeq() {
		return seq;
	}
	
	public void setSeq(String seq) {
		this.seq = seq;
	}
	
	public HttpServletRequest getRequest() {
		return request;
	}
	
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public RESOURCE_TYPE getResoureType() {
		return resoureType;
	}
	
	public void setResoureType(RESOURCE_TYPE resoureType) {
		this.resoureType = resoureType;
	}

	public OperationUtilAction getOperUtil() {
		return operUtil;
	}

	public void setOperUtil(OperationUtilAction operUtil) {
		this.operUtil = operUtil;
	}
	
}
