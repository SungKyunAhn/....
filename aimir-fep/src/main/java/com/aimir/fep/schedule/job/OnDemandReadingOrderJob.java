package com.aimir.fep.schedule.job;

import java.lang.Thread.State;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.dao.system.OnDemandReadingOrderDao;
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
import com.aimir.fep.schedule.task.OnDemandReadingOrderRunnable;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.system.OnDemandReadingOrder;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

public class OnDemandReadingOrderJob extends QuartzJobBean {
	private static Log log = LogFactory.getLog(OnDemandReadingOrderJob.class);

	private static boolean isRun = false;
	private static Hashtable<String, Thread> executeTable = null;

	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {

		String jobName = context.getJobDetail().getDescription();
		//log.info("Executing job: " + jobName + " executing at " + new Date());

		if (executeTable == null) {
			executeTable = new Hashtable<String, Thread>();
		}

		try {

			OnDemandReadingOrderDao roMgr = DataUtil
					.getBean(OnDemandReadingOrderDao.class);

			List<OnDemandReadingOrder> list = roMgr
					.listOnDemandReadingOrder("o.orderStatus in (101,102,201,202) ");

			if (list != null && !list.isEmpty()) {
				log.info("OnDemandReadingOrder target list count : "
						+ list.size());
			} else {
				//log.info("OnDemandReadingOrder target list null");
			}

			if (list != null && list.size() > 0) {
				OnDemandReadingOrder[] ro = (OnDemandReadingOrder[]) list
						.toArray(new OnDemandReadingOrder[0]);
				for (int i = 0; i < ro.length; i++) {
					if (ro[i].getOrderStatus().intValue() != OrderStatus.INPROGRESS
							.getValue()
							&& ro[i].getOrderStatus().intValue() != OrderStatus.ORDERED
									.getValue()) {
						roMgr.updateOnDemandReadingOrder(ro[i],
								new String[] { "orderStatus" },
								new Object[] { 201 });
					}
					String key = StringUtil.nullToBlank(ro[i].getUserName())
							+ "_" + ro[i].getReferenceId() + "_"
							+ ro[i].getId();

					log.debug("key : " + key + "     started.\n"
							+ ro[i].toString());

					if (ro[i].getMeterValueDate() == null || ro[i].getMeterValueDate().equals("")) {
						if (!executeTable.containsKey(key)) {

							log.debug("executeTable have not exist this key("
									+ key + "). executeTable size="
									+ executeTable.size());

							if (executeTable.size() <= Integer
									.parseInt(FMPProperty
											.getProperty(
													"interface.OnDemandReading.executeMaxSize",
													"10"))) {

								ro[i].setOrderStatus((int) OrderStatus.INPROGRESS
										.getValue());
								Thread t = new Thread(
										new OnDemandReadingOrderRunnable(
												executeTable, key, ro[i]));
								t.setName(OnDemandReadingOrderRunnable.class
										.getSimpleName() + "_" + key);
								executeTable.put(key, t);
								t.start();
								log.debug("key[" + key + "] added..");
							}
						} else {
							log.debug("Already added key[" + key + "]");
						}
					} else {
						if (Long.parseLong(ro[i].getMeterValueDate()) > Long
								.parseLong(DateTimeUtil
										.getDateString(new Date()))
								&& Long.parseLong(ro[i].getMeterValueDate()) < Long
										.parseLong(DateTimeUtil.getDateString(new Date(
												TimeUtil.getCurrentLongTime() + 1000 * 60 * 5)))) {
							log.debug("meterValueDate is within 5min.");
							if (!executeTable.containsKey(key)) {
								if (executeTable.size() <= Integer
										.parseInt(FMPProperty
												.getProperty(
														"interface.OnDemandReading.executeMaxSize",
														"10"))) {
									ro[i].setOrderStatus((int) OrderStatus.INPROGRESS
											.getValue());
									Thread t = new Thread(
											new OnDemandReadingOrderRunnable(
													executeTable, key, ro[i]));
									t.setName(OnDemandReadingOrderRunnable.class
											.getSimpleName() + "_" + key);
									executeTable.put(key, t);
									t.start();
									log.debug("key[" + key + "] added..");
								}
							} else {
								log.debug("Already added key[" + key + "]");
							}
						} else if (Long.parseLong(ro[i].getMeterValueDate()) < Long
								.parseLong(DateTimeUtil.getDateString(new Date(
										TimeUtil.getCurrentLongTime())))) {
							if (!executeTable.containsKey(key)) {
								if (ro[i].isSend()) {
									if (ro[i].getMeterReadingEnergy() != null
											|| ro[i].getMeterReadingVolume() != null) {
										ro[i].setOrderStatus((int) OrderStatus.PERFORMED
												.getValue());
									} else {
										ro[i].setOrderStatus((int) OrderStatus.FAILED
												.getValue());
										ro[i].setApplicationFault(FaultCode.FC_119
												.getCode());
									}
									roMgr.updateOnDemandReadingOrder(
											ro[i],
											new String[] { "orderStatus" },
											new Object[] { ro[i].getOrderStatus() });

								} else {
									if (ro[i].getMeterReadingEnergy() == null
											&& ro[i].getMeterReadingVolume() == null) {
										ro[i].setApplicationFault(FaultCode.FC_119
												.getCode());
									}
									HandleReadingRequest req = convertReadingOrderHandleReadingRequest(ro[i]);
									HandleReadingResponse res = sendingCallbackMessage(req);
									String handleDate = null;
									if (res != null) {
										SimpleDateFormat sdf = new SimpleDateFormat(
												"yyyyMMddHHmmss");
										handleDate = sdf.format(res
												.getHandledDate()
												.toGregorianCalendar()
												.getTime());
									}
									ro[i].setSend(true);
									String[] paramList = null;
									Object[] paramValues = null;
									if (ro[i].getMeterReadingEnergy() != null
											|| ro[i].getMeterReadingVolume() != null) {
										paramList = new String[] { "isSend",
												"orderStatus", "handleDate" };
										paramValues = new Object[] {
												ro[i].isSendInt(),
												OrderStatus.PERFORMED.getValue(),
												handleDate };
									} else {
										paramList = new String[] { "isSend",
												"orderStatus", "handleDate",
												"applicationFault" };
										paramValues = new Object[] {
												ro[i].isSendInt(),
												OrderStatus.FAILED.getValue(),
												handleDate,
												ro[i].getApplicationFault()};
									}
									roMgr.updateOnDemandReadingOrder(ro[i],
											paramList, paramValues);
								}
							}
						} else {

						}
					}
				}
			}
			if (executeTable.keySet().size() > 0) {
				Hashtable<String, Thread> executeTableClone = (Hashtable<String, Thread>) executeTable
						.clone();
				Iterator<String> iteratorKey = executeTableClone.keySet()
						.iterator();
				while (iteratorKey.hasNext()) {
					String key = (String) iteratorKey.next();
					Thread t = (Thread) executeTable.get(key);
					if (t != null && t.getState() == State.TERMINATED) {
						executeTable.remove(key);
						t = null;
						log.debug(key + " key removed");
					}
				}
			}
		} catch (Exception e) {
			log.error(e, e);
		}

		//log.info("Executing job: " + jobName + " ending at " + new Date());
	}

