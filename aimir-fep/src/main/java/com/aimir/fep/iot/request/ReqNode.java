package com.aimir.fep.iot.request;

import javax.servlet.http.HttpServletRequest;

import com.aimir.fep.iot.service.action.OperationUtilAction;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;

public class ReqNode extends Request {

	private String cseBase;
	private String nodeName;
	
	public ReqNode(String cseBase, String nodeName, String seq, HttpServletRequest request, 
			RESOURCE_TYPE resoureType, OperationUtilAction operUtil) {
		super(seq, request, resoureType, operUtil);
		this.cseBase = cseBase;
		this.nodeName = nodeName;
	}

	public String getCseBase() {
		return cseBase;
	}

	public void setCseBase(String cseBase) {
		this.cseBase = cseBase;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	
}
