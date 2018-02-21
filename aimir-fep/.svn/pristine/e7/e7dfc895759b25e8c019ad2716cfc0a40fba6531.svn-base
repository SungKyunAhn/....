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
import com.aimir.fep.command.ws.data.HandlePowerOnOffRequest;
import com.aimir.fep.command.ws.data.HandlePowerOnOffResponse;

@WebService(name = "PowerOnOffCallback", serviceName = "PowerOnOffCallback", targetNamespace = "http://ws.command.fep.aimir.com/wsdl/PowerOnOffCallback")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "powerOnOffCallback")
public interface PowerOnOffCallback {
	@WebMethod
	@WebResult(name = "handlePowerOnOffResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	public HandlePowerOnOffResponse handlePowerOnOff(
			@WebParam(name = "handlePowerOnOffRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback") HandlePowerOnOffRequest payload)
			throws ApplicationFaultException;
}