	private HandleReadingRequest convertReadingOrderHandleReadingRequest(
			OnDemandReadingOrder ro) {
		HandleReadingRequest req = null;

		try {
			req = new HandleReadingRequest(ro);
			if (ro.getApplicationFault() != null) {
				req.setApplicationFault(new ApplicationFault(FaultCode
						.getFaultCode(ro.getApplicationFault())));
			}

			String meterValueDate = ro.getMeterValueDate();

			if (ro.getMeterReadingEnergy() != null) {
				if (CommonConstants.getMeterTypeCode(MeterType.EnergyMeter)
						.equals(ro.getMeterType().toString())) {
					MeterValue mv = new MeterValue();
					mv = new MeterValue();
					mv.setMeasurementUnit(MeasurementUnit.KWH);
					mv.setMeterReading((new BigDecimal(ro
							.getMeterReadingEnergy())).setScale(3,
							RoundingMode.FLOOR));
					mv.setMeterValueMeasurementType(MeterValueMeasurementType.ACTIVE_ENERGY_POSITIVE);

					req.getMeterValue().add(mv);
					req.setOrderStatus(OrderStatus.PERFORMED);
				}
				if (CommonConstants.getMeterTypeCode(MeterType.HeatMeter)
						.equals(ro.getMeterType().toString())) {

					MeterValue mv = new MeterValue();
					mv.setMeasurementUnit(MeasurementUnit.KWH);
					mv.setMeterReading((new BigDecimal(ro
							.getMeterReadingEnergy())).setScale(3,
							RoundingMode.FLOOR));
					mv.setMeterValueMeasurementType(MeterValueMeasurementType.DISTRICT_HEATING_ENERGY);

					req.getMeterValue().add(mv);
					req.setOrderStatus(OrderStatus.PERFORMED);
				}
			}
			if (ro.getMeterReadingVolume() != null) {
				if (CommonConstants.getMeterTypeCode(MeterType.WaterMeter)
						.equals(ro.getMeterType().toString())) {
					/**
					 * TODO 수도 차후 코드 생성 및 추가
					 */
				}
				if (CommonConstants.getMeterTypeCode(MeterType.HeatMeter)
						.equals(ro.getMeterType().toString())) {
					MeterValue mv = new MeterValue();
					mv.setMeasurementUnit(MeasurementUnit.M3);
					mv.setMeterReading((new BigDecimal(ro
							.getMeterReadingVolume())).setScale(3,
							RoundingMode.FLOOR));
					mv.setMeterValueMeasurementType(MeterValueMeasurementType.DISTRICT_HEATING_VOLUME);

					req.getMeterValue().add(mv);
				}
			}

			if (req.getMeterValue().size() > 0) {
				req.setOrderStatus(OrderStatus.PERFORMED);
			} else {
				req.setOrderStatus(OrderStatus.FAILED);
			}

			if (meterValueDate != null && !meterValueDate.equals("")) {
				GregorianCalendar gcal = new GregorianCalendar();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				try {
					Date d = sdf.parse(meterValueDate);
					gcal.setTime(d);
					req.setMeterValueDate(DatatypeFactory.newInstance()
							.newXMLGregorianCalendar(gcal));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return req;
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
			HandleReadingRequest req) throws MalformedURLException,
			ApplicationFaultException {

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(OnDemandReadingCallback.class);
		factory.setAddress(FMPProperty.getProperty(
				"interface.OnDemandReadingCallback.endpoint",
				"https://localhost:8080/services/OnDemandCallback"));
		OnDemandReadingCallback port = (OnDemandReadingCallback) factory
				.create();

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
