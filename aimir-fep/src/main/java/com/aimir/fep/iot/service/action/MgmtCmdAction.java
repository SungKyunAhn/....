package com.aimir.fep.iot.service.action;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.resources.CSEBase;
import com.aimir.fep.iot.domain.resources.MgmtCmd;
import com.aimir.fep.iot.domain.resources.RemoteCSE;
import com.aimir.fep.iot.request.ReqMgmtCmd;
import com.aimir.fep.iot.request.Request;
import com.aimir.fep.iot.service.CSEBaseService;
import com.aimir.fep.iot.service.MgmtCmdService;
import com.aimir.fep.iot.service.RemoteCseService;
import com.aimir.fep.iot.utils.CommonCode.RSC;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.fep.iot.utils.MessageProperties;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

@Service
@Scope("prototype")
public class MgmtCmdAction implements ActionService {
	private static Log logger = LogFactory.getLog(MgmtCmdAction.class);
	
	@Autowired
	CSEBaseService cseBaseService;
	
	@Autowired
	RemoteCseService remoteCseService;
	
	@Autowired
	MgmtCmdService mgmtCmdService;
	
	@Autowired
	ProcessorHandler processorHandler;
	
	@Override
	public Response create(Request req) throws Exception {
		MgmtCmd reqMgmtCmd = null;
		String message = null;
		
		OperationUtilAction operUtil = req.getOperUtil();
		HttpServletRequest request = req.getRequest();
		String seq = req.getSeq();
		String cseBaseId = ((ReqMgmtCmd)req).getCseBase();
		String remoteCSEName = ((ReqMgmtCmd)req).getRemoteCSEName();
		
		logger.debug("### ["+seq+"] mgmtCmd Create start! ###");
		
		reqMgmtCmd = (MgmtCmd)operUtil.getBodyObject(seq, request, MgmtCmd.class);
		String from 	= CommonUtil.nvl(request.getHeader("X-M2M-Origin"), "");
		String name 	= CommonUtil.nvl(request.getHeader("X-M2M-NM"), "");
		String requestID= CommonUtil.nvl(request.getHeader("X-M2M-RI"), "");
		String dkey	= CommonUtil.nvl(request.getHeader("dKey"), "");
		
		//1. 입력 파라미터 유효성 체크
		if(CommonUtil.isEmpty(from)) {
			message = MessageProperties.getProperty("aimir.iot.origin.empty");
		} else if(CommonUtil.isEmpty(requestID)) {
			message = MessageProperties.getProperty("aimir.iot.ri.empty");
			return operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
		} else if(CommonUtil.isEmpty(cseBaseId)) {
			message = MessageProperties.getProperty("aimir.iot.cseId.empty");
		} else if(CommonUtil.isEmpty(remoteCSEName)) {
			message = MessageProperties.getProperty("aimir.iot.csr.name.empty");
		} else if(CommonUtil.isEmpty(reqMgmtCmd)) {
			message = MessageProperties.getProperty("aimir.iot.mgmtCmd.empty");
		} else {
			if(CommonUtil.isEmpty(reqMgmtCmd.getCmdType()) || CommonUtil.isEmpty(reqMgmtCmd.getExecTarget())) {
				message = MessageProperties.getProperty("aimir.iot.request.parameter.empty");
			}
		}
		
		if(message != null) {
			logger.debug("["+seq+"] requeset parameter error!");
			return operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
		}
		
		//2. cseBase Resource 유효성 체크
		CSEBase cseBase = cseBaseService.findOneCSEBaseByCSEID(cseBaseId);
		if(CommonUtil.isEmpty(cseBase)) {
			logger.debug("["+seq+"] CSEBase emtpy!");
			message = cseBaseId + MessageProperties.getProperty("aimir.iot.cse.empty");
			return operUtil.setResponse(request, Response.Status.NOT_FOUND, message, RSC.NOT_FOUND, null);
		}
		
		//3. remoteCSE Resource 유효성 체크
		RemoteCSE remoteCSE = remoteCseService.findOneRemoteCSEByResoureName(remoteCSEName);
		if(CommonUtil.isEmpty(remoteCSE)) {
			logger.debug("["+seq+"] RemoteCSE emtpy!");
			message = cseBaseId + MessageProperties.getProperty("aimir.iot.csr.empty");
			return operUtil.setResponse(request, Response.Status.NOT_FOUND, message, RSC.NOT_FOUND, null);
		}
		
		//4, mgmtCmd Resource 유효성 체크
		if(!CommonUtil.isEmpty(name)) {
			MgmtCmd mgmtCmd = mgmtCmdService.findOneMgmtCmdByResourceName(seq, remoteCSE.getResourceID(), name);
			if(!CommonUtil.isEmpty(mgmtCmd)) {
				logger.debug("["+seq+"] device mgmtCmd already register!");
				return operUtil.setResponse(request, Response.Status.FORBIDDEN, operUtil.getObject2Txt(request, mgmtCmd), RSC.ALREADY_EXISTS, mgmtCmd);
			}
		}
		
		reqMgmtCmd.setResourceName(name);
		mgmtCmdService.insert(seq, remoteCSE, reqMgmtCmd);
		
		reqMgmtCmd.setSeq(seq);
		processorHandler.putServiceData(ProcessorHandler.SERVICE_H_MDMSData, reqMgmtCmd);
		
		logger.info("### ["+seq+"] device mgmtCmd register success! ###");
		return operUtil.setResponse(request, Response.Status.CREATED, operUtil.getObject2Txt(request, reqMgmtCmd), RSC.CREATED, reqMgmtCmd);
	}

	@Override
	public Response retrive(Request req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response update(Request req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Response delete(Request req) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
