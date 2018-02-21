package com.aimir.fep.command.ws;

import java.util.GregorianCalendar;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.ws.WebServiceContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.system.PowerOnOffOrderDao;
import com.aimir.fep.command.ws.data.ApplicationFaultException;
import com.aimir.fep.command.ws.data.HandlePowerOnOffRequest;
import com.aimir.fep.command.ws.data.HandlePowerOnOffResponse;
import com.aimir.model.system.PowerOnOffOrder;

@WebService(name = "PowerOnOffCallback", serviceName = "PowerOnOffCallback", targetNamespace = "http://ws.command.fep.aimir.com/wsdl/PowerOnOffCallback")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "powerOnOffCallback")
public class PowerOnOffCallbackImpl implements PowerOnOffCallback {
    private static Log log = LogFactory
            .getLog(PowerOnOffCallbackImpl.class);

    @Autowired
    private PowerOnOffOrderDao poooDao;

	@WebMethod
	@WebResult(name = "handlePowerOnOffResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback")
	public HandlePowerOnOffResponse handlePowerOnOff(
			@WebParam(name = "handlePowerOnOffRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOffCallback") HandlePowerOnOffRequest payload)
			throws ApplicationFaultException {

	    log.debug(payload);
		HandlePowerOnOffResponse res = new HandlePowerOnOffResponse();
		try {
			PowerOnOffOrder dataBean = (PowerOnOffOrder) convertReadingOrder(payload);

			poooDao.updatePowerOnOffOrder(dataBean,
					new String[] { "testResult" }, new String[] { "1" });

			res.setHandledDate(DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(new GregorianCalendar()));
		} catch (Exception e) {
			throw new ApplicationFaultException("103", "Internal Server Error",
					e.getMessage());
		}
		return res;
	}

	protected Object convertReadingOrder(Object obj) {
		if (obj instanceof HandlePowerOnOffRequest) {
			HandlePowerOnOffRequest obj2 = (HandlePowerOnOffRequest) obj;
			PowerOnOffOrder dataBean = new PowerOnOffOrder();

			dataBean.setReferenceId(obj2.getReferenceId().longValue());
			dataBean.setMeterSerialNumber(obj2.getMeterSerialNumber());
			dataBean.setPowerOperation((int) obj2.getPowerOperation()
					.getValue());
			return dataBean;
		}
		return null;
	}

}
