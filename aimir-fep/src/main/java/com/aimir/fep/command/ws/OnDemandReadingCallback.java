package com.aimir.fep.command.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import org.springframework.stereotype.Service;

import com.aimir.fep.command.ws.data.ApplicationFaultException;
import com.aimir.fep.command.ws.data.HandleReadingRequest;
import com.aimir.fep.command.ws.data.HandleReadingResponse;

@WebService(name = "OnDemandReadingCallback", serviceName = "OnDemandReadingCallback", targetNamespace = "http://ws.command.fep.aimir.com/wsdl/OnDemandReadingCallback")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "onDemandReadingCallback")
public interface OnDemandReadingCallback {

	@WebMethod
	@WebResult(name = "handleReadingResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback")
	public HandleReadingResponse handleReading(
			@WebParam(name = "handleReadingRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback") HandleReadingRequest payload)
			throws ApplicationFaultException;
}
