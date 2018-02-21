package com.aimir.fep.schedule.task;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.OnDemandReadingOrderDao;
import com.aimir.fep.command.mbean.CommandBO;
import com.aimir.fep.command.ws.OnDemandReadingCallback;
import com.aimir.fep.command.ws.data.ApplicationFault;
import com.aimir.fep.command.ws.data.ApplicationFaultException;
import com.aimir.fep.command.ws.data.HandleReadingRequest;
import com.aimir.fep.command.ws.data.HandleReadingResponse;
import com.aimir.fep.command.ws.data.MeterValue;
import com.aimir.fep.command.ws.datatype.FaultCode;
import com.aimir.fep.command.ws.datatype.MeasurementUnit;
import com.aimir.fep.command.ws.datatype.MeterValueMeasurementType;
import com.aimir.fep.command.ws.datatype.OrderStatus;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.system.OnDemandReadingOrder;
import com.aimir.util.TimeUtil;

public class OnDemandReadingOrderRunnable implements Runnable {

	private static Log log = LogFactory
			.getLog(OnDemandReadingOrderRunnable.class);

	Hashtable<String, Thread> executeTable = null;
	String key = null;
	OnDemandReadingOrder ro = null;

	OnDemandReadingOrderDao odroDao = null;
	MeterDao mDao = null;

	public OnDemandReadingOrderRunnable(Hashtable<String, Thread> executeTable, String key,
			OnDemandReadingOrder ro) {
		this.executeTable = executeTable;
		this.key = key;
		this.ro = ro;

		odroDao = DataUtil.getBean(OnDemandReadingOrderDao.class);
		mDao = DataUtil.getBean(MeterDao.class);
	}

