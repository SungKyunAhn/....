package com.aimir.fep.iot.request;

import javax.servlet.http.HttpServletRequest;

import com.aimir.fep.iot.service.action.OperationUtilAction;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;

public class ReqRemoteCse extends Request {
	
	private String cseBase;
	private String remoteCseName;
	
	public ReqRemoteCse(String cseBase, String seq, HttpServletRequest request, RESOURCE_TYPE resoureType, OperationUtilAction operUtil) {
		super(seq, request, resoureType, operUtil);
		this.cseBase = cseBase;
	}
	
	public ReqRemoteCse(String cseBase, String remoteCseName, String seq, HttpServletRequest request, OperationUtilAction operUtil) {
		super(seq, request, RESOURCE_TYPE.UNKNOW, operUtil);
		this.cseBase = cseBase;
		this.remoteCseName = remoteCseName;
	}
	
	public String getCseBase() {
		return cseBase;
	}

	public void setCseBase(String cseBase) {
		this.cseBase = cseBase;
	}

	public String getRemoteCseName() {
		return remoteCseName;
	}

	public void setRemoteCseName(String remoteCseName) {
		this.remoteCseName = remoteCseName;
	}
	
}
