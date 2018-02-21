package com.aimir.fep.command.ws;

import java.util.GregorianCalendar;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.ParameterStyle;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.dao.system.OnDemandReadingOrderDao;
import com.aimir.fep.command.ws.data.ApplicationFaultException;
import com.aimir.fep.command.ws.data.HandleReadingRequest;
import com.aimir.fep.command.ws.data.HandleReadingResponse;
import com.aimir.fep.schedule.task.OnDemandReadingOrderRunnable;
import com.aimir.model.system.OnDemandReadingOrder;

@WebService(name = "OnDemandReadingCallback", serviceName = "OnDemandReadingCallback", targetNamespace = "http://ws.command.fep.aimir.com/wsdl/OnDemandReadingCallback")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "onDemandReadingCallback")
public class OnDemandReadingCallbackImpl implements OnDemandReadingCallback {
    private static Log log = LogFactory
            .getLog(OnDemandReadingCallbackImpl.class);

    @Autowired
    private OnDemandReadingOrderDao odroDao;

	@WebMethod
	@WebResult(name = "handleReadingResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback")
	public HandleReadingResponse handleReading(
			@WebParam(name = "handleReadingRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReadingCallback") HandleReadingRequest payload)
			throws ApplicationFaultException {

	    log.debug(payload);
		HandleReadingResponse res = new HandleReadingResponse();
		try {
			OnDemandReadingOrder dataBean = (OnDemandReadingOrder) convertReadingOrder(payload);

			odroDao.updateOnDemandReadingOrder(dataBean,
					new String[] { "testResult" }, new Object[] { 1 });

			res.setHandledDate(DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(new GregorianCalendar()));
		} catch (Exception e) {
			throw new ApplicationFaultException("103", "Internal Server Error",
					e.getMessage());
		}
		return res;
	}

	protected Object convertReadingOrder(Object obj) {
		if (obj instanceof HandleReadingRequest) {
			HandleReadingRequest obj2 = (HandleReadingRequest) obj;
			OnDemandReadingOrder dataBean = new OnDemandReadingOrder();

			dataBean.setReferenceId(obj2.getReferenceId().longValue());
			dataBean.setMeterSerialNumber(obj2.getMeterSerialNumber());
			return dataBean;
		}
		return null;
	}
}
