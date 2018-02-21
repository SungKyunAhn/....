package com.aimir.fep.command.mbean;

import java.math.BigInteger;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
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

import com.aimir.fep.iot.domain.resources.MgmtCmd;
import com.aimir.fep.iot.request.ReqContainer;
import com.aimir.fep.iot.request.ReqContentInstance;
import com.aimir.fep.iot.request.ReqDeviceInfo;
import com.aimir.fep.iot.request.ReqFirmware;
import com.aimir.fep.iot.request.ReqMgmtCmd;
import com.aimir.fep.iot.request.ReqRemoteCse;
import com.aimir.fep.iot.request.Request;
import com.aimir.fep.iot.service.action.ActionManager;
import com.aimir.fep.iot.service.action.OperationUtilAction;
import com.aimir.fep.iot.service.snowflake.BasicEntityIdGenerator;
import com.aimir.fep.iot.utils.CommonCode.MGMT_DEFINITION;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;
import com.aimir.fep.iot.utils.CommonCode.RSC;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.fep.iot.utils.MessageProperties;

@Produces({"application/xml", "application/json"})
@Component
public class CommandService {
	private static Log logger = LogFactory.getLog(CommandService.class);
	
	/*
	@GET
	@Path("/auth/AE-{serialNo}/{status}")
	@ResponseBody
	public Response cmdAuthSPModem(@Context HttpServletRequest request, 
			@PathParam("serialNo") String serialNo, @PathParam("status") int status) {
		
		System.out.println("serialNo:"+serialNo+"|status:"+status);
		
//		ResponseBuilder rBuild = Response.status(Response.Status.BAD_REQUEST);
//        return rBuild.type(MediaType.APPLICATION_JSON)
//        			 .status(Response.Status.BAD_REQUEST)
//                     .entity("error message")
//                     .build();
		
		return Response.status(Response.Status.BAD_REQUEST).entity("error message").header("X-M2M-RSC", "4000").build();
		 
//		return Response.ok("aaa").build(); 
//		return Response.serverError().header("X-M2M-RSC", "4000").build();
//		return Response.ok(json, MediaType.APPLICATION_JSON).build();
	}
	*/
		
