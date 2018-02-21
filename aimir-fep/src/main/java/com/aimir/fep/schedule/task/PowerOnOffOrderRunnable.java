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
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.PowerOnOffOrderDao;
import com.aimir.fep.command.conf.KamstrupCIDMeta;
import com.aimir.fep.command.conf.Mccb;
import com.aimir.fep.command.mbean.CommandBO;
import com.aimir.fep.command.mbean.CommandGW.OnDemandOption;
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
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.Meter;
import com.aimir.model.system.PowerOnOffOrder;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

public class PowerOnOffOrderRunnable implements Runnable {

	private static Log log = LogFactory.getLog(PowerOnOffOrderRunnable.class);

	Hashtable<String, Thread> executeTable = null;
	String key = null;
	PowerOnOffOrder ro = null;

	PowerOnOffOrderDao poooDao = null;
	MeterDao mDao = null;

	public PowerOnOffOrderRunnable(Hashtable<String, Thread> executeTable, String key,
			PowerOnOffOrder ro) {
		this.executeTable = executeTable;
		this.key = key;
		this.ro = ro;
		
		poooDao = DataUtil.getBean(PowerOnOffOrderDao.class);
		mDao = DataUtil.getBean(MeterDao.class);
	}

	public void run() {

		try {
			updateInProgressReadingOrder(ro);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HandlePowerOnOffRequest req = null;

		String powerOperationDate = null;
		String failMessage = null;
		Meter meter = null;
		MeterType meterType = null;

		try {
			powerOperationDate = ro.getPowerOperationDate();
			if (powerOperationDate != null && !powerOperationDate.equals("")) {
				long diff = Math.abs(TimeUtil.getLongTime(powerOperationDate)
						- TimeUtil.getCurrentLongTime());
				log.debug("key:" + key + " diff:" + diff);
				Thread.sleep(diff);

				// 최종 Cancel 여부 검사.
				PowerOnOffOrder roCondition = new PowerOnOffOrder();
				roCondition.setId(ro.getId());
				List<PowerOnOffOrder> list = poooDao.searchPowerOnOffOrder(ro, null);
				if (list != null && list.size() > 0) {
					PowerOnOffOrder roTemp = (PowerOnOffOrder) list.get(0);
					if (roTemp.getOrderStatus().intValue() == OrderStatus.CANCELED
							.getValue()) {
						log.debug("key=" + key + " canceled..");
						return;
					}
				}
			}

			req = new HandlePowerOnOffRequest(ro);

			meter = mDao.get(ro.getMeterSerialNumber());

			if (meter == null) {
				req.setApplicationFault(new ApplicationFault(FaultCode.FC_111));
				req.setOrderStatus(OrderStatus.REJECTED);
				failMessage = "Meter serial number [" + ro.getMeterSerialNumber()
						+ "] have not exist.";
				throw new ApplicationFaultException(FaultCode.FC_130);
			} 

            meterType = MeterType.valueOf(meter.getMeterType().getName());
            if (meterType != MeterType.EnergyMeter) {
                req.setApplicationFault(new ApplicationFault(FaultCode.FC_140));
                req.setOrderStatus(OrderStatus.REJECTED);
                failMessage = "Meter serial number["
                        + ro.getMeterSerialNumber()
                        + " is not electricity meter.";
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
                        mv.setMeterReading((new BigDecimal((String) mdata.get("meteringValue"))).setScale(3, RoundingMode.FLOOR));
                        mv.setMeterValueMeasurementType(MeterValueMeasurementType.ACTIVE_ENERGY_POSITIVE);
                        req.getMeterValue().add(mv);
                    }

					req.setMeterValueStatus(MeterValueStatus.METER_VALUE_COLLECTED);
				} else {
					if (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatusCode(CommonConstants.MeterStatus.PowerDown))) {
						req.setMeterValueStatus(MeterValueStatus.METER_VALUE_NOT_BE_COLLECTED_NOT_ENERGIZED_METER);
					} else {
						req.setMeterValueStatus(MeterValueStatus.METER_VALUE_NOT_BE_COLLECTED_NO_ANSWER_METER);
					}
				}
			} else {
				req.setMeterValueStatus(MeterValueStatus.METER_VALUE_NOT_BE_COLLECTED_NO_ANSWER_METER);
			}

