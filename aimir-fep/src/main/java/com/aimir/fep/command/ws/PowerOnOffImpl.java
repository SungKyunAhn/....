package com.aimir.fep.command.ws;

import java.math.BigInteger;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

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
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.PowerOnOffOrderDao;
import com.aimir.fep.command.ws.data.AddPowerOnOffOrderRequest;
import com.aimir.fep.command.ws.data.AddPowerOnOffOrderResponse;
import com.aimir.fep.command.ws.data.AddReadingOrderRequest;
import com.aimir.fep.command.ws.data.ApplicationFaultException;
import com.aimir.fep.command.ws.data.DeletePowerOnOffOrderRequest;
import com.aimir.fep.command.ws.data.DeletePowerOnOffOrderResponse;
import com.aimir.fep.command.ws.data.HandlePowerOnOffRequest;
import com.aimir.fep.command.ws.data.HandlePowerOnOffResponse;
import com.aimir.fep.command.ws.data.MeterValueNotFound;
import com.aimir.fep.command.ws.data.SearchPowerOnOffOrdersRequest;
import com.aimir.fep.command.ws.data.SearchPowerOnOffOrdersResponse;
import com.aimir.fep.command.ws.datatype.FaultCode;
import com.aimir.fep.command.ws.datatype.OrderStatus;
import com.aimir.fep.command.ws.datatype.PowerOperation;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.system.PowerOnOffOrder;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@WebService(name = "PowerOnOff", serviceName = "PowerOnOff", targetNamespace = "http://ws.command.fep.aimir.com/wsdl/PowerOnOff")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "powerOnOff")
public class PowerOnOffImpl implements PowerOnOff{
	private static Log log = LogFactory.getLog(PowerOnOffImpl.class);

	@Resource
	private WebServiceContext wsContext;

    @Autowired
    private PowerOnOffOrderDao poooDao;

    @Autowired
    private MeterDao meterDao;