	@SuppressWarnings("unchecked")
	public void run() {

		try {
			updateInProgressReadingOrder(ro);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HandleReadingRequest req = null;

		String meterValueDate = null;
		String failMessage = null;
		Meter meter = null;
		MeterType meterType = null;

		try {
			meterValueDate = ro.getMeterValueDate();
			if (meterValueDate != null && !meterValueDate.equals("")) {
				long diff = Math.abs(TimeUtil.getLongTime(meterValueDate)
						- TimeUtil.getCurrentLongTime());
				log.debug("key:" + key + " diff:" + diff);
				Thread.sleep(diff);
				
				// 최종 Cancel 여부 검사.
				OnDemandReadingOrder roCondition = new OnDemandReadingOrder();
				roCondition.setId(ro.getId());
				List<OnDemandReadingOrder> list = odroDao.searchOnDemandReadingOrder(roCondition, null);
				if (list != null && list.size() > 0) {
					OnDemandReadingOrder roTemp = (OnDemandReadingOrder) list.get(0);
					if(roTemp.getOrderStatus().intValue() == OrderStatus.CANCELED.getValue()) {
						log.debug("key=" + key + " canceled..");
						return;
					}
				}
			}

			req = new HandleReadingRequest(ro);

			meter = mDao.get(ro.getMeterSerialNumber());

			if (meter == null) {
				req.setApplicationFault(new ApplicationFault(FaultCode.FC_111));
				req.setOrderStatus(OrderStatus.REJECTED);
				failMessage = "Meter serial number [" + ro.getMeterSerialNumber()
						+ "] have not exist.";
				throw new ApplicationFaultException(FaultCode.FC_130);
			} 

            meterType = MeterType.valueOf(meter.getMeterType().getName());
			if (meterType == MeterType.WaterMeter) {
				req.setApplicationFault(new ApplicationFault(
						FaultCode.FC_117));
				req.setOrderStatus(OrderStatus.REJECTED);
				failMessage = "Meter serial number[" + ro.getMeterSerialNumber()
						+ " is water meter.";
				throw new ApplicationFaultException(FaultCode.FC_117);
			}

			CommandBO cgw = DataUtil.getBean(CommandBO.class);

			Hashtable<String, Object> mdata = null;
			Map<String, ?> mapdata = null;

			try {
			
				mdata = cgw.doOnDemand(meter, 1, "admin");

				mapdata = (Map<String, String>) mdata.get("ondemandResult");
			} catch (Exception ne) {
				log.error(ne,ne);
			}

            if (mdata!= null) {
				if (mdata.get("resultStatus") == ResultStatus.SUCCESS) {
					if(log.isDebugEnabled()) {
			            String key = null;
			            for (Iterator<String> keyset = mapdata.keySet().iterator(); keyset.hasNext(); ) {
			                key = (String)keyset.next();
			                log.debug(key+"="+mapdata.get(key));
			            }
					}

					/* Default metering value */
					if (meterType == MeterType.EnergyMeter) {
						MeterValue mv = new MeterValue();
						mv.setMeasurementUnit(MeasurementUnit.KWH);
						mv.setMeterReading((new BigDecimal((String)	mdata.get("meteringValue"))).setScale(3, RoundingMode.FLOOR));
						mv.setMeterValueMeasurementType(MeterValueMeasurementType.ACTIVE_ENERGY_POSITIVE);
						req.getMeterValue().add(mv);
					} else if(meterType == MeterType.HeatMeter) {
						/* TODO 열량계는 모델에 따라 ch2, ch4 이거나 ch2, ch3 인 경우가 있다 물로 또 다를 수 도 있다. 모델에 따라 처리하도록 추가되어야 함. */
						/*
						MeterValue mv = new MeterValue();
						mv.setMeasurementUnit(MeasurementUnit.KWH);
						mv.setMeterReading((new BigDecimal((String)	mdata.get("meteringValue"))).setScale(3, RoundingMode.FLOOR));
						mv.setMeterValueMeasurementType(MeterValueMeasurementType.DISTRICT_HEATING_ENERGY);
						req.getMeterValue().add(mv);
						mv = new MeterValue();
						mv.setMeasurementUnit(MeasurementUnit.M3);
						mv.setMeterReading((new BigDecimal((String)	mdata.get("meteringValue"))).setScale(3, RoundingMode.FLOOR));
						mv.setMeterValueMeasurementType(MeterValueMeasurementType.DISTRICT_HEATING_VOLUME);
						req.getMeterValue().add(mv);
						*/
					}

					/* 4channel, 열량계 등 특수 한 미터 인경우 아래 샘플을 통해 추가적으로 처리가능하다. */
					/*
					if(meter.getModel().getDeviceConfig().getParserName().endsWith("KamstrupOmniPower")) {
						req.getMeterValue().clear();
		            	MeterValue mv = new MeterValue();
						mv.setMeasurementUnit(MeasurementUnit.KWH);
						mv.setMeterReading((new BigDecimal((String)	mapdata.get("GetRegister.Active energy A14"))).setScale(3, RoundingMode.FLOOR));
						mv.setMeterValueMeasurementType(MeterValueMeasurementType.ACTIVE_ENERGY_POSITIVE);
						req.getMeterValue().add(mv);
						mv = new MeterValue();
						mv.setMeasurementUnit(MeasurementUnit.KWH);
						mv.setMeterReading((new BigDecimal((String)	mapdata.get("GetRegister.Active energy A23"))).setScale(3, RoundingMode.FLOOR));
						mv.setMeterValueMeasurementType(MeterValueMeasurementType.ACTIVE_ENERGY_NEGATIVE);
						req.getMeterValue().add(mv);
						mv = new MeterValue();
						mv.setMeasurementUnit(MeasurementUnit.KVARH);
						mv.setMeterReading((new BigDecimal((String)	mapdata.get("GetRegister.Reactive energy R12"))).setScale(3, RoundingMode.FLOOR));
						mv.setMeterValueMeasurementType(MeterValueMeasurementType.REACTIVE_ENERGY_POSITIVE);
						req.getMeterValue().add(mv);
						mv = new MeterValue();
						mv.setMeasurementUnit(MeasurementUnit.KVARH);
						mv.setMeterReading((new BigDecimal((String)	mapdata.get("GetRegister.Reactive energy R34"))).setScale(3, RoundingMode.FLOOR));
						mv.setMeterValueMeasurementType(MeterValueMeasurementType.ACTIVE_ENERGY_NEGATIVE);
						req.getMeterValue().add(mv);
		            } else if(meter.getModel().getDeviceConfig().getParserName().endsWith("Aidon5530") || 
		                      meter.getModel().getDeviceConfig().getParserName().endsWith("Aidon5530_2")) {
						req.getMeterValue().clear();
		            	MeterValue mv = new MeterValue();
						mv.setMeasurementUnit(MeasurementUnit.KWH);
						mv.setMeterReading((new BigDecimal((String)	mapdata.get("Active cumulative positive energy(kWh)"))).setScale(3, RoundingMode.FLOOR));
						mv.setMeterValueMeasurementType(MeterValueMeasurementType.ACTIVE_ENERGY_POSITIVE);
						req.getMeterValue().add(mv);
						mv = new MeterValue();
						mv.setMeasurementUnit(MeasurementUnit.KWH);
						mv.setMeterReading((new BigDecimal((String)	mapdata.get("Active cumulative negative energy(kWh)"))).setScale(3, RoundingMode.FLOOR));
						mv.setMeterValueMeasurementType(MeterValueMeasurementType.ACTIVE_ENERGY_NEGATIVE);
						req.getMeterValue().add(mv);
						mv = new MeterValue();
						mv.setMeasurementUnit(MeasurementUnit.KVARH);
						mv.setMeterReading((new BigDecimal((String)	mapdata.get("Reactive cumulative positive energy(kvarh)"))).setScale(3, RoundingMode.FLOOR));
						mv.setMeterValueMeasurementType(MeterValueMeasurementType.REACTIVE_ENERGY_POSITIVE);
						req.getMeterValue().add(mv);
						mv = new MeterValue();
						mv.setMeasurementUnit(MeasurementUnit.KVARH);
						mv.setMeterReading((new BigDecimal((String)	mapdata.get("Reactive cumulative negative energy(kvarh)"))).setScale(3, RoundingMode.FLOOR));
						mv.setMeterValueMeasurementType(MeterValueMeasurementType.ACTIVE_ENERGY_NEGATIVE);
						req.getMeterValue().add(mv);
		            } else {
		            	//TODO 다른 미터에 대해서도 키값을 확인해서 처리 추가해야함.
		            }
		            */

					req.setOrderStatus(OrderStatus.PERFORMED);
				} else {
					if (meter.getMeterStatus() == CommonConstants.getMeterStatusByName(CommonConstants.MeterStatus.PowerDown.name())) {
						req.setApplicationFault(new ApplicationFault(
								FaultCode.FC_116));
						failMessage = "Meter status is power down.";
					} else {
						req.setApplicationFault(new ApplicationFault(
								FaultCode.FC_120));
						failMessage = "OnDemand command failed.";
					}
					req.setOrderStatus(OrderStatus.FAILED);
				}
			} else {
				req.setApplicationFault(new ApplicationFault(
						FaultCode.FC_120));
				req.setOrderStatus(OrderStatus.FAILED);
				failMessage = "OnDemand command response is not exist.";
			}
		} catch(ApplicationFaultException e) {
			log.error(e, e);
			failMessage = e.getMessage();
		} catch (Exception e) {
			log.error(e, e);
			if (meter.getMeterStatus() == CommonConstants.getMeterStatusByName(CommonConstants.MeterStatus.PowerDown.name())) {
				req.setApplicationFault(new ApplicationFault(
						FaultCode.FC_116));
				failMessage = "Meter status is power down.";
			} else {
				req.setApplicationFault(new ApplicationFault(FaultCode.FC_120));
			}
			req.setOrderStatus(OrderStatus.FAILED);
			failMessage = e.getMessage();
		}

		GregorianCalendar gcal = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			if (meterValueDate != null && !meterValueDate.equals("")) {
				Date d = sdf.parse(meterValueDate);
				gcal.setTime(d);
			} else {
				Date d = sdf.parse(TimeUtil.getCurrentTime());
				gcal.setTime(d);
			}
			req.setMeterValueDate(DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gcal));
		} catch (Exception e) {
			log.error(e, e);
		}

