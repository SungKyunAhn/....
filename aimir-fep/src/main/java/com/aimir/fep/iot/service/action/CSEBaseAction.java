package com.aimir.fep.iot.service.action;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.common.RSCException;
import com.aimir.fep.iot.domain.resources.CSEBase;
import com.aimir.fep.iot.request.ReqCSEBase;
import com.aimir.fep.iot.request.Request;
import com.aimir.fep.iot.service.CSEBaseService;
import com.aimir.fep.iot.utils.CommonCode.RSC;
import com.aimir.fep.protocol.fmp.processor.ProcessorHandler;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.fep.iot.utils.MessageProperties;
import com.aimir.fep.util.DataUtil;

@Service
@Scope("prototype")
public class CSEBaseAction implements ActionService {
	private static Log logger = LogFactory.getLog(CSEBaseAction.class);
	
	@Autowired
	CSEBaseService cSEBaseService;
	
	@Override
	public Response create(Request req) throws Exception {
		return null;
	}

	@Override
	public Response retrive(Request req) throws Exception {
		String message = null;
		OperationUtilAction operUtil = req.getOperUtil();
		HttpServletRequest request = req.getRequest();
		
		String seq = req.getSeq();
		String cseBaseId = ((ReqCSEBase)req).getCseBase();
		
		String from 	= CommonUtil.nvl(request.getHeader("X-M2M-Origin"), "");
		String requestID= CommonUtil.nvl(request.getHeader("X-M2M-RI"), "");
		
		if(CommonUtil.isEmpty(from)) {
			message = MessageProperties.getProperty("aimir.iot.origin.empty");
		} else if(CommonUtil.isEmpty(requestID)) {
			message = MessageProperties.getProperty("aimir.iot.ri.empty");
		} else if(CommonUtil.isEmpty(cseBaseId)) {
			message = MessageProperties.getProperty("aimir.iot.cseId.empty");
		}
		
		if(message != null) {
			return operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
		}
				
		CSEBase cseBase = cSEBaseService.findOneCSEBaseByCSEID(cseBaseId);
		if(CommonUtil.isEmpty(cseBase)) {
			message = MessageProperties.getProperty("aimir.iot.cseId.empty");
			return operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
		}
		
		logger.info("["+seq+"] cseBase retrive success!");
		return operUtil.setResponse(request, Response.Status.OK, operUtil.getObject2Txt(request, cseBase), RSC.OK, cseBase);
	}

	@Override
	public Response update(Request req) throws Exception {
		return null;
	}

	@Override
	public Response delete(Request req) throws Exception {
		return null;
	}

}
