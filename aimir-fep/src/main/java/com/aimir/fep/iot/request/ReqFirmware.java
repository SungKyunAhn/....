package com.aimir.fep.iot.request;

import javax.servlet.http.HttpServletRequest;

import com.aimir.fep.iot.service.action.OperationUtilAction;
import com.aimir.fep.iot.utils.CommonCode.MGMT_DEFINITION;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;

public class ReqFirmware extends ReqNode {

	private MGMT_DEFINITION mgmtDefinition;
	
	public ReqFirmware(MGMT_DEFINITION mgmtDefinition, String cseBase, String nodeName, String seq, HttpServletRequest request,
			RESOURCE_TYPE resoureType, OperationUtilAction operUtil) {
		super(cseBase, nodeName, seq, request, resoureType, operUtil);
		this.mgmtDefinition = mgmtDefinition;
	}

	public MGMT_DEFINITION getMgmtDefinition() {
		return mgmtDefinition;
	}

	public void setMgmtDefinition(MGMT_DEFINITION mgmtDefinition) {
		this.mgmtDefinition = mgmtDefinition;
	}

}
