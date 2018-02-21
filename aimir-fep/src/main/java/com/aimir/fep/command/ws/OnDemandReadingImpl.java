package com.aimir.fep.command.ws;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

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
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.OnDemandReadingOrderDao;
import com.aimir.fep.command.ws.data.AddReadingOrderRequest;
import com.aimir.fep.command.ws.data.AddReadingOrderResponse;
import com.aimir.fep.command.ws.data.ApplicationFaultException;
import com.aimir.fep.command.ws.data.DeleteReadingOrderRequest;
import com.aimir.fep.command.ws.data.DeleteReadingOrderResponse;
import com.aimir.fep.command.ws.data.GetHistoricalReadingRequest;
import com.aimir.fep.command.ws.data.GetHistoricalReadingResponse;
import com.aimir.fep.command.ws.data.HandleReadingRequest;
import com.aimir.fep.command.ws.data.HandleReadingResponse;
import com.aimir.fep.command.ws.data.MeterValue;
import com.aimir.fep.command.ws.data.MeterValueNotFound;
import com.aimir.fep.command.ws.data.ReadingOrder;
import com.aimir.fep.command.ws.data.SearchReadingOrdersRequest;
import com.aimir.fep.command.ws.data.SearchReadingOrdersResponse;
import com.aimir.fep.command.ws.datatype.FaultCode;
import com.aimir.fep.command.ws.datatype.MeasurementUnit;
import com.aimir.fep.command.ws.datatype.MeterValueMeasurementType;
import com.aimir.fep.command.ws.datatype.OrderStatus;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.system.OnDemandReadingOrder;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

@WebService(name = "OnDemandReading", serviceName = "OnDemandReading", targetNamespace = "http://ws.command.fep.aimir.com/wsdl/OnDemandReading")
@SOAPBinding(style = Style.DOCUMENT, use = Use.LITERAL, parameterStyle = ParameterStyle.WRAPPED)
@Service(value = "onDemandReading")
@Transactional(value = "transactionManager")
public class OnDemandReadingImpl implements OnDemandReading {
	private static Log log = LogFactory.getLog(OnDemandReadingImpl.class);

	@Resource
	private WebServiceContext wsContext;

    @Autowired
    private OnDemandReadingOrderDao odroDao;

    @Autowired
    private MeterDao meterDao;
    
    @WebMethod
	@WebResult(name = "getHistoricalReadingResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	public GetHistoricalReadingResponse getHistoricalReading(
			@WebParam(name = "getHistoricalReadingRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading") GetHistoricalReadingRequest payload)
			throws ApplicationFaultException {

		checkParam(payload);
		GetHistoricalReadingResponse responseData = null;

		try {
			if (log.isDebugEnabled()) { log.debug(payload); }

			Meter meter = meterDao.get(payload.getMeterSerialNumber());
			MeterType meterType = MeterType.valueOf(meter.getMeterType()
					.getName());

			String meterValueDate = null;
            if (payload.getMeterValueDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                meterValueDate = sdf.format(payload.getMeterValueDate()
                        .toGregorianCalendar().getTime());

            }
	    
			Map<String, Double> rtnMap = odroDao.getHistoricalMeteringData(meter, meterValueDate);

			responseData = new GetHistoricalReadingResponse(payload);
			MeterValue[] meterValue = null;

            if (rtnMap != null && rtnMap.size() > 0) {
                meterValue = new MeterValue[rtnMap.size()];
                int idx = 0;
                if (rtnMap.get("meterValueEnergy") != null) {
                    if (meterType == MeterType.EnergyMeter) {
                        meterValue[idx++] = new MeterValue(
                                MeterValueMeasurementType.ACTIVE_ENERGY_POSITIVE,
                                (new BigDecimal(1000.13)).setScale(3, RoundingMode.FLOOR),
                                MeasurementUnit.KWH);
                    } else if (meterType == MeterType.HeatMeter) {
                        /** TODO 아래 내용은 검토가 필요하다. 임시로 입력함. */
                        meterValue[idx++] = new MeterValue(
                                MeterValueMeasurementType.DISTRICT_HEATING_ENERGY,
                                (new BigDecimal(1000.13)).setScale(3, RoundingMode.FLOOR),
                                MeasurementUnit.KWH);
                    }
                }
                if (rtnMap.get("meterValueVolume") != null) {
                    if (meterType == MeterType.GasMeter
                            || meterType == MeterType.WaterMeter) {
                        /** TODO 가스 수도의 measurmentType 추가 필요.
                        meterValue[idx++] = new MeterValue(
                                MeterValueMeasurementType.XXXXXX,
                                (new BigDecimal(1000.13)).setScale(3, RoundingMode.FLOOR),
                                MeasurementUnit.M3);
                        */
                    } else if (meterType == MeterType.HeatMeter) {
                        meterValue[idx++] = new MeterValue(
                                MeterValueMeasurementType.DISTRICT_HEATING_VOLUME,
                                (new BigDecimal(1000.13)).setScale(3, RoundingMode.FLOOR), 
                                MeasurementUnit.M3);
                    }
                }
			}
			
			if (meterValue == null) {
				throw new MeterValueNotFound("");
			}
			for (int i = 0; i < meterValue.length; i++) {
				responseData.getMeterValue().add(meterValue[i]);
			}

		} catch (Exception e) {
			throw checkException(e);
		}
		return responseData;
	}

