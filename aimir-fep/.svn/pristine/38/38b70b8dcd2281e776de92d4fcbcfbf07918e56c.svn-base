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
import org.springframework.transaction.annotation.Transactional;

import com.aimir.fep.command.ws.data.AddReadingOrderRequest;
import com.aimir.fep.command.ws.data.AddReadingOrderResponse;
import com.aimir.fep.command.ws.data.ApplicationFaultException;
import com.aimir.fep.command.ws.data.DeleteReadingOrderRequest;
import com.aimir.fep.command.ws.data.DeleteReadingOrderResponse;
import com.aimir.fep.command.ws.data.GetHistoricalReadingRequest;
import com.aimir.fep.command.ws.data.GetHistoricalReadingResponse;
import com.aimir.fep.command.ws.data.SearchReadingOrdersRequest;
import com.aimir.fep.command.ws.data.SearchReadingOrdersResponse;

@WebService(name = "OnDemandReading", serviceName = "OnDemandReading", targetNamespace = "http://ws.command.fep.aimir.com/wsdl/OnDemandReading")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "onDemandReading")
@Transactional(value = "transactionManager")
public interface OnDemandReading {

	@WebMethod
	@WebResult(name = "getHistoricalReadingResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	public GetHistoricalReadingResponse getHistoricalReading(
			@WebParam(name = "getHistoricalReadingRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading") GetHistoricalReadingRequest payload)
			throws ApplicationFaultException;

	@WebMethod
	@WebResult(name = "addReadingOrderResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	public AddReadingOrderResponse addReadngOrder(
			@WebParam(name = "addReadingOrderRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading") AddReadingOrderRequest payload)
			throws ApplicationFaultException;

	@WebMethod(operationName = "deleteReadingOrder")
	@WebResult(name = "deleteReadingOrderResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	public DeleteReadingOrderResponse deleteReadngOrder(
			@WebParam(name = "deleteReadingOrderRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading") DeleteReadingOrderRequest payload)
			throws ApplicationFaultException;

	@WebMethod
	@WebResult(name = "searchReadingOrdersResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	public SearchReadingOrdersResponse searchReadingOrders(
			@WebParam(name = "searchReadingOrdersRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading") SearchReadingOrdersRequest payload)
			throws ApplicationFaultException;
}
