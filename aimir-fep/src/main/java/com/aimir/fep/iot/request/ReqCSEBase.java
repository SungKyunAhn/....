package com.aimir.fep.iot.request;

import javax.servlet.http.HttpServletRequest;

import com.aimir.fep.iot.service.action.OperationUtilAction;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;

public class ReqCSEBase extends Request {
	
	private String cseBase;

	public ReqCSEBase(String cseBase, String seq, HttpServletRequest request, RESOURCE_TYPE resoureType, OperationUtilAction operUtil) {
		super(seq, request, resoureType, operUtil);
		this.cseBase = cseBase;
	}
	
	public ReqCSEBase(String cseBase, String seq, HttpServletRequest request, OperationUtilAction operUtil) {
		super(seq, request, RESOURCE_TYPE.UNKNOW, operUtil);
		this.cseBase = cseBase;
	}
	
	public String getCseBase() {
		return cseBase;
	}

	public void setCseBase(String cseBase) {
		this.cseBase = cseBase;
	}
	
}