	@WebMethod
	@WebResult(name = "addReadingOrderResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	public AddReadingOrderResponse addReadngOrder(
			@WebParam(name = "addReadingOrderRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading") AddReadingOrderRequest payload)
			throws ApplicationFaultException {

		checkParam(payload);
		AddReadingOrderResponse responseData = null;
		try {
			if (log.isDebugEnabled()) { log.debug(payload); }

			OnDemandReadingOrder dataBean = (OnDemandReadingOrder) convertReadingOrder(payload);

			Meter meter = meterDao.get(payload.getMeterSerialNumber());
			MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
			
			dataBean.setMeterType(Integer.parseInt(CommonConstants.getMeterTypeCode(meterType)));

			if(meterType == CommonConstants.MeterType.WaterMeter) {
				throw new ApplicationFaultException(FaultCode.FC_117);
			}

			List<OnDemandReadingOrder> list1 = odroDao.getOnDemandReadingOrder(dataBean.getReferenceId());
			if (list1 != null && list1.size() > 0) {
				throw new ApplicationFaultException(FaultCode.FC_108);
			}

			OnDemandReadingOrder temp2 = (OnDemandReadingOrder) dataBean.clone();
			temp2.setReferenceId(null);			
			List<OnDemandReadingOrder> list2 = null;
			if (temp2.getMeterValueDate() != null) {
				list2 = odroDao.searchOnDemandReadingOrder(temp2, " orderStatus in (101,102,201,202) ");
			}
			if (list2 != null && list2.size() > 0) {
				throw new ApplicationFaultException(FaultCode.FC_110);
			}

			dataBean.setOrderStatus(101);
			Object resObject = odroDao.add(dataBean);

			if (resObject == null || !resObject.equals(dataBean)) {
				throw new Exception("Operation failed.");
			}
		} catch (Exception e) {
			throw checkException(e);
		}
		return responseData;
	}

