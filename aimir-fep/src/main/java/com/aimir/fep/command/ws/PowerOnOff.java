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

import com.aimir.fep.command.ws.data.AddPowerOnOffOrderRequest;
import com.aimir.fep.command.ws.data.AddPowerOnOffOrderResponse;
import com.aimir.fep.command.ws.data.ApplicationFaultException;
import com.aimir.fep.command.ws.data.DeletePowerOnOffOrderRequest;
import com.aimir.fep.command.ws.data.DeletePowerOnOffOrderResponse;
import com.aimir.fep.command.ws.data.SearchPowerOnOffOrdersRequest;
import com.aimir.fep.command.ws.data.SearchPowerOnOffOrdersResponse;

@WebService(name = "PowerOnOff", serviceName = "PowerOnOff", targetNamespace = "http://ws.command.fep.aimir.com/wsdl/PowerOnOff")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "powerOnOff")
public interface PowerOnOff {

	@WebMethod
	@WebResult(name = "addPowerOnOffOrderResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	public AddPowerOnOffOrderResponse addPowerOnOffOrder(
			@WebParam(name = "addPowerOnOffOrderRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff") AddPowerOnOffOrderRequest payload)
			throws ApplicationFaultException;

	@WebMethod
	@WebResult(name = "deletePowerOnOffOrderResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	public DeletePowerOnOffOrderResponse deletePowerOnOffOrder(
			@WebParam(name = "deletePowerOnOffOrderRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff") DeletePowerOnOffOrderRequest payload)
			throws ApplicationFaultException;

	@WebMethod
	@WebResult(name = "searchPowerOnOffOrdersResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	public SearchPowerOnOffOrdersResponse searchPowerOnOffOrders(
			@WebParam(name = "searchPowerOnOffOrderRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff") SearchPowerOnOffOrdersRequest payload)
			throws ApplicationFaultException;
}