	@WebMethod
	@WebResult(name = "addPowerOnOffOrderResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	public AddPowerOnOffOrderResponse addPowerOnOffOrder(
			@WebParam(name = "addPowerOnOffOrderRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff") AddPowerOnOffOrderRequest payload)
			throws ApplicationFaultException {

		checkParam(payload);

		AddPowerOnOffOrderResponse responseData = null;
		try {
			if (log.isDebugEnabled()) { log.debug(payload); }

			PowerOnOffOrder dataBean = (PowerOnOffOrder) convertPowerOnOffOrder(payload);

			Meter meter = meterDao.get(payload.getMeterSerialNumber());
			MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());

			PowerOnOffOrder temp1 = (PowerOnOffOrder) dataBean.clone();
			PowerOnOffOrder temp2 = (PowerOnOffOrder) dataBean.clone();
			PowerOnOffOrder temp3 = (PowerOnOffOrder) dataBean.clone();

			temp1.setMeterSerialNumber(null);
			temp1.setPowerOperationDate(null);
			temp1.setUserCreateDate(null);
			temp1.setUserReference(null);
			temp1.setPowerOperation(null);

			temp2.setReferenceId(null);
			temp2.setUserCreateDate(null);
			temp2.setUserReference(null);
			temp2.setPowerOperation(null);

			temp3.setReferenceId(null);
			temp3.setPowerOperationDate(null);
			temp3.setUserCreateDate(null);
			temp3.setUserReference(null);
			temp3.setPowerOperation(null);

			List list1 = poooDao.searchPowerOnOffOrder(temp1, null);
			List list2 = null;
			List list3 = null;

			if (list1 != null && list1.size() > 0) {
				throw new ApplicationFaultException(FaultCode.FC_108);
			}

			if (temp2.getPowerOperationDate() != null) {
				list2 = poooDao.searchPowerOnOffOrder(temp2, " orderStatus in (101,102,201,202) ");
			}
			if (list2 != null && list2.size() > 0) {
				throw new ApplicationFaultException(FaultCode.FC_110);
			}

			list3 = poooDao.searchPowerOnOffOrder(temp3, " orderStatus in (101,102,201,202) ");
			if (list3 != null && list3.size() > 0) {
				throw new ApplicationFaultException(FaultCode.FC_115);
			}

			dataBean.setOrderStatus(101);
			Object resObject = poooDao.add(dataBean);

			if (resObject == null || !resObject.equals(dataBean)) {
				throw new Exception("Operation failed.");
			}
		} catch (Exception e) {
			throw checkException(e);
		}
		return responseData;
	}

	@WebMethod
	@WebResult(name = "deletePowerOnOffOrderResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	public DeletePowerOnOffOrderResponse deletePowerOnOffOrder(
			@WebParam(name = "deletePowerOnOffOrderRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff") DeletePowerOnOffOrderRequest payload)
			throws ApplicationFaultException {

		checkParam(payload);
		DeletePowerOnOffOrderResponse responseData = null;
		HandlePowerOnOffRequest req = null;
		PowerOnOffOrder ro = null;

		try {
			if (log.isDebugEnabled()) { log.debug(payload); }

			PowerOnOffOrder dataBean = (PowerOnOffOrder) convertPowerOnOffOrder(payload);

			List list = poooDao.searchPowerOnOffOrder(dataBean, null);

			if (list != null && list.size() > 0) {
				ro = (PowerOnOffOrder) list.get(0);
				req = convertPowerOnOffOrderToHandlePowerOnOffRequestForDelete(ro);
			} else {
				throw new ApplicationFaultException(FaultCode.FC_100);
			}

			int result = poooDao.deletePowerOnOffOrder(dataBean);

			if (result == 1) {
				HandlePowerOnOffResponse res = null;
				try {
					res = sendingCallbackMessage(req);
					String[] fieldName = new String[] { "handleDate", "isSend" };
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyyMMddHHmmss");
					String handleDate = sdf.format(res.getHandledDate()
							.toGregorianCalendar().getTime());
					Object[] fieldValue = new Object[] { handleDate, 1 };
					poooDao.updatePowerOnOffOrder(ro, fieldName, fieldValue);
				} catch (Exception e) {
					log.error(e, e);
				}
				responseData = new DeletePowerOnOffOrderResponse();
			} else if (result == 3) {
				throw new ApplicationFaultException(FaultCode.FC_106);
			} else {
				throw new ApplicationFaultException(FaultCode.FC_107);
			}
		} catch (Exception e) {
			throw checkException(e);
		}
		return responseData;
	}

	@WebMethod
	@WebResult(name = "searchPowerOnOffOrdersResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff")
	public SearchPowerOnOffOrdersResponse searchPowerOnOffOrders(
			@WebParam(name = "searchPowerOnOffOrderRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/PowerOnOff") SearchPowerOnOffOrdersRequest payload)
			throws ApplicationFaultException {

		checkParam(payload);
		SearchPowerOnOffOrdersResponse responseData = null;
		try {
			if (log.isDebugEnabled()) { log.debug(payload); }

			PowerOnOffOrder dataBean = (PowerOnOffOrder) convertPowerOnOffOrder(payload);

			List result = poooDao.searchPowerOnOffOrder(dataBean, null);

			if (result != null) {
				responseData = new SearchPowerOnOffOrdersResponse();
				com.aimir.fep.command.ws.data.PowerOnOffOrder ro = null;
				for (int i = 0; i < result.size(); i++) {
					ro = (com.aimir.fep.command.ws.data.PowerOnOffOrder) convertPowerOnOffOrder(result.get(i));
					responseData.getPowerOnOffOrder().add(ro);
				}
			} else {
				throw new Exception("Data not existed.");
			}
		} catch (Exception e) {
			throw checkException(e);
		}
		return responseData;
	}

	protected Object convertPowerOnOffOrder(Object obj) {
		if (obj instanceof AddPowerOnOffOrderRequest) {
			AddPowerOnOffOrderRequest obj2 = (AddPowerOnOffOrderRequest) obj;
			PowerOnOffOrder dataBean = new PowerOnOffOrder();

			dataBean.setReferenceId(obj2.getReferenceId().longValue());
			dataBean.setMeterSerialNumber(obj2.getMeterSerialNumber());
			dataBean.setPowerOperation((int) obj2.getPowerOperation()
					.getValue());
			if (obj2.getPowerOperationDate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String powerOperationDate = sdf.format(obj2
						.getPowerOperationDate().toGregorianCalendar()
						.getTime());
				dataBean.setPowerOperationDate(powerOperationDate);
			}
			dataBean.setUserReference(obj2.getUserReference());
			if (obj2.getUserCreateDate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String userCreateDate = sdf.format(obj2.getUserCreateDate()
						.toGregorianCalendar().getTime());
				dataBean.setUserCreateDate(userCreateDate);
			}

			return dataBean;
		} else if (obj instanceof DeletePowerOnOffOrderRequest) {
			DeletePowerOnOffOrderRequest obj2 = (DeletePowerOnOffOrderRequest) obj;
			PowerOnOffOrder dataBean = new PowerOnOffOrder();

			dataBean.setReferenceId(obj2.getReferenceId().longValue());
			return dataBean;

		} else if (obj instanceof SearchPowerOnOffOrdersRequest) {
			SearchPowerOnOffOrdersRequest obj2 = (SearchPowerOnOffOrdersRequest) obj;
			PowerOnOffOrder dataBean = new PowerOnOffOrder();

			dataBean.setReferenceId(obj2.getReferenceId() == null ? null : obj2
					.getReferenceId().longValue());
			dataBean.setMeterSerialNumber(obj2.getMeterSerialNumber());
			String powerOperationDateFrom = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			if (obj2.getPowerOperationFromDate() != null) {
				powerOperationDateFrom = sdf.format(obj2
						.getPowerOperationFromDate().toGregorianCalendar()
						.getTime());
			}
			String powerOperationDateto = null;
			if (obj2.getPowerOperationToDate() != null) {
				powerOperationDateto = sdf.format(obj2
						.getPowerOperationToDate().toGregorianCalendar()
						.getTime());
			}
			dataBean.setPowerOperationDateFrom(powerOperationDateFrom);
			dataBean.setPowerOperationDateTo(powerOperationDateto);
			String userCreateDateFrom = null;
			if (obj2.getUserCreateFromDate() != null) {
				userCreateDateFrom = sdf.format(obj2.getUserCreateFromDate()
						.toGregorianCalendar().getTime());
			}
			String userCreateDateTo = null;
			if (obj2.getUserCreateToDate() != null) {
				userCreateDateTo = sdf.format(obj2.getUserCreateToDate()
						.toGregorianCalendar().getTime());
			}
			dataBean.setUserCreateDateFrom(userCreateDateFrom);
			dataBean.setUserCreateDateTo(userCreateDateTo);

			if (obj2.getOrderStatus() != null) {
				dataBean.setOrderStatus((int) obj2.getOrderStatus().getValue());
			}
			return dataBean;
		} else if (obj instanceof PowerOnOffOrder) {
			PowerOnOffOrder obj2 = (PowerOnOffOrder) obj;
			com.aimir.fep.command.ws.data.PowerOnOffOrder dataBean = new com.aimir.fep.command.ws.data.PowerOnOffOrder();
			dataBean.setReferenceId(new BigInteger(""
					+ obj2.getReferenceId().longValue()));
			dataBean.setMeterSerialNumber(obj2.getMeterSerialNumber());
			if (obj2.getPowerOperationDate() != null) {
				GregorianCalendar gcal = new GregorianCalendar();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				try {
					Date d = sdf.parse(obj2.getPowerOperationDate());
					gcal.setTime(d);
					dataBean.setPowerOperationDate(DatatypeFactory
							.newInstance().newXMLGregorianCalendar(gcal));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dataBean.setPowerOperation(PowerOperation.getPowerOperation(obj2
					.getPowerOperation()));
			dataBean.setUserReference(obj2.getUserReference());
			if (obj2.getUserCreateDate() != null) {
				GregorianCalendar gcal = new GregorianCalendar();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				try {
					Date d = sdf.parse(obj2.getUserCreateDate());
					gcal.setTime(d);
					dataBean.setUserCreateDate(DatatypeFactory.newInstance()
							.newXMLGregorianCalendar(gcal));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (obj2.getOrderStatus() != null) {
				dataBean.setOrderStatus(OrderStatus.getOrderStatus(obj2
						.getOrderStatus().intValue()));
			}

			return dataBean;
		}
		return null;
	}

	private HandlePowerOnOffRequest convertPowerOnOffOrderToHandlePowerOnOffRequestForDelete(
			PowerOnOffOrder data) {
		HandlePowerOnOffRequest req = new HandlePowerOnOffRequest();
		req.setReferenceId(new BigInteger(Long.toString(data.getReferenceId())));
		req.setMeterSerialNumber(data.getMeterSerialNumber());

		GregorianCalendar gcal = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date d = sdf.parse(data.getPowerOperationDate());
			gcal.setTime(d);
			req.setPowerOperationDate(DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gcal));
		} catch (Exception e) {
			e.printStackTrace();
		}
		req.setPowerOperation(PowerOperation.getPowerOperation(data
				.getPowerOperation()));
		req.setUserReference(data.getUserReference());
		try {
			Date d = sdf.parse(data.getUserCreateDate());
			gcal.setTime(d);
			req.setUserCreateDate(DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gcal));
		} catch (Exception e) {
			e.printStackTrace();
		}
		req.setOrderStatus(OrderStatus.CANCELED);

		return req;
	}

	protected ApplicationFaultException checkException(Exception e) {
		if(log.isErrorEnabled()) {log.error(e, e);}

		if (e instanceof MeterValueNotFound) {
			return new ApplicationFaultException(FaultCode.FC_105);
		} else if (e instanceof ApplicationFaultException) {
		    return (ApplicationFaultException)e;
		} else if (e.getMessage() != null) {
			if (e.getMessage().toUpperCase().indexOf("SQL") > -1
					|| e.getMessage().toUpperCase().indexOf("DATABASE") > -1) {
				return new ApplicationFaultException(FaultCode.FC_101,
						e.getMessage());
			} else if (e.getMessage().toUpperCase().indexOf("TRANSACTION") > -1) {
				return new ApplicationFaultException(FaultCode.FC_102,
						e.getMessage());
			} else {
				return new ApplicationFaultException(FaultCode.FC_103,
						StringUtil.makeStackTraceToString(e));
			}
		}
		return new ApplicationFaultException(FaultCode.FC_103,
				StringUtil.makeStackTraceToString(e));
	}

	protected void checkParam(Object param) throws ApplicationFaultException {
		StringBuffer details = new StringBuffer();
		if (param == null) {
			if (details.length() > 0) {
				details.append(", ");
			}
			details.append("Parameter is null");
		} else if (param instanceof AddPowerOnOffOrderRequest) {
			/*
			 * Add Power On/Off Order check logic 
			 */

			/*
			 * Reference Id
			 * Not Null
			 * Maximum 9999999999
			 */
			if (((AddPowerOnOffOrderRequest) param).getReferenceId() == null
					|| ((AddPowerOnOffOrderRequest) param).getReferenceId()
							.equals("")) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("ReferenceId is null");
			} else {
				if (((AddPowerOnOffOrderRequest) param).getReferenceId()
						.longValue() > 9999999999l) {
					if (details.length() > 0) {
						details.append(", ");
					}
					details.append("ReferenceId exceeded 9999999999.");
				}
			}

			/*
			 * User Reference
			 * Not Null
			 * max length 50 byte
			 */
			if (((AddPowerOnOffOrderRequest) param).getUserReference() == null
					|| ((AddPowerOnOffOrderRequest) param).getUserReference()
							.equals("")) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("UserReference is null");
			} else {
				if (((AddPowerOnOffOrderRequest) param).getUserReference()
						.length() > 50) {
					if (details.length() > 0) {
						details.append(", ");
					}
					details.append("UserReference exceeded length(50).");
				}
			}

			/*
			 * Power Operation
			 * Not Null
			 */
			if (((AddPowerOnOffOrderRequest) param).getPowerOperation() == null) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("PowerOperation is null");
			}

			/*
			 * Power Operation date
			 * Future date
			 */
			if (((AddPowerOnOffOrderRequest) param).getPowerOperationDate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String powerOperationDate = sdf
						.format(((AddPowerOnOffOrderRequest) param)
								.getPowerOperationDate().toGregorianCalendar()
								.getTime());
				try {
					if (Long.parseLong(powerOperationDate) < Long
							.parseLong(TimeUtil.getCurrentTime())) {
						if (details.length() == 0) {
							throw new ApplicationFaultException(
									FaultCode.FC_114);
						}
					}
				} catch (ApplicationFaultException e) {
					throw e;
				} catch (Exception e) {
					details.append(e.getMessage());
				}
			}

			/*
			 * User Create Date
			 * Not Null
			 */
			if (((AddPowerOnOffOrderRequest) param).getUserCreateDate() == null) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("UserCreateDate is null");
			}

			/*
			 * Meter Serial Number
			 * Not Null
			 */
			if (((AddPowerOnOffOrderRequest) param).getMeterSerialNumber() == null
					|| ((AddPowerOnOffOrderRequest) param).getMeterSerialNumber()
							.equals("")) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("MeterSerialNumber is null");
			} else {
				Meter meter = meterDao.get(((AddReadingOrderRequest) param).getMeterSerialNumber());
				if(meter == null) {
					log.error("Meter["
							+ ((AddReadingOrderRequest) param)
									.getMeterSerialNumber() + "] have not exist.");
					if (details.length() == 0) {
						throw new ApplicationFaultException(
								FaultCode.FC_130);
					}
				}

				if (MeterType.valueOf(meter.getMeterType().getName()) != MeterType.EnergyMeter) {
					throw new ApplicationFaultException(FaultCode.FC_122);
				}
			}
		} else if (param instanceof DeletePowerOnOffOrderRequest) {
			/*
			 * Delete Power On/Off Order check logic 
			 */

			/*
			 * Refrence Id
			 * Not Null
			 * max 9999999999
			 */
			if (((DeletePowerOnOffOrderRequest) param).getReferenceId() == null
					|| ((DeletePowerOnOffOrderRequest) param).getReferenceId()
							.equals("")) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("ReferenceId is null");
			} else {
				if (((DeletePowerOnOffOrderRequest) param).getReferenceId()
						.longValue() > 9999999999l) {
					if (details.length() > 0) {
						details.append(", ");
					}
					details.append("ReferenceId exceeded 9999999999.");
				}
			}
		} else if (param instanceof SearchPowerOnOffOrdersRequest) {
			/*
			 * Search Power On/Off Order check logic 
			 */

			/*
			 * UserCreateFromDate
			 * Not null
			 */
			if (((SearchPowerOnOffOrdersRequest) param).getUserCreateFromDate() == null) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("UserCreateFromDate is null");
			}

			/*
			 * Reference Id
			 * maximum 9999999999
			 */
			if (((SearchPowerOnOffOrdersRequest) param).getReferenceId() != null
					&& !((SearchPowerOnOffOrdersRequest) param)
							.getReferenceId().equals("")) {
				if (((SearchPowerOnOffOrdersRequest) param).getReferenceId()
						.longValue() > 9999999999l) {
					if (details.length() > 0) {
						details.append(", ");
					}
					details.append("ReferenceId exceeded 9999999999.");
				}
			}

			/*
			 * Meter Serial Number
			 * Not Null
			 */
			if (((SearchPowerOnOffOrdersRequest) param).getMeterSerialNumber() == null
					|| ((SearchPowerOnOffOrdersRequest) param).getMeterSerialNumber()
							.equals("")) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("MeterSerialNumber is null");
			} else {
				Meter meter = meterDao.get(((SearchPowerOnOffOrdersRequest) param).getMeterSerialNumber());
				if(meter == null) {
					log.error("Meter["
							+ ((SearchPowerOnOffOrdersRequest) param)
									.getMeterSerialNumber() + "] have not exist.");
					if (details.length() == 0) {
						throw new ApplicationFaultException(
								FaultCode.FC_130);
					}
				}
			}
		}

		if (details.length() > 0) {
			throw new ApplicationFaultException(FaultCode.FC_100,
					details.toString());
		}
	}

	private HandlePowerOnOffResponse sendingCallbackMessage(
			HandlePowerOnOffRequest req) throws Exception {
		boolean isSend = Boolean.parseBoolean(FMPProperty.getProperty(
				"interface.PowerOnOffCallback.isSend", "false"));
		if (!isSend) {
			throw new Exception("Currently AIMIR can't send handling data");
		}
		HandlePowerOnOffResponse res = sendingCallbackMessageJBossWSCXF(req);
		return res;
	}

	private HandlePowerOnOffResponse sendingCallbackMessageJBossWSCXF(
			HandlePowerOnOffRequest req)
			throws MalformedURLException, ApplicationFaultException {

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(PowerOnOffCallback.class);
		factory.setAddress(FMPProperty.getProperty(
				"interface.PowerOnOffCallback.endpoint",
				"https://localhost:8080/services/OnDemandCallback"));
		PowerOnOffCallback port = (PowerOnOffCallback) factory.create();

		Client client = ClientProxy.getClient(port);
		HTTPConduit httpConduit = (HTTPConduit) client.getConduit();

		/** HTTPS, SSL 이용시 아래부분을 사용한다. */
		TLSClientParameters tlsParams = new TLSClientParameters();
		tlsParams.setDisableCNCheck(true); // CN Name check ignore...
		httpConduit.setTlsClientParameters(tlsParams);

		/** Disable AllowChunking */
		HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
		httpClientPolicy.setAllowChunking(false);
		httpConduit.setClient(httpClientPolicy);

		HandlePowerOnOffResponse res = port.handlePowerOnOff(req);
		return res;
	}

}