			switch (req.getPowerOperation()) {
				case POWER_ON_DIRECTLY:
					failMessage = powerOn(meter, req);
					break;
				case POWER_ON_DEBLOCKING:
					failMessage = powerDeblocking(meter, req);
					break;
				case POWER_OFF:
					failMessage = powerOff(meter, req);
					break;
				default:
			}
		} catch (ApplicationFaultException e) {
			req.setMeterValueStatus(MeterValueStatus.METER_VALUE_NOT_BE_COLLECTED_NO_ANSWER_METER);
			log.error(e, e);
			failMessage = e.getMessage();
		} catch (Exception e) {
			req.setMeterValueStatus(MeterValueStatus.METER_VALUE_NOT_BE_COLLECTED_NO_ANSWER_METER);
			log.error(e, e);
			req.setApplicationFault(new ApplicationFault(FaultCode.FC_120));
			req.setOrderStatus(OrderStatus.FAILED);
			failMessage = e.getMessage();
		}

		GregorianCalendar gcal = new GregorianCalendar();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			if (powerOperationDate != null && !powerOperationDate.equals("")) {
				Date d = sdf.parse(powerOperationDate);
				gcal.setTime(d);
			} else {
				Date d = sdf.parse(TimeUtil.getCurrentTime());
				gcal.setTime(d);
			}
			req.setPowerOperationDate(DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(gcal));
		} catch (Exception e) {
			log.error(e, e);
		}

		HandlePowerOnOffResponse res = null;
		try {
			res = sendingCallbackMessage(ro.getUserName(), req);
			updateFinishPowerOnOffOrder(ro, true, req, res, failMessage); // sending
		} catch (Exception e) {
			updateFinishPowerOnOffOrder(ro, false, req, "",
					failMessage + "\b" + StringUtil.makeStackTraceToString(e)); // sending fail
			log.error(e, e);
		}

	}

	private String powerOn(Meter meter, HandlePowerOnOffRequest req) {
		CommandBO cgw = DataUtil.getBean(CommandBO.class);

		MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());
		try {

            if(meter.getModel().getDeviceConfig().getParserName().endsWith("Aidon5530") || 
                    meter.getModel().getDeviceConfig().getParserName().endsWith("Aidon5530_2")) {

                // Aidon은 MCCB 상태정보를 알수 없다.
                String res = cgw.cmdAidonMccb(meter.getModem().getMcu().getSysID(), meter.getMdsId(), Mccb.MSG_REQ[6]); // Disable Use - Disconnect
                Thread.sleep(1000);
                res = cgw.cmdAidonMccb(meter.getModem().getMcu().getSysID(), meter.getMdsId(), Mccb.MSG_REQ[5]); // Enable Use - Connect Now
                if(res != null && res.indexOf("OK+") > -1) {
                    req.setOrderStatus(OrderStatus.PERFORMED);
                } if(res != null && res.indexOf("E+") > -1) { 
                    req.setOrderStatus(OrderStatus.FAILED);
                } else {
                    req.setOrderStatus(OrderStatus.FAILED);
                    if (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatusCode(CommonConstants.MeterStatus.PowerDown))) {
                        throw new ApplicationFaultException(
                                FaultCode.FC_116);
                    } else {
                        throw new ApplicationFaultException(
                                FaultCode.FC_120);
                    }
                }

            } else if(meter.getModel().getDeviceConfig().getParserName().endsWith("Kamstrup382") ||
                    meter.getModel().getDeviceConfig().getParserName().endsWith("KamstrupOmniPower")) {

                Object[] res = cgw.cmdKamstrupCID(meter.getModem().getMcu().getSysID(), meter.getMdsId(), 
                        new String[] { KamstrupCIDMeta.CID.GetCutOffState.getCommand() }); // GetCutOffState
                log.debug(res);

                if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays connected") > -1) {
                    req.setOrderStatus(OrderStatus.FAILED);
                    throw new ApplicationFaultException(FaultCode.FC_121);
                }

                Thread.sleep(2000);

                if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays disconnected") > -1) {
                    res = cgw.cmdKamstrupCID(meter.getModem().getMcu().getSysID(), meter.getMdsId(), 
                            new String[] { KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
                        KamstrupCIDMeta.CID.SetCutOffState.getArgs()[1][0]}); // SetCutOffState - Reconnect relays possible

                    log.debug(res);
                    Thread.sleep(2000);
                }

                if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays released") > -1) {
                    res = cgw.cmdKamstrupCID(meter.getModem().getMcu().getSysID(), meter.getMdsId(), 
                            new String[] { KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
                        KamstrupCIDMeta.CID.SetCutOffState.getArgs()[2][0]}); // SetCutOffState - Reconnect relays
    
                    log.debug(res);
                }

                if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays connected") > -1) {
                    req.setOrderStatus(OrderStatus.PERFORMED);
                } else if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays connected") < 0) {
                    req.setOrderStatus(OrderStatus.FAILED);
                } else {
                    req.setOrderStatus(OrderStatus.FAILED);
                    if (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatusCode(CommonConstants.MeterStatus.PowerDown))) {
                        throw new ApplicationFaultException(
                                FaultCode.FC_116);
                    } else {
                        throw new ApplicationFaultException(
                                FaultCode.FC_120);
                    }
                }
            } else if(meter.getModel().getDeviceConfig().getParserName().endsWith("SM110")) {

                Hashtable<String, Object> mdata = null;
                Map<String, ?> mapdata = null;

                mdata = cgw.doOnDemand(meter, 1, "admin"); // OnDemand
                log.debug(mdata);

                if (mdata.get("resultStatus") == ResultStatus.SUCCESS) {
                    if (meterType == MeterType.EnergyMeter) {
                        mapdata = (Map<String, String>) mdata.get("ondemandResult");

                        log.debug(mapdata);

                        if(mapdata.get("Relay status").equals("On")) {
                            req.setOrderStatus(OrderStatus.FAILED);
                            throw new ApplicationFaultException(FaultCode.FC_121);
                        }

                        Thread.sleep(2000);

                        if(mapdata.get("Relay status").equals("Off") && mapdata.get("activateStatus").equals("Off")) {
                            mapdata = cgw.cmdRelaySwitchAndActivate(meter.getMcu().getSysID(), meter.getMdsId(), OnDemandOption.WRITE_OPTION_ACTON.getCode()); // Activation on

                            log.debug(mapdata);

                            Thread.sleep(1000);
                        }

                        if(mapdata.get("switchStatus").equals("Off") && mapdata.get("activateStatus").equals("On")) {
                            mapdata = cgw.cmdRelaySwitchAndActivate(meter.getMcu().getSysID(), meter.getMdsId(), OnDemandOption.WRITE_OPTION_RELAYON.getCode()); // Switch on                                

                            log.debug(mapdata);
                        }
                        
                        if(mapdata != null && mapdata.get("switchStatus").equals("On")) {
                            req.setOrderStatus(OrderStatus.PERFORMED);
                        } else if(mapdata != null && mapdata.get("switchStatus").equals("Off")) {
                            req.setOrderStatus(OrderStatus.FAILED);
                        } else {
                            req.setOrderStatus(OrderStatus.FAILED);
                        }
                    }
                } else {
                    req.setOrderStatus(OrderStatus.FAILED);
                    if (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatusCode(CommonConstants.MeterStatus.PowerDown))) {
                        throw new ApplicationFaultException(
                                FaultCode.FC_116);
                    } else {
                        throw new ApplicationFaultException(
                                FaultCode.FC_120);
                    }
                }
            }
		} catch (ApplicationFaultException e) {
			req.setOrderStatus(OrderStatus.FAILED);
			req.setApplicationFault(new ApplicationFault(FaultCode
					.getFaultCode(Integer.parseInt(e.getFaultCode()))));
			log.error(e);
			return e.getMessage();
		} catch (Exception e) {
			req.setOrderStatus(OrderStatus.FAILED);
			log.error(e);
			return e.getMessage();
		}
		return null;
	}

	private String powerDeblocking(Meter meter,
			HandlePowerOnOffRequest req) {
	    CommandBO cgw = DataUtil.getBean(CommandBO.class);
	    MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());

        try {
            if(meter.getModel().getDeviceConfig().getParserName().endsWith("Aidon5530") || 
                meter.getModel().getDeviceConfig().getParserName().endsWith("Aidon5530_2")) {

                // Aidon은 MCCB 상태정보를 알수 없다.
                //String res = cgw.cmdAidonMccb(meter.getModem().getMcu().getSysID(), meter.getMdsId(), Mccb.MSG_REQ[6]); // Disable Use - Disconnect
                //Thread.sleep(1000);
                String res = cgw.cmdAidonMccb(meter.getModem().getMcu().getSysID(), meter.getMdsId(), Mccb.MSG_REQ[3]); // Enable Use - Disconnected
                if(res != null && res.indexOf("OK+") > -1) {
                    req.setOrderStatus(OrderStatus.PERFORMED);
                } if(res != null && res.indexOf("E+") > -1) { 
                    req.setOrderStatus(OrderStatus.FAILED);
                } else {
                    req.setOrderStatus(OrderStatus.FAILED);
                    if (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatusCode(CommonConstants.MeterStatus.PowerDown))) {
                        throw new ApplicationFaultException(
                                FaultCode.FC_116);
                    } else {
                        throw new ApplicationFaultException(
                                FaultCode.FC_120);
                    }
                }
                
            } else if(meter.getModel().getDeviceConfig().getParserName().endsWith("Kamstrup382") ||
                    meter.getModel().getDeviceConfig().getParserName().endsWith("KamstrupOmniPower")) {
        
                Object[] res = cgw.cmdKamstrupCID(meter.getModem().getMcu().getSysID(), meter.getMdsId(), 
                        new String[] { KamstrupCIDMeta.CID.GetCutOffState.getCommand() }); // GetCutOffState
                log.debug(res);

                if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays released") > -1) {
                    req.setOrderStatus(OrderStatus.FAILED);
                    throw new ApplicationFaultException(FaultCode.FC_125);
                }
        
                Thread.sleep(2000);

                if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays disconnected") > -1) {
                    res = cgw.cmdKamstrupCID(meter.getModem().getMcu().getSysID(), meter.getMdsId(), 
                            new String[] { KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
                        KamstrupCIDMeta.CID.SetCutOffState.getArgs()[1][0]}); // SetCutOffState - Reconnect relays possible
        
                    log.debug(res);
                    Thread.sleep(2000);
                }

                if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays released") > -1) {
                    req.setOrderStatus(OrderStatus.PERFORMED);
                } else if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays released") < 0) {
                    req.setOrderStatus(OrderStatus.FAILED);
                } else {
                    req.setOrderStatus(OrderStatus.FAILED);
                    if (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatusCode(CommonConstants.MeterStatus.PowerDown))) {
                        throw new ApplicationFaultException(
                                FaultCode.FC_116);
                    } else {
                        throw new ApplicationFaultException(
                                FaultCode.FC_120);
                    }
                }
            } else if(meter.getModel().getDeviceConfig().getParserName().endsWith("SM110")) {
        
                Hashtable<String, Object> mdata = null;
                Map<String, ?> mapdata = null;
        
                mdata = cgw.doOnDemand(meter, 1, "admin"); // OnDemand
                log.debug(mdata);
        
                if (mdata.get("resultStatus") == ResultStatus.SUCCESS) {
                    if (meterType == MeterType.EnergyMeter) {
                        mapdata = (Map<String, String>) mdata.get("ondemandResult");
        
                        log.debug(mapdata);
        
                        if(mapdata.get("Relay activate status").equals("On")) {
                            req.setOrderStatus(OrderStatus.FAILED);
                            throw new ApplicationFaultException(FaultCode.FC_125);
                        }
        
                        Thread.sleep(2000);
        
                        if(mapdata.get("Relay status").equals("Off") && mapdata.get("activateStatus").equals("Off")) {
                            mapdata = cgw.cmdRelaySwitchAndActivate(meter.getMcu().getSysID(), meter.getMdsId(), OnDemandOption.WRITE_OPTION_ACTON.getCode()); // Activation on
        
                            log.debug(mapdata);
                        }

                        if(mapdata != null && mapdata.get("activateStatus").equals("On")) {
                            req.setOrderStatus(OrderStatus.PERFORMED);
                        } else if(mapdata != null && mapdata.get("activateStatus").equals("Off")) {
                            req.setOrderStatus(OrderStatus.FAILED);
                        } else {
                            req.setOrderStatus(OrderStatus.FAILED);
                        }
                    }
                } else {
                    req.setOrderStatus(OrderStatus.FAILED);
                    if (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatusCode(CommonConstants.MeterStatus.PowerDown))) {
                        throw new ApplicationFaultException(
                                FaultCode.FC_116);
                    } else {
                        throw new ApplicationFaultException(
                                FaultCode.FC_120);
                    }
                }
            }
        } catch (ApplicationFaultException e) {
            req.setOrderStatus(OrderStatus.FAILED);
            req.setApplicationFault(new ApplicationFault(FaultCode
                    .getFaultCode(Integer.parseInt(e.getFaultCode()))));
            log.error(e);
            return e.getMessage();
        } catch (Exception e) {
            req.setOrderStatus(OrderStatus.FAILED);
            log.error(e);
            return e.getMessage();
        }
        return null;
	}

	private String powerOff(Meter meter, HandlePowerOnOffRequest req) {
        CommandBO cgw = DataUtil.getBean(CommandBO.class);
        MeterType meterType = MeterType.valueOf(meter.getMeterType().getName());

        try {

            if(meter.getModel().getDeviceConfig().getParserName().endsWith("Aidon5530") || 
                    meter.getModel().getDeviceConfig().getParserName().endsWith("Aidon5530_2")) {

                // Aidon은 MCCB 상태정보를 알수 없다.
                String res = cgw.cmdAidonMccb(meter.getModem().getMcu().getSysID(), meter.getMdsId(), Mccb.MSG_REQ[6]); // Disable Use - Disconnect
                if(res != null && res.indexOf("OK+") > -1) {
                    req.setOrderStatus(OrderStatus.PERFORMED);
                } if(res != null && res.indexOf("E+") > -1) { 
                    req.setOrderStatus(OrderStatus.FAILED);
                } else {
                    req.setOrderStatus(OrderStatus.FAILED);
                    if (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatusCode(CommonConstants.MeterStatus.PowerDown))) {
                        throw new ApplicationFaultException(
                                FaultCode.FC_116);
                    } else {
                        throw new ApplicationFaultException(
                                FaultCode.FC_120);
                    }
                }

            } else if(meter.getModel().getDeviceConfig().getParserName().endsWith("Kamstrup382") ||
                    meter.getModel().getDeviceConfig().getParserName().endsWith("KamstrupOmniPower")) {

                Object[] res = cgw.cmdKamstrupCID(meter.getModem().getMcu().getSysID(), meter.getMdsId(), 
                        new String[] { KamstrupCIDMeta.CID.GetCutOffState.getCommand() }); // GetCutOffState
                log.debug(res);

                if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays disconnected") > -1) {
                    req.setOrderStatus(OrderStatus.FAILED);
                    throw new ApplicationFaultException(FaultCode.FC_124);
                }

                Thread.sleep(2000);

                res = cgw.cmdKamstrupCID(meter.getModem().getMcu().getSysID(), meter.getMdsId(), 
                        new String[] { KamstrupCIDMeta.CID.SetCutOffState.getCommand(),
                    KamstrupCIDMeta.CID.SetCutOffState.getArgs()[0][0]}); // SetCutOffState - Disconnected relays

                if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays disconnected") > -1) {
                    req.setOrderStatus(OrderStatus.PERFORMED);
                } else if(res!=null && res.length>0 && ((String)res[0]).indexOf("Relays disconnected") < 0) {
                    req.setOrderStatus(OrderStatus.FAILED);
                } else {
                    req.setOrderStatus(OrderStatus.FAILED);
                    if (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatusCode(CommonConstants.MeterStatus.PowerDown))) {
                        throw new ApplicationFaultException(
                                FaultCode.FC_116);
                    } else {
                        throw new ApplicationFaultException(
                                FaultCode.FC_120);
                    }
                }
            } else if(meter.getModel().getDeviceConfig().getParserName().endsWith("SM110")) {

                Hashtable<String, Object> mdata = null;
                Map<String, ?> mapdata = null;

                mdata = cgw.doOnDemand(meter, 1, "admin"); // OnDemand
                log.debug(mdata);

                if (mdata.get("resultStatus") == ResultStatus.SUCCESS) {
                    if (meterType == MeterType.EnergyMeter) {
                        mapdata = (Map<String, String>) mdata.get("ondemandResult");

                        log.debug(mapdata);

                        if(mapdata.get("Relay status").equals("Off")) {
                            req.setOrderStatus(OrderStatus.FAILED);
                            throw new ApplicationFaultException(FaultCode.FC_124);
                        }

                        Thread.sleep(2000);

                        mapdata = cgw.cmdRelaySwitchAndActivate(meter.getMcu().getSysID(), meter.getMdsId(), OnDemandOption.WRITE_OPTION_RELAYOFF.getCode()); // Switch off
                        log.debug(mapdata);
                        
                        if(mapdata != null && mapdata.get("switchStatus").equals("Off")) {
                            req.setOrderStatus(OrderStatus.PERFORMED);
                        } else if(mapdata != null && mapdata.get("switchStatus").equals("On")) {
                            req.setOrderStatus(OrderStatus.FAILED);
                        } else {
                            req.setOrderStatus(OrderStatus.FAILED);
                        }
                    }
                } else {
                    req.setOrderStatus(OrderStatus.FAILED);
                    if (meter.getMeterStatus().getCode().equals(CommonConstants.getMeterStatusCode(CommonConstants.MeterStatus.PowerDown))) {
                        throw new ApplicationFaultException(
                                FaultCode.FC_116);
                    } else {
                        throw new ApplicationFaultException(
                                FaultCode.FC_120);
                    }
                }
            }
        } catch (ApplicationFaultException e) {
            req.setOrderStatus(OrderStatus.FAILED);
            req.setApplicationFault(new ApplicationFault(FaultCode
                    .getFaultCode(Integer.parseInt(e.getFaultCode()))));
            log.error(e);
            return e.getMessage();
        } catch (Exception e) {
            req.setOrderStatus(OrderStatus.FAILED);
            log.error(e);
            return e.getMessage();
        }
        return null;
	}

	private HandlePowerOnOffResponse sendingCallbackMessage(String userName,
			HandlePowerOnOffRequest req) throws Exception {
		boolean isSend = Boolean.parseBoolean(FMPProperty.getProperty(
				"interface.PowerOnOffCallback.isSend", "false"));
		if (!isSend) {
			throw new Exception("Currently AIMIR can't send handling data");
		}
		HandlePowerOnOffResponse res = sendingCallbackMessageJBossWSCXF(userName, req);
		return res;
	}

	private HandlePowerOnOffResponse sendingCallbackMessageJBossWSCXF(
			String userName, HandlePowerOnOffRequest req)
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

	private void updateInProgressReadingOrder(PowerOnOffOrder ro) {
		poooDao.updatePowerOnOffOrder(ro, new String[] { "orderStatus" },
				new String[] { "" + ro.getOrderStatus().intValue() });
	}

	private void updateFinishPowerOnOffOrder(PowerOnOffOrder ro,
			boolean isSend, HandlePowerOnOffRequest req,
			HandlePowerOnOffResponse res, String failMessage) {
		String handleDate = null;
		if (res != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			handleDate = sdf.format(res.getHandledDate().toGregorianCalendar()
					.getTime());
		}
		updateFinishPowerOnOffOrder(ro, isSend, req, handleDate, failMessage);

	}

	private void updateFinishPowerOnOffOrder(PowerOnOffOrder ro,
			boolean isSend, HandlePowerOnOffRequest req, String handleDate,
			String failMessage) {
		ro.setOrderStatus((int) req.getOrderStatus().getValue());
		ro.setSend(isSend);
		if (handleDate != null && !handleDate.equals("")) {
			ro.setHandleDate(handleDate);
		}
		if (ro.getPowerOperationDate() == null
				&& req.getPowerOperationDate() != null) {
			String powerOperationDate = null;
			if (req != null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				powerOperationDate = sdf.format(req.getPowerOperationDate()
						.toGregorianCalendar().getTime());
			}
			ro.setPowerOperationDate(powerOperationDate);
		} else if (ro.getPowerOperationDate() == null
				&& req.getPowerOperationDate() == null && handleDate != null) {
			ro.setPowerOperationDate(handleDate);
		}
		if (req.getMeterValue() != null && req.getMeterValue().size() > 0
				&& req.getMeterValue().get(0).getMeterReading() != null) {
			ro.setMeterReading(req.getMeterValue().get(0).getMeterReading()
					.doubleValue());
		}
		if (req.getApplicationFault() != null) {
			ro.setApplicationFault(Integer.parseInt(req.getApplicationFault()
					.getFaultCode()));
		}
		if (failMessage != null) {
			ro.setFailMessage(failMessage.substring(0,
					(failMessage.length() > 1024 ? 1023 : failMessage.length())));
		}
        poooDao.updatePowerOnOffOrder(
                ro,
                new String[] { "orderstatus", "isSend", "handleDate",
                        "powerOperationDate", "meterReading",
                        "applicationFault", "failMessage" },
                new Object[] { ro.getOrderStatus(), ro.isSendInt(),
                        ro.getHandleDate(), ro.getPowerOperationDate(),
                        ro.getMeterReading(), ro.getApplicationFault(),
                        ro.getFailMessage() });

	}
}