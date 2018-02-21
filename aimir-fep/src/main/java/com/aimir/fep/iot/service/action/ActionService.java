package com.aimir.fep.iot.service.action;

import javax.ws.rs.core.Response;

import com.aimir.fep.iot.request.Request;

public interface ActionService {
	public Response create(Request req) throws Exception;
	public Response retrive(Request req) throws Exception;
	public Response update(Request req) throws Exception;
	public Response delete(Request req) throws Exception;
}
