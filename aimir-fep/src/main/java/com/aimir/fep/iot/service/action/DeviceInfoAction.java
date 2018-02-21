package com.aimir.fep.iot.service.action;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.resources.CSEBase;
import com.aimir.fep.iot.domain.resources.DeviceInfo;
import com.aimir.fep.iot.domain.resources.Node;
import com.aimir.fep.iot.request.ReqDeviceInfo;
import com.aimir.fep.iot.request.Request;
import com.aimir.fep.iot.service.CSEBaseService;
import com.aimir.fep.iot.service.DeviceInfoService;
import com.aimir.fep.iot.service.NodeService;
import com.aimir.fep.iot.utils.CommonCode.RSC;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.fep.iot.utils.MessageProperties;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;

@Service
@Scope("prototype")
public class DeviceInfoAction implements ActionService {
	private static Log logger = LogFactory.getLog(DeviceInfoAction.class);
	
	@Autowired
	CSEBaseService cseBaseService;
	
	@Autowired
	NodeService nodeService;
	
	@Autowired
	DeviceInfoService deviceInfoService;
	
	@Autowired
	ProcessorHandler processorHandler;
	
	@Override
	public Response create(Request req) throws Exception {
		DeviceInfo reqDeviceInfo = null;
		String message = null;
		
		OperationUtilAction operUtil = req.getOperUtil();
		HttpServletRequest request = req.getRequest();
		String seq = req.getSeq();
		
		logger.debug("### ["+seq+"] DeviceInfo Create start! ###");
		
		String cseBaseId = ((ReqDeviceInfo)req).getCseBase();
		String nodeName = ((ReqDeviceInfo)req).getNodeName();
		
		reqDeviceInfo = (DeviceInfo)operUtil.getBodyObject(seq, request, DeviceInfo.class);
		String from 	= CommonUtil.nvl(request.getHeader("X-M2M-Origin"), "");
		String name 	= CommonUtil.nvl(request.getHeader("X-M2M-NM"), "");
		String requestID= CommonUtil.nvl(request.getHeader("X-M2M-RI"), "");
		String dkey	= CommonUtil.nvl(request.getHeader("dKey"), "");
		
		//1. 입력 파라미터 유효성 체크
		if(CommonUtil.isEmpty(from)) {
			message = MessageProperties.getProperty("aimir.iot.origin.empty");
		} else if(CommonUtil.isEmpty(requestID)) {
			message = MessageProperties.getProperty("aimir.iot.ri.empty");
		} else if(CommonUtil.isEmpty(cseBaseId)) {
			message = MessageProperties.getProperty("aimir.iot.cseId.empty");
		} else if(CommonUtil.isEmpty(nodeName)) {
			message = MessageProperties.getProperty("aimir.iot.node.name.empty");
		} else if(CommonUtil.isEmpty(reqDeviceInfo)) {
			message = MessageProperties.getProperty("aimir.iot.deviceInfo.empty");
		} 
		
		if(message != null) {
			logger.debug("["+seq+"] requeset parameter error!");
			return operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
		}
		
		//2.cseBase DB 유효성 체크
		CSEBase cseBase = cseBaseService.findOneCSEBaseByCSEID(cseBaseId);
		if(CommonUtil.isEmpty(cseBase)) {
			logger.debug("["+seq+"] CSEBase emtpy!");
			message = cseBaseId +" "+MessageProperties.getProperty("aimir.iot.cse.empty");
			return operUtil.setResponse(request, Response.Status.NOT_FOUND, message, RSC.NOT_FOUND, null);
		}
		
		//3.Node DB 유효성 체크
		Node node = nodeService.findOneNodeByResourceName(seq, nodeName);
		if(CommonUtil.isEmpty(node)) {
			logger.debug("["+seq+"] Node emtpy!");
			message = cseBaseId +" "+MessageProperties.getProperty("aimir.iot.node.empty");
			return operUtil.setResponse(request, Response.Status.NOT_FOUND, message, RSC.NOT_FOUND, null);
		}
		
		//4. DeviceInfo DB 유효성 체크
		if(!CommonUtil.isEmpty(name)) {
			DeviceInfo deviceInfo = deviceInfoService.findOneDeviceInfoByResourceName(seq, node.getResourceID(), name);
			if(!CommonUtil.isEmpty(deviceInfo)) {
				logger.debug("["+seq+"] device Info already register!");
				return operUtil.setResponse(request, Response.Status.FORBIDDEN, operUtil.getObject2Txt(request, deviceInfo), RSC.ALREADY_EXISTS, deviceInfo);
			}
		}
		
		reqDeviceInfo.setResourceName(name);
		reqDeviceInfo = deviceInfoService.insert(seq, node, reqDeviceInfo);
		
		reqDeviceInfo.setSeq(seq);
		reqDeviceInfo.setParentID(from);
		
		//MDMS log create
		processorHandler.putServiceIoTData(ProcessorHandler.SERVICE_H_MDMSData, reqDeviceInfo);
		
		//FepD send Message
		processorHandler.putServiceIoTData(ProcessorHandler.SERVICE_IOT_MDDATA, reqDeviceInfo);
		
		logger.info("### ["+seq+"] DeviceInfo register success! ###");
		return operUtil.setResponse(request, Response.Status.CREATED, operUtil.getObject2Txt(request, reqDeviceInfo), RSC.CREATED, reqDeviceInfo);
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
