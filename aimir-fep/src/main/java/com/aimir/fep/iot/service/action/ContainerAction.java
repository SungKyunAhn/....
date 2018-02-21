package com.aimir.fep.iot.service.action;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.resources.CSEBase;
import com.aimir.fep.iot.domain.resources.Container;
import com.aimir.fep.iot.domain.resources.RemoteCSE;
import com.aimir.fep.iot.request.ReqContainer;
import com.aimir.fep.iot.request.Request;
import com.aimir.fep.iot.service.CSEBaseService;
import com.aimir.fep.iot.service.ContainerService;
import com.aimir.fep.iot.service.RemoteCseService;
import com.aimir.fep.iot.utils.CommonCode.RSC;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.fep.iot.utils.MessageProperties;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

@Service
@Scope("prototype")
public class ContainerAction implements ActionService{

	private static Log logger = LogFactory.getLog(ContainerAction.class);

	@Autowired
	CSEBaseService cseBaseService;
	
	@Autowired
	RemoteCseService remoteCseService;
	
	@Autowired
	ContainerService containerService;
	
	@Autowired
	ProcessorHandler processorHandler;
	
	@Override
	public Response create(Request req) throws Exception {
		Container reqContainer = null;
		String message = null;
		
		OperationUtilAction operUtil = req.getOperUtil();
		HttpServletRequest request = req.getRequest();
		String seq = req.getSeq();
		String cseBaseId = ((ReqContainer)req).getCseBase();
		String remoteCSEName = ((ReqContainer)req).getRemoteCSEName();
		
		logger.debug("### ["+seq+"] Container Create start! ###");
		reqContainer = (Container)operUtil.getBodyObject(seq, request, Container.class);
		String from 	= CommonUtil.nvl(request.getHeader("X-M2M-Origin"), "");
		String name 	= CommonUtil.nvl(request.getHeader("X-M2M-NM"), "");
		String requestID= CommonUtil.nvl(request.getHeader("X-M2M-RI"), "");
		String passCode	= CommonUtil.nvl(request.getHeader("passCode"), "");
		
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
		} else if(CommonUtil.isEmpty(reqContainer)) {
			message = MessageProperties.getProperty("aimir.iot.container.empty");
		}  else if(!CommonUtil.getExpirationTimeValidation(reqContainer.getExpirationTime())) {
			message = MessageProperties.getProperty("aimir.iot.expirationTime.fail");
		} /*else if(CommonUtil.isEmpty(name)) {
			//Mobius 1.0에서는 container 등록시 반드시 name 필요.
			//추후 확장성 고려시 해당 부분 수정 필요(oasis에서는 서버에서 resourceName 지정)
			message = MessageProperties.getProperty("aimir.iot.container.name.empty");
		}*/
		
		if(message != null) {
			logger.debug("["+seq+"] requeset parameter error!");
			return operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
		}
		
		//2. parent Resource 유효성 체크
		//2-1 cseBase Resource 유효성 체크
		CSEBase cseBase = cseBaseService.findOneCSEBaseByCSEID(cseBaseId);
		if(CommonUtil.isEmpty(cseBase)) {
			logger.debug("["+seq+"] CSEBase emtpy!");
			message = cseBaseId + MessageProperties.getProperty("aimir.iot.cse.empty");
			return operUtil.setResponse(request, Response.Status.NOT_FOUND, message, RSC.NOT_FOUND, null);
		}
		
		//2-2 remoteCSE Resource 유효성 체크
		RemoteCSE remoteCSE = remoteCseService.findOneRemoteCSEByResoureName(remoteCSEName);
		if(CommonUtil.isEmpty(remoteCSE)) {
			logger.debug("["+seq+"] RemoteCSE emtpy!");
			message = cseBaseId + MessageProperties.getProperty("aimir.iot.csr.empty");
			return operUtil.setResponse(request, Response.Status.NOT_FOUND, message, RSC.NOT_FOUND, null);
		}
		
		//2-3 container Resource 유효성 체크
		Container container = containerService.findOneContainerByResourceName(remoteCSE.getResourceID(), name);
		if(!CommonUtil.isEmpty(container)) {
			//등록된 디바이스 컨테이너
			logger.info("["+seq+"] device container already register!");
			return operUtil.setResponse(request, Response.Status.FORBIDDEN, operUtil.getObject2Txt(request, container), RSC.ALREADY_EXISTS, container);
		}
		
		//3.Container 추가
		reqContainer.setResourceName(name);
		containerService.insert(seq, request.getRequestURI(), remoteCSE, reqContainer);
		logger.info("### ["+seq+"] device container register success! ###");
		
		reqContainer.setSeq(seq);
		processorHandler.putServiceData(ProcessorHandler.SERVICE_H_MDMSData, reqContainer);
		
		return operUtil.setResponse(request, Response.Status.CREATED, operUtil.getObject2Txt(request, reqContainer), RSC.CREATED, reqContainer);
	}

	@Override
	public Response retrive(Request req) throws Exception {
		Container reqContainer = null;
		String message = null;
		
		OperationUtilAction operUtil = req.getOperUtil();
		HttpServletRequest request = req.getRequest();
		String seq = req.getSeq();
		String cseBaseId = ((ReqContainer)req).getCseBase();
		String remoteCSEName = ((ReqContainer)req).getRemoteCSEName();
		String containerName = ((ReqContainer)req).getContainerName();
		
		String from 	= CommonUtil.nvl(request.getHeader("X-M2M-Origin"), "");
		String requestID= CommonUtil.nvl(request.getHeader("X-M2M-RI"), "");
		
		if(CommonUtil.isEmpty(from)) {
			message = MessageProperties.getProperty("aimir.iot.origin.empty");
		} else if(CommonUtil.isEmpty(requestID)) {
			message = MessageProperties.getProperty("aimir.iot.ri.empty");
		} else if(CommonUtil.isEmpty(cseBaseId)) {
			message = MessageProperties.getProperty("aimir.iot.cseId.empty");
		} else if(CommonUtil.isEmpty(remoteCSEName)) {
			message = MessageProperties.getProperty("aimir.iot.csr.name.empty");
		} else if(CommonUtil.isEmpty(containerName)) {
			message = MessageProperties.getProperty("aimir.iot.container.name.empty");
		}
		
		if(message != null) {
			logger.debug("["+seq+"] requeset parameter error!");
			return operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
		}
		
		//cseBase 유효성 체크
		CSEBase cseBase = cseBaseService.findOneCSEBaseByCSEID(cseBaseId);
		if(CommonUtil.isEmpty(cseBase)) {
			logger.debug("["+seq+"] CSEBase emtpy!");
			message = MessageProperties.getProperty("aimir.iot.cseId.empty");
			return operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
		}
		
		//remoteCSE 유효성 체크
		RemoteCSE remoteCSE = remoteCseService.findOneRemoteCSEByResoureName(remoteCSEName);
		if(CommonUtil.isEmpty(remoteCSE)) {
			logger.debug("["+seq+"] RemoteCSE emtpy!");
			message = MessageProperties.getProperty("aimir.iot.csr.empty");
			return operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
		}
		
		//container 유효성 체크
		Container container = containerService.findOneContainerByResourceName(remoteCSE.getResourceID(), containerName);
		if(CommonUtil.isEmpty(container)) {
			logger.debug("["+seq+"] Container emtpy!");
			message = MessageProperties.getProperty("aimir.iot.container.empty");
			return operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
		}
		
		logger.info("["+seq+"] remotecse retrive success!");
		return operUtil.setResponse(request, Response.Status.OK, operUtil.getObject2Txt(request, container), RSC.OK, container);
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
