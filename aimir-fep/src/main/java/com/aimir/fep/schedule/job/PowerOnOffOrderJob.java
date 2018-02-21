package com.aimir.fep.schedule.job;

import java.lang.Thread.State;
import java.math.BigDecimal;
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

import com.aimir.dao.system.PowerOnOffOrderDao;
import com.aimir.fep.command.ws.PowerOnOffCallback;
import com.aimir.fep.command.ws.data.ApplicationFault;
import com.aimir.fep.command.ws.data.ApplicationFaultException;
import com.aimir.fep.command.ws.data.HandlePowerOnOffRequest;
import com.aimir.fep.command.ws.data.HandlePowerOnOffResponse;
import com.aimir.fep.command.ws.data.MeterValue;
import com.aimir.fep.command.ws.datatype.FaultCode;
import com.aimir.fep.command.ws.datatype.MeasurementUnit;
import com.aimir.fep.command.ws.datatype.MeterValueMeasurementType;
import com.aimir.fep.command.ws.datatype.MeterValueStatus;
import com.aimir.fep.command.ws.datatype.OrderStatus;
import com.aimir.fep.schedule.task.PowerOnOffOrderRunnable;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.system.PowerOnOffOrder;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

public class PowerOnOffOrderJob extends QuartzJobBean{
	private static Log log = LogFactory.getLog(PowerOnOffOrderJob.class);

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
        	PowerOnOffOrderDao roMgr = DataUtil
					.getBean(PowerOnOffOrderDao.class);

			List<PowerOnOffOrder> list = roMgr
					.listPowerOnOffOrder("p.orderStatus in (101,102,201,202) ");

        	if(list != null && !list.isEmpty()) {
    	        log.debug("PowerOnOffOrder target list count : " +list.size());
        	}else {
    	        //log.debug("PowerOnOffOrder target list null");
        	}