	@SuppressWarnings("finally")
	@POST
	@Path("/{cseBase}")	
	@ResponseBody
	public Response commonCSEBaseBottomCreate(@Context HttpServletRequest request,
			@PathParam("cseBase") String cseBase) {
		String seq = "";
		String message = "";
		OperationUtilAction operUtil = null;
		Response response = null;
		
		try {
			RESOURCE_TYPE ty = getTy(request);
			seq = BasicEntityIdGenerator.getInstance().generateLongId();
			operUtil = ActionManager.getInstance().getOperationBean();
			logger.info("### ["+seq+"] commonCSEBaseBottomCreate IN | parameter | cseBase:"+cseBase+"|ty:"+ty.name()+" ###");
			
			if(CommonUtil.isEmpty(ty)) {
				message = MessageProperties.getProperty("aimir.iot.ty.empty");
				response = operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
			}
			
			switch(ty) {
			case AE:
				response = Response.status(Response.Status.BAD_REQUEST).entity("ae crate request").header("X-M2M-RSC", "4000").build();
				break;
			case GROUP:
				response = Response.status(Response.Status.BAD_REQUEST).entity("group create request").header("X-M2M-RSC", "4000").build();
				break;
			case LOCATION_POLICY:
				response = Response.status(Response.Status.BAD_REQUEST).entity("locationPolicy create request").header("X-M2M-RSC", "4000").build();
				break;
			case REMOTE_CSE:
				ReqRemoteCse req = new ReqRemoteCse(cseBase, seq, request, ty, operUtil);
				response = ActionManager.getInstance().getBean(ty).create(req);
				break;
			default:
				message = MessageProperties.getProperty("aimir.iot.service.fail");
				response = Response.status(Response.Status.BAD_REQUEST).entity(message).header("X-M2M-RSC", "4000").build();
				break;
			}
			
		}catch(Exception e) {
			logger.error(e, e);
			response = operUtil.setResponse(request, Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), RSC.INTERNAL_SERVER_ERROR, null);
		}finally {
			getResponseToString(seq, "commonCSEBaseNodeBottomCreate", response);
			return response;
		}
	}
	
	@SuppressWarnings("finally")
	@POST
	@Path("/{cseBase}/remoteCSE-{remoteCSEName}")	
	@ResponseBody
	public Response commonCSEBaseRemoteCSEBottomCreate(@Context HttpServletRequest request,			
			@PathParam("cseBase") String cseBase, @PathParam("remoteCSEName") String remoteCSEName) {
		String seq = "";
		String message = "";
		OperationUtilAction operUtil = null;
		Response response = null;
		Request req = null;
		
		try {
			RESOURCE_TYPE ty = getTy(request);
			seq = BasicEntityIdGenerator.getInstance().generateLongId();
			operUtil = ActionManager.getInstance().getOperationBean();
			logger.info("### ["+seq+"] commonCSEBaseRemoteCSEBottomCreate IN | parameter |cseBase:"+cseBase+"|remoteCSEName:"+remoteCSEName+"|ty:"+ty.name()+" ###");
			
			if(CommonUtil.isEmpty(ty)) {
				message = MessageProperties.getProperty("aimir.iot.ty.empty");
				response = operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
			}
			
			switch(ty) {
			case AE:
				response = Response.status(Response.Status.BAD_REQUEST).entity("remotecse - ae create request").header("X-M2M-RSC", "4000").build();
				break;
			case CONTAINER:
				req = new ReqContainer(cseBase, remoteCSEName, seq, request, ty, operUtil);
				response = ActionManager.getInstance().getBean(ty).create(req);
				break;
			case GROUP:
				response = Response.status(Response.Status.BAD_REQUEST).entity("remotecse - group create request").header("X-M2M-RSC", "4000").build();
				break;
			case MGMT_CMD:
				req = new ReqMgmtCmd(cseBase, remoteCSEName, seq, request, ty, operUtil);
				response = ActionManager.getInstance().getBean(ty).create(req);
				break;
			default:
				message = MessageProperties.getProperty("aimir.iot.service.fail");
				response = Response.status(Response.Status.BAD_REQUEST).entity(message).header("X-M2M-RSC", "4000").build();
				break;
			}
			
		}catch(Exception e) {
			logger.error(e, e);
			response = operUtil.setResponse(request, Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), RSC.INTERNAL_SERVER_ERROR, null);
		}finally {
			getResponseToString(seq, "commonCSEBaseNodeBottomCreate", response);
			return response;
		}
	}
	
	@SuppressWarnings("finally")
	@POST
	@Path("/{cseBase}/remoteCSE-{remoteCSEName}/container-{containerName}")
	@ResponseBody
	public Response commonCSEBaseRemoteCSEContainerBottomCreate(@Context HttpServletRequest request,			
			@PathParam("cseBase") String cseBase, @PathParam("remoteCSEName") String remoteCSEName, @PathParam("containerName") String containerName) {
		String seq = "";
		String message = "";
		OperationUtilAction operUtil = null;
		Response response = null;
		
		try {
			RESOURCE_TYPE ty = getTy(request);
			seq = BasicEntityIdGenerator.getInstance().generateLongId();
			operUtil = ActionManager.getInstance().getOperationBean();
			logger.info("### ["+seq+"] commonCSEBaseRemoteCSEContainerBottomCreate IN | parameter "
					+ "|cseBase:"+cseBase+"|remoteCSEName:"+remoteCSEName+"|containerName:"+containerName+"|ty:"+ty.name()+" ###");
			
			if(CommonUtil.isEmpty(ty)) {
				message = MessageProperties.getProperty("aimir.iot.ty.empty");
				response = operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
			}

			switch(ty) {
			case CONTENT_INSTANCE:
				ReqContentInstance req = new ReqContentInstance(cseBase, remoteCSEName, containerName, seq, request, ty, operUtil);
				response = ActionManager.getInstance().getBean(ty).create(req);
				break;
			case SUBSCRIPTION:
				response = Response.status(Response.Status.BAD_REQUEST).entity("container - subscription create request").header("X-M2M-RSC", "4000").build();
				break;
			default:
				message = MessageProperties.getProperty("aimir.iot.service.fail");
				response = Response.status(Response.Status.BAD_REQUEST).entity(message).header("X-M2M-RSC", "4000").build();
				break;
			}
			
		}catch(Exception e) {
			logger.error(e, e);
			response = operUtil.setResponse(request, Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), RSC.INTERNAL_SERVER_ERROR, null);
		}finally {
			getResponseToString(seq, "commonCSEBaseNodeBottomCreate", response);
			return response;
		}
	}
	
	@SuppressWarnings("finally")
	@POST
	@Path("/{cseBase}/node-{nodeName}")
	@ResponseBody
	public Response commonCSEBaseNodeBottomCreate(@Context HttpServletRequest request,			
			@PathParam("cseBase") String cseBase, @PathParam("nodeName") String nodeName) {
		
		String seq = "";
		String message = "";
		OperationUtilAction operUtil = null;
		Response response = null;
		Request req = null;
		
		try {
			RESOURCE_TYPE ty = getTy(request);
			MGMT_DEFINITION mgd = getMgd(request);
			seq = BasicEntityIdGenerator.getInstance().generateLongId();
			operUtil = ActionManager.getInstance().getOperationBean();
			logger.info("### ["+seq+"] commonCSEBaseNodeBottomCreate IN | parameter |cseBase:"+cseBase+"|nodeName:"+nodeName+"|mgd:"+mgd.name()+"|ty:"+ty.name()+" ###");
			
			if(CommonUtil.isEmpty(ty)) {
				message = MessageProperties.getProperty("aimir.iot.ty.empty");
				response = operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
			}else if(ty != RESOURCE_TYPE.MGMT_OBJ) {
				message = MessageProperties.getProperty("aimir.iot.invalid.value");
				response = operUtil.setResponse(request, Response.Status.BAD_REQUEST, message, RSC.BAD_REQUEST, null);
			}

			switch(mgd) {
			case FIRMWARE:
				req = new ReqFirmware(mgd, cseBase, nodeName, seq, request, ty, operUtil);
				response = ActionManager.getInstance().getBean(ty, mgd).create(req);
				break;
			case SOFTWARE:
				response = Response.status(Response.Status.BAD_REQUEST).entity("container - SOFTWARE create request").header("X-M2M-RSC", "4000").build();
				break;
			case DEVICE_INFO:
				req = new ReqDeviceInfo(mgd, cseBase, nodeName, seq, request, ty, operUtil);
				response = ActionManager.getInstance().getBean(ty, mgd).create(req);
				break;
			case BATTERY:
				response = Response.status(Response.Status.BAD_REQUEST).entity("container - BATTERY create request").header("X-M2M-RSC", "4000").build();
				break;
			case MEMORY:
				response = Response.status(Response.Status.BAD_REQUEST).entity("container - MEMORY create request").header("X-M2M-RSC", "4000").build();
				break;
			case REBOOT:
				response = Response.status(Response.Status.BAD_REQUEST).entity("container - REBOOT create request").header("X-M2M-RSC", "4000").build();
				break;
			case EVENT_LOG:
				response = Response.status(Response.Status.BAD_REQUEST).entity("container - EVENT_LOG create request").header("X-M2M-RSC", "4000").build();
				break;
			default:
				message = MessageProperties.getProperty("aimir.iot.service.fail");
				response = Response.status(Response.Status.BAD_REQUEST).entity(message).header("X-M2M-RSC", "4000").build();
				break;
			}
			
		}catch(Exception e) {
			logger.error(e, e);
			response = operUtil.setResponse(request, Response.Status.INTERNAL_SERVER_ERROR, e.getMessage(), RSC.INTERNAL_SERVER_ERROR, null);
		}finally {
			getResponseToString(seq, "commonCSEBaseNodeBottomCreate", response);
			return response;
		}
	}
	
	protected void getResponseToString(String seq, String method, Response response) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("### [").append(seq).append("] ").append(method).append(" OUT |");
		if(response == null) {
			builder.append("return Response null! ");
		}else {
			int statusCode = response.getStatus();
			Status status = Status.fromStatusCode(statusCode);
			String msg = response.getEntity().toString();
			builder.append("response status:").append(status.name()).append("(").append(statusCode).append(")");
			
			if(!(msg.contains("xml") || msg.contains("{")))
				builder.append("|response message:").append(msg);
		}
		
		builder.append(" ###");
		
		logger.info(builder.toString());		
	}
	
	private RESOURCE_TYPE getTy(HttpServletRequest request) {
		String contentType 	= CommonUtil.nvl(request.getHeader("Content-Type"), "");
		RESOURCE_TYPE ty 	= null;
		String[] contentTypeValues = contentType.split(";");
		for (int i=0; i<contentTypeValues.length; i++) {
			String contentTypeValue = contentTypeValues[i].trim();
			
			if (contentTypeValue.contains("ty=")) {
				String value = contentTypeValue.split("=")[1];
				if (!CommonUtil.isEmpty(value)) {
					ty = RESOURCE_TYPE.getThis(new BigInteger(value));
				}
			}
		}
		
		return ty;
	}
	
	public MGMT_DEFINITION getMgd(HttpServletRequest request) {
		String contentType 	= CommonUtil.nvl(request.getHeader("Content-Type"), "");
		MGMT_DEFINITION mgd	= null;
		String[] contentTypeValues = contentType.split(";");
		for (int i=0; i<contentTypeValues.length; i++) {
			String contentTypeValue = contentTypeValues[i].trim();
			
			if (contentTypeValue.contains("mgd=")) {
				String value = contentTypeValue.split("=")[1];
				if (!CommonUtil.isEmpty(value)) {
					mgd = MGMT_DEFINITION.getThis(new BigInteger(value));
				}
			}
		}
		
		return mgd;
	}
}	