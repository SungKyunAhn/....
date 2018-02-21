package com.aimir.fep.command.mbean;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;

import com.aimir.fep.iot.request.ReqCSEBase;
import com.aimir.fep.iot.request.ReqContainer;
import com.aimir.fep.iot.request.ReqContentInstance;
import com.aimir.fep.iot.request.ReqRemoteCse;
import com.aimir.fep.iot.service.action.ActionManager;
import com.aimir.fep.iot.service.action.OperationUtilAction;
import com.aimir.fep.iot.service.snowflake.BasicEntityIdGenerator;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;
import com.aimir.fep.iot.utils.CommonCode.RSC;

@Produces({"application/xml", "application/json"})
@Component
public class CommandGetService extends CommandService{
	private static Log logger = LogFactory.getLog(CommandGetService.class);
	
	@SuppressWarnings("finally")
	@GET
	@Path("/{cseBase}")	
	@ResponseBody
	public Response cmdCSEBaseRetrieve(@Context HttpServletRequest request, 
			@PathParam("cseBase") String cseBase) {
		String seq = "";
		OperationUtilAction operUtil = null;
		Response response = null;
		
		try{
			seq = BasicEntityIdGenerator.getInstance().generateLongId();
			operUtil = ActionManager.getInstance().getOperationBean();
			logger.info("### ["+seq+"] cmdCSEBaseRetrieve IN | parameter | cseBase:"+cseBase+" ###");
			
			ReqCSEBase req = new ReqCSEBase(cseBase, seq, request, operUtil);
			response = ActionManager.getInstance().getBean(RESOURCE_TYPE.CSE_BASE).retrive(req);
		}catch(Exception e) {
			logger.error(e, e);
			response = operUtil.setResponse(request, Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), RSC.INTERNAL_SERVER_ERROR, null);
		}finally {
			getResponseToString(seq, "commonCSEBaseNodeBottomCreate", response);
			return response;
		}
	}
	
	@SuppressWarnings("finally")
	@GET
	@Path("/{cseBase}/remoteCSE-{remoteCSEName}")	
	@ResponseBody
	public Response cmdRemoteCSERetrieve(@Context HttpServletRequest request,
			@PathParam("cseBase") String cseBase, @PathParam("remoteCSEName") String remoteCSEName) {
		String seq = "";
		OperationUtilAction operUtil = null;
		Response response = null;
		
		try {
			seq = BasicEntityIdGenerator.getInstance().generateLongId();
			operUtil = ActionManager.getInstance().getOperationBean();
			logger.info("### ["+seq+"] cmdRemoteCSERetrieve IN | parameter | cseBase:"+cseBase+" ###");
			
			ReqRemoteCse req = new ReqRemoteCse(cseBase, remoteCSEName, seq, request, operUtil);
			response = ActionManager.getInstance().getBean(RESOURCE_TYPE.REMOTE_CSE).retrive(req);
		}catch(Exception e) {
			logger.error(e, e);
			response = operUtil.setResponse(request, Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), RSC.INTERNAL_SERVER_ERROR, null);
		}finally {
			getResponseToString(seq, "commonCSEBaseNodeBottomCreate", response);
			return response;
		}
	}
	
	@SuppressWarnings("finally")
	@GET
	@Path("/{cseBase}/remoteCSE-{remoteCSEName}/container-{containerName}")
	@ResponseBody
	public Response cmdContainerRetrieve(@Context HttpServletRequest request,
			@PathParam("cseBase") String cseBase, @PathParam("remoteCSEName") String remoteCSEName, @PathParam("containerName") String containerName) {
		String seq = "";
		OperationUtilAction operUtil = null;
		Response response = null;
		
		try {
			seq = BasicEntityIdGenerator.getInstance().generateLongId();
			operUtil = ActionManager.getInstance().getOperationBean();
			logger.info("### ["+seq+"] cmdContainerRetrieve IN | parameter | cseBase:"+cseBase+" ###");
			
			ReqContainer req = new ReqContainer(cseBase, remoteCSEName, containerName, seq, request, operUtil);
			response = ActionManager.getInstance().getBean(RESOURCE_TYPE.CONTAINER).retrive(req);
		}catch(Exception e) {
			logger.error(e, e);
			response = operUtil.setResponse(request, Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), RSC.INTERNAL_SERVER_ERROR, null);
		}finally {
			getResponseToString(seq, "commonCSEBaseNodeBottomCreate", response);
			return response;
		}
	}
	
	@SuppressWarnings("finally")
	@GET
	@Path("/{cseBase}/remoteCSE-{remoteCSEName}/container-{containerName}/content-inst-{contentInstanceName}")
	@ResponseBody
	public Response cmdContentInstanceRetrieve(@Context HttpServletRequest request,
			@PathParam("cseBase") String cseBase, @PathParam("remoteCSEName") String remoteCSEName, 
			@PathParam("containerName") String containerName, @PathParam("contentInstanceName") String contentInstanceName) {
		
		String seq = "";
		OperationUtilAction operUtil = null;
		Response response = null;
		
		try {
			seq = BasicEntityIdGenerator.getInstance().generateLongId();
			operUtil = ActionManager.getInstance().getOperationBean();
			logger.info("### ["+seq+"] cmdContentInstanceRetrieve IN | parameter | cseBase:"+cseBase+"|containerName:"+containerName+"|contentInstanceName:"+contentInstanceName+" ###");
			
			ReqContentInstance req = new ReqContentInstance(cseBase, remoteCSEName, containerName, contentInstanceName, seq, request, operUtil);
			response = ActionManager.getInstance().getBean(RESOURCE_TYPE.CONTENT_INSTANCE).retrive(req);
		}catch(Exception e) {
			logger.error(e, e);
			response = operUtil.setResponse(request, Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), RSC.INTERNAL_SERVER_ERROR, null);
		}finally {
			getResponseToString(seq, "commonCSEBaseNodeBottomCreate", response);
			return response;
		}
	}
	
	@SuppressWarnings("finally")
	@GET
	@Path("/{cseBase}/remoteCSE-{remoteCSEName}/container-{containerName}/{labelTag}")
	@ResponseBody
	public Response cmdContentInstanceTagRetrieve(@Context HttpServletRequest request,
			@PathParam("cseBase") String cseBase, @PathParam("remoteCSEName") String remoteCSEName, 
			@PathParam("containerName") String containerName, @PathParam("labelTag") String labelTag) {
		
		String seq = "";
		OperationUtilAction operUtil = null;
		Response response = null;
		
		try {
			seq = BasicEntityIdGenerator.getInstance().generateLongId();
			operUtil = ActionManager.getInstance().getOperationBean();
			logger.info("### ["+seq+"] cmdContentInstanceRetrieve IN | parameter | cseBase:"+cseBase+"|containerName:"+containerName+"|labelTag:"+ labelTag+" ###");
			
			ReqContentInstance req = new ReqContentInstance(cseBase, remoteCSEName, containerName, null, labelTag, seq, request, operUtil);
			response = ActionManager.getInstance().getBean(RESOURCE_TYPE.CONTENT_INSTANCE).retrive(req);
		}catch(Exception e) {
			logger.error(e, e);
			response = operUtil.setResponse(request, Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), RSC.INTERNAL_SERVER_ERROR, null);
		} finally {
			getResponseToString(seq, "commonCSEBaseNodeBottomCreate", response);
			return response;
		}
	}
	
	
}