	        if(list!= null && list.size()>0) {
	        	PowerOnOffOrder[] ro = (PowerOnOffOrder[]) list.toArray(new PowerOnOffOrder[0]);
	        	for (int i = 0; i < ro.length; i++) {
					if (ro[i].getOrderStatus().intValue() != OrderStatus.INPROGRESS
							.getValue()
							&& ro[i].getOrderStatus().intValue() != OrderStatus.ORDERED
									.getValue()) {
						roMgr.updatePowerOnOffOrder(ro[i],
								new String[] { "orderStatus" },
								new String[] { "201" });
					}
					String key = StringUtil.nullToBlank(ro[i].getUserName())
							+ "_" + ro[i].getReferenceId() + "_"
							+ ro[i].getId();

					log.debug("key : " + key + "     started.\n"
							+ ro[i].toString());

					if (ro[i].getPowerOperationDate() == null || ro[i].getPowerOperationDate().equals("")) {
						if(!executeTable.containsKey(key)) {
							log.debug("executeTable have not exist this key("
									+ key + "). executeTable size="
									+ executeTable.size());

							if (executeTable.size() <= Integer.parseInt(FMPProperty
									.getProperty("interface.PowerOnOff.executeMaxSize", "10"))) {

								ro[i].setOrderStatus((int) OrderStatus.INPROGRESS.getValue());
								Thread t = new Thread(new PowerOnOffOrderRunnable(executeTable, key, ro[i]));
								t.setName(PowerOnOffOrderRunnable.class.getSimpleName() + "_" + key);
								executeTable.put(key, t);
								t.start();
								log.debug("key[" + key + "] added..");
							}
						} else {
							log.debug("Already added key[" + key + "]");
						}
					} else {
						if (Long.parseLong(ro[i].getPowerOperationDate()) > Long
								.parseLong(DateTimeUtil.getDateString(new Date()))
								&& Long.parseLong(ro[i].getPowerOperationDate()) < Long
										.parseLong(DateTimeUtil.getDateString(new Date(
												TimeUtil.getCurrentLongTime() + 1000*60*5)))) {
							log.debug("powerOperationDate is within 5min.");
							if(!executeTable.containsKey(key)) {
								if (executeTable.size() <= Integer.parseInt(FMPProperty
										.getProperty("interface.PowerOnOff.executeMaxSize", "10"))) {

									Thread t = new Thread(new PowerOnOffOrderRunnable(executeTable, key, ro[i]));
									t.setName(PowerOnOffOrderRunnable.class.getSimpleName() + "_" + key);
									executeTable.put(key, t);
									t.start();
									log.debug("key[" + key + "] added..");
								}
							} else {
								log.debug("Already added key[" + key + "]");
							}
						} else if (Long.parseLong(ro[i].getPowerOperationDate()) < Long
								.parseLong(DateTimeUtil.getDateString(new Date(
										TimeUtil.getCurrentLongTime())))) {
							if(!executeTable.containsKey(key)) {
								if(ro[i].isSend()) {
									if(ro[i].getMeterReading()!=null) {
										ro[i].setOrderStatus((int) OrderStatus.PERFORMED.getValue());
									} else {
										ro[i].setOrderStatus((int) OrderStatus.FAILED.getValue());
										ro[i].setApplicationFault(FaultCode.FC_119.getCode());
									}
									roMgr.updatePowerOnOffOrder(
											ro[i],
											new String[] { "orderStatus" },
											new String[] { ""
													+ ro[i].getOrderStatus() });
								} else {
									if(ro[i].getMeterReading()==null) {
										ro[i].setApplicationFault(FaultCode.FC_119.getCode());
									}
									HandlePowerOnOffRequest req = convertPowerOnOffOrderHandlePowerOnOffRequest(ro[i]);
									HandlePowerOnOffResponse res = sendingCallbackMessage(ro[i].getUserName(), req);
									String handleDate = null;
									if(res != null) {
										SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
										handleDate = sdf.format(res.getHandledDate()
												.toGregorianCalendar().getTime());
									}
									ro[i].setSend(true);
									String[] paramList = null;
									Object[] paramValues = null;
									if(ro[i].getMeterReading()!=null) {
										paramList = new String[] {
												"isSend", "orderStatus", "handleDate"
												};
										paramValues = new Object[] {
												ro[i].isSendInt(),
												OrderStatus.PERFORMED.getValue(),
												handleDate };
									} else {
										paramList = new String[] {
												"isSend", "orderStatus", "handleDate",
												"applicationFault"
												};
										paramValues = new Object[] {
												ro[i].isSendInt(),
												OrderStatus.FAILED.getValue(),
												handleDate,
												ro[i].getApplicationFault()};
									}
									roMgr.updatePowerOnOffOrder(ro[i],
											paramList, paramValues);
								}
							}
						} else {

						}
					}
				}
	        }
	        if(executeTable.keySet().size()>0) {
	        	Hashtable<String, Thread> executeTableClone = (Hashtable<String, Thread>) executeTable.clone();
		        Iterator<String> iteratorKey = executeTableClone.keySet().iterator();
		        while(iteratorKey.hasNext()) {
		        	String key = (String) iteratorKey.next();
		        	Thread t = (Thread) executeTable.get(key);
					if(t != null && t.getState() == State.TERMINATED) {
						t = null;
						executeTable.remove(key);
						log.debug(key + " key removed");
					}
		        }
	        }
        }catch(Exception e) {
        	log.error(e,e);
        }

        //log.info("Executing job: " + jobName + " ending at " + new Date());		
	}

    private HandlePowerOnOffRequest convertPowerOnOffOrderHandlePowerOnOffRequest(PowerOnOffOrder ro) {
		HandlePowerOnOffRequest req = null;

		try {
			req = new HandlePowerOnOffRequest(ro);

			req.setApplicationFault(new ApplicationFault(FaultCode
					.getFaultCode(ro
							.getApplicationFault())));

			String powerOperationDate = ro.getPowerOperationDate();
			if(powerOperationDate != null && !powerOperationDate.equals("")) {
				GregorianCalendar gcal = new GregorianCalendar();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				try {
					Date d = sdf.parse(powerOperationDate);
					gcal.setTime(d);
					req.setPowerOperationDate(DatatypeFactory.newInstance()
							.newXMLGregorianCalendar(gcal));
				} catch (Exception e) {
				}
			}
			if(ro.getMeterReading()!=null){
				MeterValue mv = new MeterValue();
				mv = new MeterValue();
				mv.setMeasurementUnit(MeasurementUnit.KWH);
				mv.setMeterReading(new BigDecimal(ro.getMeterReading()));
				mv.setMeterValueMeasurementType(MeterValueMeasurementType.ACTIVE_ENERGY_POSITIVE);

				req.getMeterValue().add(mv);
				req.setMeterValueStatus(MeterValueStatus.METER_VALUE_COLLECTED);
				req.setOrderStatus(OrderStatus.PERFORMED);
			} else {
				req.setMeterValueStatus(MeterValueStatus.METER_VALUE_NOT_BE_COLLECTED_NO_ANSWER_METER);
				req.setOrderStatus(OrderStatus.FAILED);
			}
		} catch (Exception e) {
		}
		return req;
    }

	private HandlePowerOnOffResponse sendingCallbackMessage(String userName, HandlePowerOnOffRequest req) throws Exception {

		boolean isSend = Boolean.parseBoolean(FMPProperty.getProperty(
				"interface.PowerOnOffCallback.isSend", "false"));
		if (!isSend) {
			throw new Exception("Currently AIMIR can't send handling data");
		}
		HandlePowerOnOffResponse res = sendingCallbackMessageJBossWSCXF(req);
		return res;
	}

	private HandlePowerOnOffResponse sendingCallbackMessageJBossWSCXF( HandlePowerOnOffRequest req)
	throws MalformedURLException, ApplicationFaultException {

		JaxWsProxyFactoryBean factory = new JaxWsProxyFactoryBean();
		factory.setServiceClass(PowerOnOffCallback.class);
		factory.setAddress(FMPProperty.getProperty(
				"interface.OnDemandReadingCallback.endpoint",
				"https://localhost:8080/services/OnDemandCallback"));
		PowerOnOffCallback port = (PowerOnOffCallback) factory
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

		HandlePowerOnOffResponse res = port.handlePowerOnOff(req);
		return res;
	}
}