	@WebMethod(operationName = "deleteReadingOrder")
	@WebResult(name = "deleteReadingOrderResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	public DeleteReadingOrderResponse deleteReadngOrder(
			@WebParam(name = "deleteReadingOrderRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading") DeleteReadingOrderRequest payload)
			throws ApplicationFaultException {
		checkParam(payload);
		DeleteReadingOrderResponse responseData = null;
		HandleReadingRequest req = null;
		OnDemandReadingOrder odro = null;

		try {
			if (log.isDebugEnabled()) { log.debug(payload); }

			OnDemandReadingOrder dataBean = (OnDemandReadingOrder) convertReadingOrder(payload);

			List<OnDemandReadingOrder> list = odroDao.searchOnDemandReadingOrder(dataBean, null);

			if (list != null && list.size() > 0) {
				odro = (OnDemandReadingOrder) list.get(0);
				req = convertReadingOrderDataToHandleReadingRequestForDelete(odro);
			} else {
				throw new ApplicationFaultException(FaultCode.FC_100);
			}

			int result = odroDao.deleteOnDemandReadingOrder(dataBean);

			if (result == 1) {
				HandleReadingResponse res = null;
				try {
					res = sendingCallbackMessage(req);
					String[] fieldName = new String[] { "handleDate", "isSend" };
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyyMMddHHmmss");
					String handleDate = sdf.format(res.getHandledDate()
							.toGregorianCalendar().getTime());
					Object[] fieldValue = new Object[] { handleDate, 1 };
					odroDao.updateOnDemandReadingOrder(odro, fieldName, fieldValue);
				} catch (Exception e) {
					log.error(e, e);
				}
				responseData = new DeleteReadingOrderResponse();
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
	@WebResult(name = "searchReadingOrdersResponse", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading")
	public SearchReadingOrdersResponse searchReadingOrders(
			@WebParam(name = "searchReadingOrdersRequest", targetNamespace = "http://ws.command.fep.aimir.com/xsd/OnDemandReading") SearchReadingOrdersRequest payload)
			throws ApplicationFaultException {

		checkParam(payload);
		SearchReadingOrdersResponse responseData = null;
		try {
			if (log.isDebugEnabled()) { log.debug(payload); }

			OnDemandReadingOrder dataBean = (OnDemandReadingOrder) convertReadingOrder(payload);
			
			List<OnDemandReadingOrder> result = odroDao.searchOnDemandReadingOrder(dataBean, null);

			if (result != null) {
				responseData = new SearchReadingOrdersResponse();
				ReadingOrder ro = null;
				for (int i = 0; i < result.size(); i++) {
					ro = (ReadingOrder) convertReadingOrder(result.get(i));
					responseData.getReadingOrder().add(ro);
				}
			} else {
				throw new Exception("Data not existed.");
			}
		} catch (Exception e) {
			throw checkException(e);
		}
		return responseData;
	}

	protected Object convertReadingOrder(Object obj) {
		if (obj instanceof AddReadingOrderRequest) {
			AddReadingOrderRequest obj2 = (AddReadingOrderRequest) obj;
			OnDemandReadingOrder dataBean = new OnDemandReadingOrder();

			dataBean.setReferenceId(obj2.getReferenceId().longValue());
			dataBean.setMeterSerialNumber(obj2.getMeterSerialNumber());

			if (obj2.getMeterValueDate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String meterValueDate = sdf.format(obj2.getMeterValueDate()
						.toGregorianCalendar().getTime());
				dataBean.setMeterValueDate(meterValueDate);
			}
			return dataBean;
		} else if (obj instanceof DeleteReadingOrderRequest) {
			DeleteReadingOrderRequest obj2 = (DeleteReadingOrderRequest) obj;
			OnDemandReadingOrder dataBean = new OnDemandReadingOrder();

			dataBean.setReferenceId(obj2.getReferenceId().longValue());
			return dataBean;

		} else if (obj instanceof SearchReadingOrdersRequest) {
			SearchReadingOrdersRequest obj2 = (SearchReadingOrdersRequest) obj;
			OnDemandReadingOrder dataBean = new OnDemandReadingOrder();

			dataBean.setReferenceId(obj2.getReferenceId() == null ? null : obj2
					.getReferenceId().longValue());
			dataBean.setMeterSerialNumber(obj2.getMeterSerialNumber());
			if (obj2.getOrderStatus() != null) {
				dataBean.setOrderStatus((int) obj2.getOrderStatus().getValue());
			}
			String meterValueDateFrom = null;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			if (obj2.getReadingOrderFromDate() != null) {
				meterValueDateFrom = sdf.format(obj2.getReadingOrderFromDate()
						.toGregorianCalendar().getTime());
			}
			dataBean.setMeterValueDateFrom(meterValueDateFrom);
			String meterValueDateTo = null;
			if (obj2.getReadingOrderToDate() != null) {
				meterValueDateTo = sdf.format(obj2.getReadingOrderToDate()
						.toGregorianCalendar().getTime());
			}
			dataBean.setMeterValueDateTo(meterValueDateTo);
			if (obj2.getOrderStatus() != null) {
				dataBean.setOrderStatus((int) obj2.getOrderStatus().getValue());
			}
			return dataBean;
		} else if (obj instanceof OnDemandReadingOrder) {
			OnDemandReadingOrder obj2 = (OnDemandReadingOrder) obj;
			ReadingOrder dataBean = new ReadingOrder();
			dataBean.setReferenceId(new BigInteger(""
					+ obj2.getReferenceId().longValue()));
			dataBean.setMeterSerialNumber(obj2.getMeterSerialNumber());
			if (obj2.getMeterValueDate() != null) {
				GregorianCalendar gcal = new GregorianCalendar();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				try {
					Date d = sdf.parse(obj2.getMeterValueDate());
					gcal.setTime(d);
					dataBean.setMeterValueDate(DatatypeFactory.newInstance()
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

	private HandleReadingRequest convertReadingOrderDataToHandleReadingRequestForDelete(
			OnDemandReadingOrder data) {
		HandleReadingRequest req = new HandleReadingRequest();
		req.setReferenceId(new BigInteger(Long.toString(data.getReferenceId())));
		req.setMeterSerialNumber(data.getMeterSerialNumber());

		GregorianCalendar gcal = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			Date d = sdf.parse(data.getMeterValueDate());
			gcal.setTime(d);
			req.setMeterValueDate(DatatypeFactory.newInstance()
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
		} else if (param instanceof GetHistoricalReadingRequest) {
			/*
			 * Get Historical Reading check logic
			 */

            /*
             * Reference Id
             * Not Null
             * Max 9999999999
             */
            if (((GetHistoricalReadingRequest) param).getReferenceId() == null
                    || ((GetHistoricalReadingRequest) param).getReferenceId()
                    .equals("")) {
                if (details.length() > 0) {
                    details.append(", ");
                }
                details.append("ReferenceId is null");
            } else {
                if (((GetHistoricalReadingRequest) param).getReferenceId()
                        .longValue() > 9999999999l) {
                    if (details.length() > 0) {
                        details.append(", ");
                    }
                    details.append("ReferenceId exceeded 9999999999.");
                }
            }

            /*
			 * Meter value date
			 */
			if (((GetHistoricalReadingRequest) param).getMeterValueDate() == null) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("MeterValueDate is null");
			} else {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String meterValueDate = sdf
						.format(((GetHistoricalReadingRequest) param)
								.getMeterValueDate().toGregorianCalendar()
								.getTime());
				if (!meterValueDate.endsWith("0000")) {
					if(details.length() == 0) {
						throw new ApplicationFaultException(FaultCode.FC_104);
					}
				}
			}

			/*
			 * meterSerialNumber
			 * Not Null
			 */
			if (((GetHistoricalReadingRequest) param).getMeterSerialNumber() == null
					|| ((GetHistoricalReadingRequest) param).getMeterSerialNumber()
							.equals("")) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("MeterSerialNumber is null");
			} else {
				Meter meter = meterDao.get(((GetHistoricalReadingRequest) param).getMeterSerialNumber());
				if(meter == null) {
					log.error("Meter["
							+ ((GetHistoricalReadingRequest) param)
									.getMeterSerialNumber() + "] have not exist.");
					if (details.length() == 0) {
						throw new ApplicationFaultException(
								FaultCode.FC_111);
					}
				}
			}
		} else if (param instanceof AddReadingOrderRequest) {
			/*
			 * Add reading order check logic
			 */

			/*
			 * Reference Id
			 * Not Null
			 * max value 9999999999
			 */
			if (((AddReadingOrderRequest) param).getReferenceId() == null
					|| ((AddReadingOrderRequest) param).getReferenceId()
							.equals("")) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("ReferenceId is null");
			} else {
				if (((AddReadingOrderRequest) param).getReferenceId()
						.longValue() > 9999999999l) {
					if (details.length() > 0) {
						details.append(", ");
					}
					details.append("ReferenceId exceeded 9999999999.");
				}
			}
			
			/*
			 * Meter value date
			 * Future date
			 */
			if (((AddReadingOrderRequest) param).getMeterValueDate() != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				String meterValueDate = sdf
						.format(((AddReadingOrderRequest) param)
								.getMeterValueDate().toGregorianCalendar()
								.getTime());
				try {
					if (Long.parseLong(meterValueDate) < Long
							.parseLong(TimeUtil.getCurrentTime())) {
						if(details.length() == 0) {
							throw new ApplicationFaultException(FaultCode.FC_114);
						}
					}
				} catch (ApplicationFaultException e) {
					throw e;
				} catch (Exception e) {
					details.append(e.getMessage());
				}
			}

			/*
			 * meterSerialNumber
			 * Not Null
			 */
			if (((AddReadingOrderRequest) param).getMeterSerialNumber() == null
					|| ((AddReadingOrderRequest) param).getMeterSerialNumber().equals(
							"")) {
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
			}
		} else if (param instanceof DeleteReadingOrderRequest) {
			if (((DeleteReadingOrderRequest) param).getReferenceId() == null
					|| ((DeleteReadingOrderRequest) param).getReferenceId()
							.equals("")) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("ReferenceId is null");
			} else {
				if (((DeleteReadingOrderRequest) param).getReferenceId()
						.longValue() > 9999999999l) {
					if (details.length() > 0) {
						details.append(", ");
					}
					details.append("ReferenceId exceeded 9999999999.");
				}
			}
		} else if (param instanceof SearchReadingOrdersRequest) {
			if (((SearchReadingOrdersRequest) param).getReadingOrderFromDate() == null) {
				if (details.length() > 0) {
					details.append(", ");
				}
				details.append("ReadingOrderFromDate is null");
			}
			
			if (((SearchReadingOrdersRequest) param).getReferenceId() != null) {
				if (((SearchReadingOrdersRequest) param).getReferenceId()
						.longValue() > 9999999999l) {
					if (details.length() > 0) {
						details.append(", ");
					}
					details.append("ReferenceId exceeded 9999999999.");
				}
			}
			
			if (((SearchReadingOrdersRequest) param).getMeterSerialNumber() != null
					&& !((SearchReadingOrdersRequest) param).getMeterSerialNumber().equals(
							"")) {
				Meter meter = meterDao.get(((SearchReadingOrdersRequest) param).getMeterSerialNumber());
				if(meter == null) {
					log.error("Meter["
							+ ((SearchReadingOrdersRequest) param)
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

	private HandleReadingResponse sendingCallbackMessage(
			HandleReadingRequest req) throws Exception {
		boolean isSend = Boolean.parseBoolean(FMPProperty.getProperty(
				"interface.OnDemandReadingCallback.isSend", "false"));
		if (!isSend) {
			throw new Exception("Currently AIMIR can't send handling data");
		}
		HandleReadingResponse res = sendingCallbackMessageJBossWSCXF(req);
		return res;
	}

	private HandleReadingResponse sendingCallbackMessageJBossWSCXF(
			HandleReadingRequest req)
			throws MalformedURLException, ApplicationFaultException {

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(OnDemandReadingCallback.class);
		factory.setAddress(FMPProperty.getProperty(
				"interface.OnDemandReadingCallback.endpoint",
				"http://localhost:8080/services/OnDemandReadingCallback"));
		OnDemandReadingCallback port = (OnDemandReadingCallback) factory.create();

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

		HandleReadingResponse res = port.handleReading(req);
		return res;
	}

}