		HandleReadingResponse res = null;
		try {
			res = sendingCallbackMessage(ro.getUserName(), req);
			updateFinishReadingOrder(ro, true, req, res, failMessage); // sending
		} catch (Exception e) {
			updateFinishReadingOrder(ro, false, req, "",
					failMessage + ".... " + e.getMessage()); // sending fail
			log.error(e, e);
		}

	}

	private HandleReadingResponse sendingCallbackMessage(String userName,
			HandleReadingRequest req) throws Exception {
		boolean isSend = Boolean.parseBoolean(FMPProperty.getProperty(
				"interface.OnDemandReadingCallback.isSend", "false"));
		if (!isSend) {
			throw new Exception("Currently AIMIR can't send handling data");
		}
		HandleReadingResponse res = sendingCallbackMessageJBossWSCXF(userName, req);

		return res;
	}

	private HandleReadingResponse sendingCallbackMessageJBossWSCXF(
			String userName, HandleReadingRequest req)
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

	private void updateInProgressReadingOrder(OnDemandReadingOrder ro) {
		odroDao.updateOnDemandReadingOrder(ro, new String[] { "orderstatus" },
				new Object[] { ro.getOrderStatus() });
	}

	private void updateFinishReadingOrder(OnDemandReadingOrder ro,
			boolean isSend, HandleReadingRequest req, HandleReadingResponse res,
			String failMessage) {
		String handleDate = null;
		if (res != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			handleDate = sdf.format(res.getHandledDate().toGregorianCalendar()
					.getTime());
		}
		updateFinishReadingOrder(ro, isSend, req, handleDate, failMessage);
	}

	private void updateFinishReadingOrder(OnDemandReadingOrder ro,
			boolean isSend, HandleReadingRequest req, String handleDate,
			String failMessage) {
	    
		ro.setOrderStatus((int)req.getOrderStatus().getValue());
		ro.setSend(isSend);
		if (handleDate != null && !handleDate.equals("")) {
			ro.setHandleDate(handleDate);
		}
		if (ro.getMeterValueDate() == null && req.getMeterValueDate() != null) {
			String meterValueDate = null;
			if (req != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				meterValueDate = sdf.format(req.getMeterValueDate()
						.toGregorianCalendar().getTime());
			}
			ro.setMeterValueDate(meterValueDate);
		} else if (ro.getMeterValueDate() == null
				&& req.getMeterValueDate() == null && handleDate != null) {
			ro.setMeterValueDate(handleDate);
		}
		if (req.getMeterValue() != null && req.getMeterValue().size()>0
				&& req.getMeterValue().get(0).getMeterReading() != null) {
			ro.setMeterReadingEnergy(req.getMeterValue().get(0).getMeterReading()
					.doubleValue());
		}
		if (req.getMeterValue() != null && req.getMeterValue().size()>1
				&& req.getMeterValue().get(1).getMeterReading() != null) {
			ro.setMeterReadingVolume(req.getMeterValue().get(1).getMeterReading()
					.doubleValue());
		}
		if (req.getApplicationFault() != null) {
			ro.setApplicationFault(Integer.parseInt(req.getApplicationFault().getFaultCode()));
		}
		if (failMessage != null) {
			ro.setFailMessage(failMessage.substring(
					0,
					(failMessage.length() > 1024 ? 1023 : failMessage
							.length())));
		}
        odroDao.updateOnDemandReadingOrder(
                ro,
                new String[] { "orderstatus", "isSend", "handleDate",
                        "meterValueDate", "meterReadingEnergy",
                        "meterReadingVolume", "applicationFault", "failMessage" },
                new Object[] { ro.getOrderStatus(), ro.isSendInt(),
                        ro.getHandleDate(), ro.getMeterValueDate(),
                        ro.getMeterReadingEnergy(), ro.getMeterReadingVolume(),
                        ro.getApplicationFault(), ro.getFailMessage() });

	}
}