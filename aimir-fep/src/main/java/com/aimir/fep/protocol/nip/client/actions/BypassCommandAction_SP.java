/**
 * (@)# BypassCommandAction_SP.java
 *
 * 2016. 6. 1.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.protocol.nip.client.actions;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.MeterEventKind;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.MeterEventLogDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.bypass.BypassDevice;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForSORIA.ActionResult;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameResult;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassSORIAFactory;
import com.aimir.fep.bypass.dlms.enums.ObjectType;
import com.aimir.fep.meter.data.LPData;
import com.aimir.fep.meter.data.MeterData;
import com.aimir.fep.meter.parser.DLMSKaifa;
import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.METER_EVENT_LOG;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.OBIS;
import com.aimir.fep.meter.parser.DLMSKaifaTable.DLMSVARIABLE.RELAY_STATUS_KAIFA; // INSERT 2016/09/20 SP-117
import com.aimir.fep.meter.saver.DLMSKaifaMDSaver;
import com.aimir.fep.protocol.nip.client.bypass.BypassResult;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.trap.actions.SP.EV_SP_200_65_0_Action;
import com.aimir.fep.trap.actions.SP.EV_SP_200_66_0_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DLMSCmdUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Meter;
import com.aimir.model.device.MeterEventLog;
import com.aimir.model.device.Modem;
import com.aimir.model.system.DeviceConfig;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.MeterConfig;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

import net.sf.json.JSONObject;

/**
 * @author simhanger
 *
 */
public class BypassCommandAction_SP extends BypassCommandAction {
	private static Logger logger = LoggerFactory.getLogger(BypassCommandAction_SP.class);	
	private BypassResult bypassResult;
	private boolean isBypassFinish;
    private int responseTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.bypass.response.timeout", "180"));
    private Object resMonitor = new Object();
    // INSERT START SP-628
    private int handshakeTimeout = Integer.parseInt(FMPProperty.getProperty("protocol.bypass.handshake.timeout", "20"));
    private int handshakeRetry = Integer.parseInt(FMPProperty.getProperty("protocol.bypass.handshake.retry", "2"));
    // INSERT END SP-628
    
	@Override
	public void execute(MultiSession session, String command) throws Exception {
		/*
		 * SORIA Bypass Command List
		 * 
		 * 1.  cmdMeterOTAStart
		 * 2.  cmdMeterParamGet
		 * 3.  cmdMeterParamSet
		 * 4.  cmdGetMeterFWVersion
		 * 5.  cmdSORIAGetMeterKey
		 * 6.
		 * 7.
		 * 8.
		 * 
		 */
		logger.debug("#### EXCUTE CommandName => {}", command);
		logger.debug("#### EXCUTE CommandName => {}", command);
		logger.debug("#### EXCUTE CommandName => {}", command);
		
		Method method = this.getClass().getMethod(command, MultiSession.class);
		method.invoke(this, session);
	}
	
	/**
	 * 1. cmdMeterOTAStart
	 * 
	 * @param session
	 * @throws Exception
	 */
	public void cmdMeterOTAStart(MultiSession session) throws Exception {
		long startTime = System.currentTimeMillis();

		/*
		 * 1. F/W Image 준비
		 */
		BypassDevice bd = session.getBypassDevice();
		bd.setStartOTATime(startTime);
		bd.setCommand("cmdMeterOTAStart");
		
		logger.debug("## [MeterId={}][cmdMeterOTAStart] Action Start - {}", bd.getMeterId(), DateTimeUtil.getDateString(startTime));
		
		boolean takeover = false;
		if (bd.getArgMap().containsKey("take_over")) {
			try {
				takeover = Boolean.parseBoolean((String) bd.getArgMap().get("take_over"));
			} catch (Exception e) {
			}
		}

		BypassFrameFactory bfFactory = new BypassSORIAFactory(bd.getMeterId(), "cmdMeterOTAStart");
		bfFactory.setParam(bd.getArgMap());
		bd.setFrameFactory(bfFactory);
		bfFactory.start(session, null);
	}

	/**
	 * 2. cmdMeterParamGet
	 * 
	 * @param session
	 * @throws Exception
	 */
	public void cmdMeterParamGet(MultiSession session) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.debug("## [cmdMeterParamGet] Action : " + DateTimeUtil.getDateString(startTime));

		/*
		 * 1. Parameter 준비
		 */
		BypassDevice bd = session.getBypassDevice();
		String param = (String) bd.getArgMap().get("paramGet");
		String option = (String) bd.getArgMap().get("option");
		
		logger.debug("paramGet={}, option={}", param, option);
		
		String[] data = null;
		//obisCode|classId|attributeNo|accessRight|dataType|value
		//value는 ,로 구분한다.
		data = param.split("[|]");

		HashMap<String, Object> argMap = new HashMap<String, Object>();
		argMap.put("timeout", "60");      // 패킷한번 주고 받을때 60초동안 주고받는 패킷이 없을경우 disconnect
		argMap.put("obisCode", data[0]);
		argMap.put("classId", data[1]);
		argMap.put("attributeNo", data[2]);
		if ( option != null ) argMap.put("option", option);
		bd.setArgMap(argMap);

		String cmd = DLMSCmdUtil.getCmdName(Integer.parseInt(data[1]), data[2], "Get");
		if ( cmd.equals("cmdGetProfileBuffer") && "ondemand".equals(option)){
			cmd = "cmdGetLoadProfileOnDemand";
		}
		else if ( cmd.equals("cmdGetProfileBuffer") && "ondemandmbb".equals(option)){
			cmd = "cmdGetLoadProfileOnDemandMbb";
		}
		// -> UPDATE START 2016/08/24 SP117
		else if( "relaystatusall".equals(option)){
			cmd = "cmdGetRelayStatusAll";
		}
		// <- UPDATE END   2016/08/24 SP117
		bd.setCommand(cmd);
		/*
		 * 2. HDLC Connction.
		 */
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("obisCode", data[0]);
		params.put("classId", data[1]);
		params.put("attributeNo", data[2]);

		if (data.length >= 6 && data[5] != null && !data[5].isEmpty()) {
			params.put("value", data[5]);
		}
		// UPDATE START SP-519
		BypassFrameFactory bfFactory = null;
		params.put("target", (String)session.getAttribute("target")); 
		if ( "true".equals(session.getAttribute("UseNiBypass"))){
			bfFactory = new BypassSORIAFactory(bd.getMeterId(), cmd, true);
		}
		else {
			bfFactory = new BypassSORIAFactory(bd.getMeterId(), cmd);
		}
		// UPDATE START SP-519
		bfFactory.setParam(params);
		bd.setFrameFactory(bfFactory);
		bfFactory.start(session, null);
	}

	/**
	 * 3. cmdMeterParamSet
	 * 
	 * @param session
	 * @throws Exception
	 */
	public void cmdMeterParamSet(MultiSession session) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.debug("## [cmdMeterParamSet] Action : " + DateTimeUtil.getDateString(startTime));

		BypassDevice bd = session.getBypassDevice();
		String param = (String) bd.getArgMap().get("paramSet");
		String option = (String) bd.getArgMap().get("option");

		String[] data = null;
		//obisCode|classId|attributeNo|accessRip/ght|dataType|value
		//value는 ,로 구분한다.
		logger.debug("param=" + param);
		data = param.split("[|]");

		HashMap<String, Object> argMap = new HashMap<String, Object>();
		argMap.put("timeout", "60");
		if ( option != null ) argMap.put("option", option);
//		bd.setArgMap(argMap);

		String cmd = DLMSCmdUtil.getCmdName(Integer.parseInt(data[1]), data[2], "Set");
		bd.setCommand(cmd);

		/*
		 * 2. HDLC Connction.
		 */
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("obisCode", data[0]);
		params.put("classId", data[1]);
		params.put("attributeNo", data[2]);
		params.put("dataType", data[4]);

		if ( option != null && "synctime".equals(option)){
			logger.debug("synctime option. decide time.");
			int travelTime = 0;
			if (data[5] != null) {
				travelTime = Integer.parseInt(data[5]);
			}
            String current = DateTimeUtil.getDateString(new Date());
            String afterTime = DateTimeUtil.getDateString(TimeUtil.getLongTime(current) + (travelTime*1000));            
    		Calendar afterCal = DateTimeUtil.getCalendar(afterTime);

            logger.debug("newTime[" + afterTime + "] travel[" + travelTime + "]");
            JSONObject json = new JSONObject();
            json.put("year", afterTime.substring(0,4));
            json.put("month", afterTime.substring(4,6));
            json.put("dayOfMonth", afterTime.substring(6,8));
            json.put("dayOfWeek", String.valueOf(afterCal.get(Calendar.DAY_OF_WEEK)));
            json.put("hh", afterTime.substring(8,10));
            json.put("mm", afterTime.substring(10,12));
            json.put("ss", afterTime.substring(12,14));
            json.put("daylight", 0);
            json.put("pcTime", "false");
            
			String value = "["+ json.toString() + "]";                    
			params.put("value", value);
			argMap.put("aftertime", afterTime);
		}		
		else {
			params.put("value", data[5]);			
		}

		bd.setArgMap(argMap);

		BypassFrameFactory bfFactory = new BypassSORIAFactory(bd.getMeterId(), cmd);
		bfFactory.setParam(params);
		bd.setFrameFactory(bfFactory);
		bfFactory.start(session, null);
	}

	/*
	 * 4. Meter FW Version 갱신
	 */
	public void cmdGetMeterFWVersion(MultiSession session) throws Exception {
		BypassDevice bd = session.getBypassDevice();
		bd.setCommand("cmdGetMeterFWVersion");
		
		logger.debug("## [MeterId={}][cmdGetMeterFWVersion] Action ##", bd.getMeterId());

		HashMap<String, Object> argMap = new HashMap<String, Object>();
		argMap.put("timeout", "60");
		bd.setArgMap(argMap);

		/*
		 * 2. HDLC Connction.
		 */
		BypassFrameFactory sdFactory = null;
		if(session.getAttribute("UseNiBypass") != null && session.getAttribute("UseNiBypass").equals("true")){
			sdFactory = new BypassSORIAFactory(bd.getMeterId(), "cmdGetMeterFWVersion", true);
		}else{
			sdFactory = new BypassSORIAFactory(bd.getMeterId(), "cmdGetMeterFWVersion");
		}
		
		bd.setFrameFactory(sdFactory);
		sdFactory.start(session, null);
	}
	
	/*
	 * 5. Get Meter Key 
	 */
	public void cmdSORIAGetMeterKey(MultiSession session) throws Exception {
		BypassDevice bd = session.getBypassDevice();
		bd.setCommand("cmdSORIAGetMeterKey");
		
		logger.debug("## [MeterId={}][cmdSORIAGetMeterKey] Action ##", bd.getMeterId());

		HashMap<String, Object> argMap = new HashMap<String, Object>();
		argMap.put("timeout", "60");
		bd.setArgMap(argMap);

		/*
		 * 2. HDLC Connction.
		 */
		BypassFrameFactory sdFactory = null;
		if(session.getAttribute("UseNiBypass") != null && session.getAttribute("UseNiBypass").equals("true")){
			sdFactory = new BypassSORIAFactory(bd.getMeterId(), "cmdSORIAGetMeterKey", true);
		}else{
			sdFactory = new BypassSORIAFactory(bd.getMeterId(), "cmdSORIAGetMeterKey", false);
		}
		
		bd.setFrameFactory(sdFactory);
		sdFactory.start(session, null);
	}

	/**
	 *  cmdMeterParamAct
	 * 
	 * @param session
	 * @throws Exception
	 */
	public void cmdMeterParamAct(MultiSession session) throws Exception {
		long startTime = System.currentTimeMillis();
		logger.debug("## [cmdMeterParamAct] Action : " + DateTimeUtil.getDateString(startTime));

		BypassDevice bd = session.getBypassDevice();
		String param = (String) bd.getArgMap().get("paramAct");

		String[] data = null;
		//obisCode|classId|attributeNo|accessRip/ght|dataType|value
		//value는 ,로 구분한다.
		data = param.split("[|]");

		HashMap<String, Object> argMap = new HashMap<String, Object>();
		argMap.put("timeout", "60");
		bd.setArgMap(argMap);

		String cmd = DLMSCmdUtil.getCmdName(Integer.parseInt(data[1]), data[2], "Act");
		bd.setCommand(cmd);

		/*
		 * 2. HDLC Connction.
		 */
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("obisCode", data[0]);
		params.put("classId", data[1]);
		params.put("attributeNo", data[2]);
		params.put("dataType", data[4]);
		params.put("value", data[5]);

		BypassFrameFactory bfFactory = new BypassSORIAFactory(bd.getMeterId(), cmd);
		bfFactory.setParam(params);
		bd.setFrameFactory(bfFactory);
		bfFactory.start(session, null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void executeBypass(MultiSession session, byte[] rawFrame) throws Exception {
		BypassDevice bd = session.getBypassDevice();
		
		logger.debug("#### [Command={}][METER_ID={}] Receive Bypass Frame ==> {}", bd.getCommand(), bd.getMeterId(), Hex.decode(rawFrame));
		BypassFrameResult bypassFrameResult = bd.getFrameFactory().receiveBypass(session, rawFrame);
		
		if (bypassFrameResult == null) return;

		logger.debug("### [" + bd.getCommand() + "] Execute Bypass ==> " + bypassFrameResult.toString());

		/*
		 * Last Procedure.
		 */
		if(bypassFrameResult.getLastProcedure() == null && bypassFrameResult.getStep() == Procedure.HDLC_DISC){
			logger.debug("##### Bypass Transaction Finished. bye ~ #####");
			logger.debug("##### Bypass Transaction Finished. bye ~ #####");
			logger.debug("##### Bypass Transaction Finished. bye ~ #####");
			return;
		}
		
		if (bd.getCommand().equals("cmdMeterOTAStart")) {
			try {
				// 실패시는 모두 저장
				if (!bypassFrameResult.isResultState()) {
					BypassResult bypassResult = new BypassResult();
					bypassResult.setCommnad(bd.getCommand());
					bypassResult.setSuccess(false);
					
					//### [cmdMeterOTAStart] Execute Bypass ==> BypassFrameResult[resultState=false,finished=false,lastProcedure=<null>,step=ACTION_IMAGE_BLOCK_TRANSFER,resultValueMap={Progress Rate=92.47%, Meter Message=OTHER_REASON},resultValue=<null>]
							
							
					bypassResult.setResultValue(bypassFrameResult.getLastProcedure() == null ? bypassFrameResult.getStep().name() : bypassFrameResult.getLastProcedure().name());
					
					/*
					 * OTA 종료후 Event 저장
					 */
					String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
					EV_SP_200_65_0_Action action = new EV_SP_200_65_0_Action();
					action.makeEvent(TargetClass.EnergyMeter
							, bd.getMeterId()
							, TargetClass.EnergyMeter
							, openTime
							, "0"
							, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_TRN_FAIL
							//, bypassResult.getResultValue().toString() + (bypassFrameResult.getLastProcedure() == Procedure.ACTION_IMAGE_ACTIVATE ? bypassFrameResult.getResultValue().toString() : "")
							, bypassResult.getResultValue().toString() + bypassFrameResult.getValueMap() == null ? "" : bypassFrameResult.getValueMap().toString()
							, "HES");
					action.updateOTAHistory(
							  bd.getMeterId()
							, DeviceType.Meter
							, openTime
							, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_TRN_FAIL
							, bypassResult.getResultValue().toString() + bypassFrameResult.getValueMap() == null ? "" : bypassFrameResult.getValueMap().toString());
					
					EV_SP_200_66_0_Action action2 = new EV_SP_200_66_0_Action();
					action2.makeEvent(TargetClass.EnergyMeter
							, bd.getMeterId()
							, TargetClass.EnergyMeter
							, openTime
							, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_TRN_FAIL, null
							, "HES");
					action2.updateOTAHistory(bd.getMeterId(), DeviceType.Meter, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_TRN_FAIL);
					
					logger.debug("[{}][{}] OTA Fail Result = {}", bd.getCommand(), bd.getMeterId(), bypassResult.toString());
				} else {
					try {
						if (bypassFrameResult.getLastProcedure() == Procedure.ACTION_IMAGE_ACTIVATE) {
							long endTime = System.currentTimeMillis();

							Map<String, Object> resultParam = new HashMap<String, Object>();
							resultParam.put("RESULT_STATUS", bypassFrameResult.getLastProcedure().name());
							resultParam.put("RESULT_VALUE", bypassFrameResult.getResultValue());
							resultParam.put("RESULT_OTA_START", DateTimeUtil.getDateString(bd.getStartOTATime()));
							resultParam.put("RESULT_OTA_END", DateTimeUtil.getDateString(endTime));
							resultParam.put("RESULT_OTA_ELAPSE", DateTimeUtil.getElapseTimeToString((endTime - bd.getStartOTATime())));

							bypassResult = new BypassResult();
							bypassResult.setCommnad(bd.getCommand());
							bypassResult.setSuccess(true);
							bypassResult.setResultValue(resultParam);
							
							/*
							 * OTA 종료후 Event 저장
							 */
							String fwVersion = bd.getArgMap().containsKey("fw_version") == true ? (String) bd.getArgMap().get("fw_version") : null;
							String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
							EV_SP_200_65_0_Action action1 = new EV_SP_200_65_0_Action();
							action1.makeEvent(TargetClass.EnergyMeter
									, bd.getMeterId()
									, TargetClass.EnergyMeter
									, openTime
									, resultParam.get("RESULT_OTA_ELAPSE").toString()
									, OTA_UPGRADE_RESULT_CODE.OTAERR_NOERROR
									, resultParam.get("RESULT_VALUE").toString() + (fwVersion != null ? ", F/W Version=[" + fwVersion + "]" : "")
									, "HES");
							// UPDATE START SP-707
							action1.updateOTAHistory2(
									  bd.getMeterId()
									, DeviceType.Meter
									, openTime
									, OTA_UPGRADE_RESULT_CODE.OTAERR_NOERROR
									, resultParam.get("RESULT_VALUE").toString() + (fwVersion != null ? ", F/W Version=[" + fwVersion + "]" : "")
									, (fwVersion != null ? fwVersion : ""));
							// UPDATE END SP-707
							
							EV_SP_200_66_0_Action action2 = new EV_SP_200_66_0_Action();
							action2.makeEvent(TargetClass.EnergyMeter
									, bd.getMeterId()
									, TargetClass.EnergyMeter
									, openTime
									, OTA_UPGRADE_RESULT_CODE.OTAERR_NOERROR
									, null
									, "HES");
							action2.updateOTAHistory(bd.getMeterId(), DeviceType.Meter, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_NOERROR);
							logger.debug("[{}][{}] OTA Success Result = {}", bd.getCommand(), bd.getMeterId(), resultParam.toString());
							
							
							/**
							 * Meter F/W 버전 가져오기 !!
							 */
							logger.debug("Meter OTA Finished.. ");
							//Thread.sleep(10000);
							//logger.debug("Meter OTA Finished and Meter F/W Version Check Start...");
							//cmdGetMeterFWVersion(session);
						}
					} catch (Exception e) {
						logger.error("Procedure save error - {}", e);
					}
				}
			} catch (Exception e) {
				BypassResult bypassResult = new BypassResult();
				bypassResult.setCommnad(bd.getCommand());
				bypassResult.setSuccess(false);
				bypassResult.setResultValue(bypassFrameResult.getLastProcedure() == null ? bypassFrameResult.getStep().name() : bypassFrameResult.getLastProcedure().name());
				
				/*
				 * OTA ERROR시 Event 저장
				 */
				String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
				EV_SP_200_65_0_Action action = new EV_SP_200_65_0_Action();
				action.makeEvent(TargetClass.EnergyMeter
						, bd.getMeterId()
						, TargetClass.EnergyMeter
						, openTime
						, "0"
						, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_EXCEPTION_FAILE
						, bypassResult.getResultValue().toString()
						, "HES");
				action.updateOTAHistory(bd.getMeterId(), DeviceType.Meter, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_EXCEPTION_FAILE, bypassResult.getResultValue().toString());
				
				EV_SP_200_66_0_Action action2 = new EV_SP_200_66_0_Action();
				action2.makeEvent(TargetClass.EnergyMeter, bd.getMeterId(), TargetClass.EnergyMeter, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_BYPASS_EXCEPTION_FAILE, null, "HES");
        		
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdGetMeterFWVersion")) {
			try {
				String fwVersion = (String) bypassFrameResult.getResultValue();

				// 실패시는 모두 저장
				if (!bypassFrameResult.isResultState()) {
					BypassResult bypassResult = new BypassResult();
					bypassResult.setCommnad(bd.getCommand());
					bypassResult.setSuccess(false);
					bypassResult.setResultValue(bypassFrameResult.getLastProcedure() == null ? bypassFrameResult.getStep().name() : bypassFrameResult.getLastProcedure().name());

					logger.debug("Fail Result => " + bypassResult.toJson());
				} else {
					try {
						logger.debug("#### bypassResult step1 ~!!!!! ==> {}", bypassResult == null ? "NULL ^^" : bypassResult.toString());
						
						//						if (bypassFrameResult.isFinished() == true && bypassFrameResult.getLastProcedure() != null 
						//								&& bypassFrameResult.getLastProcedure() == Procedure.GET_FIRMWARE_VERSION) {
						if (bypassFrameResult.isFinished() == true && bypassFrameResult.getLastProcedure() == Procedure.GET_FIRMWARE_VERSION) {

							Map<String, Object> resultParam = new HashMap<String, Object>();
							resultParam.put("RESULT_STATUS", bypassFrameResult.getLastProcedure().name());
							if (bypassFrameResult.getResultValue() != null) {
								resultParam.put("result", true);
								resultParam.put("resultValue", fwVersion);
							} else {
								resultParam.put("result", false);
								resultParam.put("resultValue", "---");
							}

							bypassResult = new BypassResult();
							bypassResult.setCommnad(bd.getCommand());
							bypassResult.setSuccess(true);
							bypassResult.setResultValue(resultParam);
							
							logger.debug("#### bypassResult step2 ~!!!!! ==> {}", bypassResult == null ? "NULL ㅠㅠ" : bypassResult.toString());

							/*
							 * 미터 FW정보 UPDATE
							 */
							ModemDao modemDao = DataUtil.getBean(ModemDao.class);
							Modem modem = modemDao.get(bd.getModemId());
							Set<Meter> meters = modem.getMeter();
							Meter meter = meters.iterator().next();
							meter.setSwVersion(fwVersion);
							
							MeterDao meterDao = DataUtil.getBean(MeterDao.class);
							meterDao.update(meter);

							logger.debug("[{}] BypassResult = {}", bd.getCommand(), bypassResult.toString());
						}
					} catch (Exception e) {
						logger.error("Procedure save error - {}", e);
					}
				}
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		}else if (bd.getCommand().equals("cmdSORIAGetMeterKey")) {   
			try {
				if (!bypassFrameResult.isResultState()) {
					BypassResult bypassResult = new BypassResult();
					bypassResult.setCommnad(bd.getCommand());
					bypassResult.setSuccess(false);
					bypassResult.setResultValue(bypassFrameResult.getLastProcedure() == null ? bypassFrameResult.getStep().name() : bypassFrameResult.getLastProcedure().name());

					logger.debug("Fail Result => " + bypassResult.toJson());
				} else {
					try {
						if (bypassFrameResult.isFinished() == true && bypassFrameResult.getLastProcedure() == Procedure.GET_SORIA_METER_KEY_C) {
							Map<String, Object> resultParam = (Map<String, Object>) session.getAttribute(bd.getCommand());
							logger.debug("111111111111");
							getKaifaCustomGetMeterKeyString(bypassFrameResult, resultParam);
							
							
							
							Map<String, Object> resultParams = new HashMap<String, Object>();
							resultParams.put("RESULT_STATUS", bypassFrameResult.getLastProcedure().name());
							if (0 < bypassFrameResult.getValueSize()) {
								resultParams.put("result", true);
								
 								String temp = Procedure.GET_SORIA_METER_KEY_A.name() + "=" + resultParam.get(Procedure.GET_SORIA_METER_KEY_A.name()) 
 										+ ", " + Procedure.GET_SORIA_METER_KEY_B.name() + "=" + resultParam.get(Procedure.GET_SORIA_METER_KEY_B.name())
 										+ ", " + Procedure.GET_SORIA_METER_KEY_C.name() + "=" +resultParam.get(Procedure.GET_SORIA_METER_KEY_C.name());
								resultParams.put("resultValue", temp);
							} else {
								resultParams.put("result", false);
								resultParams.put("resultValue", "---");
							}

							
							
							bypassResult = new BypassResult();
							bypassResult.setCommnad(bd.getCommand());
							bypassResult.setSuccess(true);
							bypassResult.setResultValue(resultParams);

							logger.debug("222222 : " + bypassResult.getCommnad());
							
							logger.debug("#### bypassResult step ~!!!!! ==> {}", bypassResult == null ? "NULL ㅠㅠ" : bypassResult.toString());
							logger.debug("[{}] BypassResult = {}", bd.getCommand(), bypassResult.toString());
						}else{
							if (session.getAttribute(bd.getCommand()) == null) {
								Map<String, Object> resultParam = new HashMap<String, Object>();
								getKaifaCustomGetMeterKeyString(bypassFrameResult, resultParam);
								session.setAttribute(bd.getCommand(), resultParam);
							} else {
								Map<String, Object> resultParam = (Map<String, Object>) session.getAttribute(bd.getCommand());
								getKaifaCustomGetMeterKeyString(bypassFrameResult, resultParam);
								session.setAttribute(bd.getCommand(), resultParam);
							}
						}
					} catch (Exception e) {
						logger.error("Procedure save error - {}", e);
					}
				}
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdSetDLMSMeterTime")) {
			logger.info("Received cmdSetDLMSMeterTime");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.SET_METER_TIME);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				
				String option = bd.getArgMap().containsKey("option") == true ? (String) bd.getArgMap().get("option") : null;
				if ( option != null && "synctime".equals(option)){
					String aftertime = bd.getArgMap().containsKey("aftertime") == true ? (String) bd.getArgMap().get("aftertime") : null;
					resultParam.put("aftertime", aftertime);
				}
				
				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());

			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdGetDLMSMeterTime")) {
			logger.info("Received cmdGetDLMSMeterTime");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_METER_TIME);

				HashMap<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				HashMap<String, Object> resultMap = bypassFrameResult.getResultValue() == null ? null : (HashMap<String, Object>) bypassFrameResult.getResultValue();

				if (resultMap != null && bypassFrameResult.getResultValue() != null) {
					resultParam.put("Date(YYYY/MM/DD)", resultMap.get("date"));
					resultParam.put("time(HH:MM:SS)", resultMap.get("time"));
					bypassResult.setResultValue(resultParam);
				}

				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());

			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdGetRegisterValue")) {
			logger.info("Received cmdGetRegisterValue");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_REGISTER_VALUE);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();

				if (resultParam != null && bypassFrameResult.getResultValue() != null) {
					resultParam.put("value", String.valueOf(bypassFrameResult.getResultValue()));
					bypassResult.setResultValue(resultParam);
				}

				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdSetRegisterValue")) {
			logger.info("Received cmdSetRegisterValue");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.SET_REGISTER_VALUE);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());

			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdGetRegisterUnit")) {
			logger.info("Received cmdGetRegisterUnit");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_REGISTER_UNIT);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				HashMap<String, Object> resultMap = bypassFrameResult.getResultValue() == null ? null : (HashMap<String, Object>) bypassFrameResult.getResultValue();

				if (resultParam != null && bypassFrameResult.getResultValue() != null) {
					if (bypassResult.isSuccess()) {
						resultParam.put("Scaler/Unit", String.valueOf(resultMap.get("scaler")) + "/" + DLMSVARIABLE.getUnit((int) resultMap.get("unit")).toString());
						bypassResult.setResultValue(resultParam);
					} else {
						if (resultMap.get("scaler") != null) {
							resultParam.put("Scaler", resultMap.get("scaler"));
						}
						if (resultMap.get("unit") != null) {
							resultParam.put("Unit", resultMap.get("unit"));
						}
						bypassResult.setResultValue(resultParam);
					}
				}

				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());

			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdSetRegisterUnit")) {
			logger.info("Received cmdSetRegisterUnit");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.SET_REGISTER_UNIT);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());

			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdGetProfileBuffer")) {
			logger.info("Received cmdGetProfileBuffer");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_PROFILE_BUFFER);
				if (bypassFrameResult.isFinished() == true && bypassFrameResult.getLastProcedure() == Procedure.GET_PROFILE_BUFFER) {

					HashMap<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
					logger.debug("###### [ModemId={}] cmdGetProfileBuffer resultParam ==> {}", bd.getModemId(), resultParam.toString());
					
					if (resultParam != null && 0 < bypassFrameResult.getValueSize()) {
						/*
						 * resultList 구성 형식
						 * resultList.get(0) : channel 정보 _{classId : 71(Limiter), attributeNo : 3(capture_object)}
						 * resultList.get(x) : channel 에 따른 data 정보 _{classId : 71(Limiter), attributeNo : 2(buffer)}
						 * 					   resultList.get(0)의 channel 순서대로 데이터가 정렬되어 올라옴.
						 * 
						 * ex) resultList.get(0) = {(classId=8,attribute=2,obisCode=0000010000FF),(classId=1,attribute=2,obisCode=0000600B00FF)}
						 *     resultList.get(1) = {20160602070000,1} //Clock,eventCode
						 *     resultList.get(2) = {20160602073000,1}
						 * 	   resultList.get(3) = {20160602073200,2}
						 * 
						 */
						String obisCode = (String) bd.getArgMap().get("obisCode");
						if (obisCode.matches("\\d+\\.\\d+\\.99\\.98\\.\\d+\\.255") || obisCode.equals("1.0.99.97.0.255")) {
							logger.info("#### Event Profile ####");

							List<Object> resultList = (List<Object>) bypassFrameResult.getResultValue("listData");
							resultParam.put("DATA_SIZE",resultList.size());
							List<HashMap<String, Object>> channelList = (List<HashMap<String, Object>>) bypassFrameResult.getResultValue("channelData");
							
							MeterEventLogDao meterEventLogDao = DataUtil.getBean(MeterEventLogDao.class);
							
							HashMap<String, Object> resultMap = bypassFrameResult.getResultValue() == null ? null : (HashMap<String, Object>) bypassFrameResult.getResultValue();

							//resultList의 첫번째줄은 channel정보(capture_object)
							//List<HashMap<String, Object>> channelList = (List<HashMap<String, Object>>) resultList.get(0);

							logger.debug("######### cmdGetProfileBuffer resultList size ===> {}", resultList.size());

							logger.info("#### Event Profile ####");
							//obis Code 가 x.x.x.98.x.ff 인 경우 Event Log (DLMS 표준)
							ModemDao modemDao = DataUtil.getBean(ModemDao.class);
							Modem modem = modemDao.get(bd.getModemId());
							Set<Meter> meters = modem.getMeter();
							Meter meter = meters.iterator().next();
						
							DeviceModel model = meter.getModel();
							SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
							for (int i = 0; i < resultList.size(); i++) {
								try {
									String value = "";
									MeterEventLog meterLog = new MeterEventLog();
									HashMap<String,Object> recData = (HashMap<String, Object>) resultList.get(i);
									StringBuffer messageSb = new StringBuffer();
									boolean isEventCode = false;
									int valueCnt = 0;
									for (int j = 0; j < channelList.size(); j++) {
										
										HashMap<String, Object> tempChannel = channelList.get(j);
										String chlClassId = String.valueOf(tempChannel.get("classId"));
										String chlAttributeNo = String.valueOf(tempChannel.get("attribute"));
										String chlObisCode = String.valueOf(tempChannel.get("obisCode"));
										
										if (ObjectType.CLOCK.getValue() == Integer.parseInt(chlClassId) && 2 == Integer.parseInt(chlAttributeNo)) {
											SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
											String yyyymmddhhmmss = String.valueOf(recData.get("yyyymmddhhmmss"));
											meterLog.setOpenTime(yyyymmddhhmmss);
											meterLog.setYyyymmdd(yyyymmddhhmmss.substring(0, 8));
											resultParam.put("["+(i+1)+"] Event Time",formatter.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(meterLog.getOpenTime())));
										} else if (ObjectType.DATA.getValue() == Integer.parseInt(chlClassId) && 2 == Integer.parseInt(chlAttributeNo)) {
											//eventCode 형식. 0.x.96.11.x.255 (DLMS 표준)
											if (chlObisCode.startsWith("0000600B") && chlObisCode.endsWith("FF")) {
												int eventCode = Integer.parseInt(String.valueOf(recData.get("eventCode")));
												String kind = MeterEventKind.STE.name();
												
												for (METER_EVENT_LOG ev : METER_EVENT_LOG.values()) {
													if(ev.getFlag() == eventCode) {
													 	meterLog.setMessage(ev.getMsg());
													 	meterLog.setMeterEventId(eventCode + "." + kind + "." + model.getDeviceVendor().getName() + "." + model.getName() + "." + obisCode);
													 	isEventCode = true;
												 	}
												 }
												resultParam.put("["+(i+1)+"]"+" Event Message",meterLog.getMessage());
											}
										} else {
											if (ObjectType.LIMITER.getValue() == Integer.parseInt(chlClassId) && 3 == Integer.parseInt(chlAttributeNo)) {
												value = recData.get("thresholdActive") == null ? null : String.valueOf(recData.get("thresholdActive"));
												resultParam.put("["+(i+1)+"]"+"Threshold_active",value);
												if(!isEventCode) {
													messageSb.append("Threshold_active["+value+"]");
												}
												
											} else if (chlObisCode.equals("0000600713FF") && ObjectType.REGISTER.getValue() == Integer.parseInt(chlClassId) && 2 == Integer.parseInt(chlAttributeNo)) {
												value = recData.get("value"+j) == null ? null : String.valueOf(recData.get("value"+j));
												resultParam.put("["+(i+1)+"]"+ "Duration Time",value);
												if(!isEventCode) {
													messageSb.append("Duration Time["+value+"]");
												}
												
											} else {
												value = recData.get("value"+j) == null ? null : String.valueOf(recData.get("value"+j));
												resultParam.put("["+(i+1)+"]"+" Value "+(valueCnt++),value);
												if(!isEventCode) {
													messageSb.append("Value["+value+"]");
												}
												
											}
											
										}
									}
									
									if(!isEventCode) {
										meterLog.setMessage(messageSb.toString());
									}
									
									Calendar cal = Calendar.getInstance();
									String currTime = sdf.format(cal.getTime());
									

									meterLog.setActivatorId(meter.getMdsId());
									
									meterLog.setActivatorType("EnergyMeter");
									meterLog.setSupplier(meter.getSupplier());
									meterLog.setWriteTime(currTime);
									meterEventLogDao.add(meterLog);

								} catch (Exception e) {
									logger.error("Meter Event Save Error-" + e);
								}
								
							}
							
							bypassResult.setResultValue(resultParam);

						} else if ( obisCode.matches("\\d+\\.\\d+\\.99\\.(1|2)\\.\\d+\\.255")
								|| obisCode.matches("\\d+\\.\\d+\\.24\\.3\\.\\d+\\.255") ) {
							logger.info("#### Load Profile ####");
							// OBIS    CLASS    ATTR    LENGTH    TAG    DATA or LEN/DATA
							//  6        2       1        2        1          *

							List<HashMap<String, Object>> channelList = (List<HashMap<String, Object>>) bypassFrameResult.getResultValue("channelData");
							if (channelList != null ){
								logger.debug("######### Channel List = {}", channelList.toString());
							}
							
							byte[] rawData = (byte[]) bypassFrameResult.getResultValue("rawData");
							logger.debug("######### Total LOAD_PROFILE = {}", Hex.decode(rawData));
							
							List<Object> resultList = (List<Object>) bypassFrameResult.getResultValue("listData");							
							logger.debug("######### Total LIST = {}", resultList.toString());
							if (rawData != null && 0 < rawData.length) {
								byte[] headArray = new byte[]{};
								
						        // DLMS Header OBIS(6), CLASS(2), ATTR(1), LENGTH(2), TAGDATA(n)
						        // DLMS Tag Tag(1), DATA or LEN/DATA (*)
								
								headArray = DataUtil.append(headArray, DataUtil.obisTobyteArray((String) bd.getArgMap().get("obisCode")));								
								headArray = DataUtil.append(headArray, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(bd.getArgMap().get("classId")))));
								byte[] attr = new byte[1];
								attr[0] = DataUtil.getByteToInt(Integer.parseInt(String.valueOf(bd.getArgMap().get("attributeNo")))); 
								headArray = DataUtil.append(headArray, attr);
								headArray = DataUtil.append(headArray, DataUtil.get2ByteToInt(rawData.length));
								rawData = DataUtil.append(headArray, rawData);  // TAGDATA
								
								ModemDao modemDao = DataUtil.getBean(ModemDao.class);
								Modem modem = modemDao.get(bd.getModemId());
								Set<Meter> meters = modem.getMeter();
								Meter meter = meters.iterator().next();

								DeviceConfig deviceConfig = null;
								if (meter.getModel() != null) {
									logger.debug("model of meter[" + meter.getModel() + "]");
									deviceConfig = meter.getModel().getDeviceConfig();
									if (deviceConfig != null) {
										logger.debug("DeviceConfig[" + deviceConfig + "]");
									}
								}

								if ((deviceConfig == null || (deviceConfig != null && (deviceConfig.getParserName() == null || "".equals(deviceConfig.getParserName())))) && meter.getModem().getModel() != null)
									deviceConfig = meter.getModem().getModel().getDeviceConfig();

								if (deviceConfig == null)
									throw new Exception("register modem[" + meter.getModem().getDeviceSerial() + "] or meter[" + meter.getMdsId() + "] config");
								else {
									if (deviceConfig instanceof MeterConfig) {
										MeterConfig meterconfig = (MeterConfig) deviceConfig;
										if (meterconfig.getChannels() == null || meterconfig.getChannels().size() == 0) {
											throw new Exception("set meter channel config for meter model[" + meter.getModel().getName() + "]");
										}
									}
								}

								logger.debug("Parser[" + deviceConfig.getParserName() + "]");
								MeterDataParser parser = (MeterDataParser) Class.forName(deviceConfig.getParserName()).newInstance();
								logger.debug("Parser Instance Created..");

								if (parser == null) {
									throw new Exception("parser is null, check meterId[" + meter.getMdsId() + "], check deviceModel[" + meter.getModel() + "]");
								}

								parser.setMeter(meter);
								//parser.setMeteringTime(getTimeStamp());
								parser.parse(rawData);

								/*
								 *  추후 저장해야 할 경우 아래 주석 풀어줄것. 
					  				MeasurementData mmd = new MeasurementData(meter);
									mmd.setMeterDataParser(parser);
									AbstractMDSaver saver = (AbstractMDSaver) DataUtil.getBean(Class.forName(deviceConfig.getSaverName()));
									MeterDataSaverProxy saverProxy = new MeterDataSaverProxy();
									boolean result = saverProxy.save(saver, mmd);  
								*/
								
								
								/*
								 * 화면에 뿌려줄 데이터 생성
								 */
								SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
								if ( obisCode.matches("\\d+\\.\\d+\\.24\\.3\\.\\d+\\.255") ){
									int counter = 1;
									for ( int port = 1; port < 5; port ++ ){
										LPData[] lplist = null;
										switch (port) {
											case 1 : 
												lplist = ((DLMSKaifa)parser).getMBUS1LPData(); 
												break;
											case 2 :
												lplist = ((DLMSKaifa)parser).getMBUS2LPData(); 
												break;
											case 3 :
												lplist = ((DLMSKaifa)parser).getMBUS3LPData(); 
												break;
											case 4 :
												lplist = ((DLMSKaifa)parser).getMBUS4LPData(); 
												break;	
										}
										if ( lplist == null )
											continue;
										for(int a=0; a<lplist.length; a++){
											LPData lp = lplist[a];
											
											StringBuilder sb = new StringBuilder();
									        String dateTime =formatter.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lp.getDatetime()));
									        Double[] values = lp.getCh();
									        for(int i=0; i<values.length; i++){
									        	sb.append("Port[" + port + "] Ch[" + i + "]=" + values[i] + ", ");
									        }
									        sb.delete(sb.length()-2, sb.length());
									        
									        resultParam.put(counter + ". " + dateTime, sb.toString());
											counter++;
											logger.debug("MBUS" + port + " Load Profile Value ==> {}{}", counter + ". " + dateTime, sb.toString());
										}	
									}
								}
								else {
									LPData[] lplist = ((DLMSKaifa)parser).getLPData();
									for(int a=0; a<lplist.length; a++){
										LPData lp = lplist[a];
										
										StringBuilder sb = new StringBuilder();
								        String dateTime =formatter.format(DateTimeUtil.getDateFromYYYYMMDDHHMMSS(lp.getDatetime()));
								        Double[] values = lp.getCh();
								        for(int i=0; i<values.length; i++){
								        	sb.append(" Ch[" + i + "]=" + values[i] + ", ");
								        }
								        sb.delete(sb.length()-2, sb.length());
								        
								        resultParam.put((a+1) + ". " + dateTime, sb.toString());
										
										logger.debug("Load Profile Value ==> {}{}", a + ". " + dateTime, sb.toString());
									}
								}
								bypassResult.setResultValue(resultParam);
								
								////////
								
								////////
					
							}

							bypassResult.setCommnad(bd.getCommand());
							bypassResult.setSuccess(true);
						}

					}
					logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());

				}

			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e.getStackTrace());
			}
		} else if (bd.getCommand().equals("cmdGetProfileObject")) {
			logger.info("Received cmdGetProfileObject");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_PROFILE_OBJECT);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				List<HashMap<String, Object>> returnList = (List<HashMap<String, Object>>) bypassFrameResult.getResultValue();
				if (resultParam != null && bypassFrameResult.getResultValue() != null) {
					for (int i = 0; i < returnList.size(); i++) {
						HashMap<String, Object> tempMap = returnList.get(i);
						resultParam.put("Channel " + (1+i), "ClassId[" + tempMap.get("classId") + "], ObisCode[" + tempMap.get("obisCode") + "], AttributeNo[" + tempMap.get("attribute") + "]");
					}
					bypassResult.setResultValue(resultParam);
				}

				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdGetProfilePeriod")) {
			logger.info("Received cmdGetProfilePeriod");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_PROFILE_PERIOD);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				if (resultParam != null && bypassFrameResult.getResultValue() != null) {
					if (bypassFrameResult.getResultValue() != null) {
						resultParam.put("period(sec)", String.valueOf(bypassFrameResult.getResultValue()));
					}
					bypassResult.setResultValue(resultParam);
				}
				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdSetProfilePeriod")) {
			logger.info("Received cmdSetProfilePeriod");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.SET_PROFILE_PERIOD);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdGetThresholdNormal")) {
			logger.info("Received cmdGetThresholdNormal");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_THRESHOLD_NORMAL);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				if (resultParam != null && bypassFrameResult.getResultValue() != null) {
					if (bypassFrameResult.getResultValue() != null) {
						resultParam.put("Threshold normal", String.valueOf(bypassFrameResult.getResultValue()));
					}
					bypassResult.setResultValue(resultParam);
				}

				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdSetThresholdNormal")) {
			logger.info("Received cmdSetThresholdNormal");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.SET_THRESHOLD_NORMAL);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdGetMinOverThresholdDuration")) {
			logger.info("Received cmdGetMinOverThresholdDuration");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_MINOVER_THRESHOLD_DURATION);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();

				if (resultParam != null && bypassFrameResult.getResultValue() != null) {
					resultParam.put("Min over threshold duration", String.valueOf(bypassFrameResult.getResultValue()));
					bypassResult.setResultValue(resultParam);
				}

				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdSetMinOverThresholdDuration")) {
			logger.info("Received cmdSetMinOverThresholdDuration");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.SET_MINOVER_THRESHOLD_DURATION);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdGetRelayState")) {
			logger.info("Received cmdGetRelayState");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_DISCONNECT_CONTROL);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();

				if (resultParam != null && bypassFrameResult.getResultValue() != null) {
					resultParam.put("value", String.valueOf(bypassFrameResult.getResultValue()));
					bypassResult.setResultValue(resultParam);
				}

				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdSetRelayState")) {
			logger.info("Received cmdSetRelayState");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.SET_DISCONNECT_CONTROL);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdActRelayState")) {
			logger.info("Received cmdActRelayState");
			try {
				bypassResult = new BypassResult();
				bypassResult.setCommnad(bd.getCommand());
				// -> UPDATE START 2016/09/14 SP-117
				// if (bypassFrameResult.isFinished() == true && bypassFrameResult.getLastProcedure() == Procedure.ACTION_DISCONNECT_CONTROL) {
				if( (bypassFrameResult.isFinished() == true) && 
					((bypassFrameResult.getLastProcedure() == Procedure.ACTION_DISCONNECT_CONTROL)  ||
					 (bypassFrameResult.getLastProcedure() == Procedure.GET_REGISTER_VALUE       )) ){
				// <- UPDATE END   2016/09/14 SP-117
					Map<String, Object> resultParam = new HashMap<String, Object>();

					resultParam.put("RESULT_STATUS", bypassFrameResult.getLastProcedure().name());
					if ( bypassFrameResult.getResultValue("status") != null ){
						ActionResult result = (ActionResult)bypassFrameResult.getResultValue("status");
						if ( result == ActionResult.SUCCESS){
							resultParam.put("RESULT_VALUE",  "Success");
						}
						else {
							resultParam.put("RESULT_VALUE",  "Fail");
							resultParam.put("failReason", result.name());
						}
					}
					else if ( bypassFrameResult.getResultValue() != null
							&& bypassFrameResult.getResultValue() instanceof ActionResult){
						ActionResult result = (ActionResult)bypassFrameResult.getResultValue();
						if ( result == ActionResult.SUCCESS){
							resultParam.put("RESULT_VALUE",  "Success");
						}
						else {
							resultParam.put("RESULT_VALUE",  "Fail");
							resultParam.put("failReason", result.name());
						}
					}
		            
		            // -> UPDATE START 2016/0920 SP-117
					// if ( bypassFrameResult.getResultValue("value") != null){
					// 	resultParam.put("value",  bypassFrameResult.getResultValue("value"));	
					// }
					if ( bypassFrameResult.getResultValue("Relay Status") != null ) {
						MeterDao         meterDao      = DataUtil.getBean(MeterDao.class);
						Meter            meter         = meterDao.get( bd.getMeterId() );
			            DLMSKaifaMDSaver saver         = (DLMSKaifaMDSaver)DataUtil.getBean(DLMSKaifaMDSaver.class);
			            Boolean          actrelayonoff = (Boolean)bypassFrameResult.getResultValue("ActRelayOnOff");
						resultParam.put( "Relay Status",  bypassFrameResult.getResultValue("Relay Status") );	
						if( (resultParam.get("Relay Status") == RELAY_STATUS_KAIFA.Connected) && (actrelayonoff == true) ) {
							logger.info("ActRelayOn: updateMeterStatusNormal()");
					        saver.updateMeterStatusNormal( meter );
						}
						else if( (resultParam.get("Relay Status") == RELAY_STATUS_KAIFA.Disconnected) && (actrelayonoff == false) ) {
							logger.info("ActRelayOff: updateMeterStatusCutOff()");
					        saver.updateMeterStatusCutOff( meter );
						}
					}
		            // <- UPDATE END   2016/0920 SP-117
					
					bypassResult.setSuccess(true);
					bypassResult.setResultValue(resultParam);
					logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
				}
				else {
					Map<String, Object> resultParam = new LinkedHashMap<String,Object>();
					bypassResult.setSuccess(false);
					resultParam.put("RESULT_STEP", bypassFrameResult.getLastProcedure() == null ? bypassFrameResult.getStep().name() : bypassFrameResult.getLastProcedure().name());
					resultParam.put("RESULT_VALUE", "Fail");
					bypassResult.setResultValue(resultParam);
					logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
				}
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		}else if (bd.getCommand().equals("cmdActSetEncryptionKey")) {
			logger.info("Received cmdActSetEncryptionKey");
			try {
				bypassResult = new BypassResult();
				bypassResult.setCommnad(bd.getCommand());
				if (bypassFrameResult.isFinished() == true && bypassFrameResult.getLastProcedure() == Procedure.ACTION_SET_ENCRYPTION_KEY) {
					Map<String, Object> resultParam = new HashMap<String, Object>();

					resultParam.put("RESULT_STATUS", bypassFrameResult.getLastProcedure().name());
					if ( bypassFrameResult.getResultValue() != null
							&& bypassFrameResult.getResultValue() instanceof ActionResult){
						ActionResult result = (ActionResult)bypassFrameResult.getResultValue();
						if ( result == ActionResult.SUCCESS){
							resultParam.put("RESULT_VALUE",  "Success");
						}
						else {
							resultParam.put("RESULT_VALUE",  "Fail");
							resultParam.put("failReason", result.name());
						}
					}
					bypassResult.setSuccess(true);
					bypassResult.setResultValue(resultParam);
					logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
				}
				else {
					Map<String, Object> resultParam = new LinkedHashMap<String,Object>();
					bypassResult.setSuccess(false);
					resultParam.put("RESULT_STEP", bypassFrameResult.getLastProcedure() == null ? bypassFrameResult.getStep().name() : bypassFrameResult.getLastProcedure().name());
					resultParam.put("RESULT_VALUE", "Fail");
					bypassResult.setResultValue(resultParam);
					logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
				}
				
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		}
		else if (bd.getCommand().equals("cmdActTransferKey")) {
			logger.info("Received cmdActTransferKey");
			try {
				bypassResult = new BypassResult();
				bypassResult.setCommnad(bd.getCommand());
				if (bypassFrameResult.isFinished() == true && bypassFrameResult.getLastProcedure() == Procedure.ACTION_TRANSFER_KEY) {
					Map<String, Object> resultParam = new HashMap<String, Object>();

					resultParam.put("RESULT_STATUS", bypassFrameResult.getLastProcedure().name());
					if ( bypassFrameResult.getResultValue() != null
							&& bypassFrameResult.getResultValue() instanceof ActionResult){
						ActionResult result = (ActionResult)bypassFrameResult.getResultValue();
						if ( result == ActionResult.SUCCESS){
							resultParam.put("RESULT_VALUE",  "Success");
						}
						else {
							resultParam.put("RESULT_VALUE",  "Fail");
							resultParam.put("failReason", result.name());
						}
					}
					bypassResult.setSuccess(true);
					bypassResult.setResultValue(resultParam);
					logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
				}
				else {
					Map<String, Object> resultParam = new LinkedHashMap<String,Object>();
					bypassResult.setSuccess(false);
					resultParam.put("RESULT_STEP", bypassFrameResult.getLastProcedure() == null ? bypassFrameResult.getStep().name() : bypassFrameResult.getLastProcedure().name());
					resultParam.put("RESULT_VALUE", "Fail");
					bypassResult.setResultValue(resultParam);
					logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
				}
				
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} 
		else if (bd.getCommand().equals("cmdGetValue")) {
			logger.info("Received cmdGetValue");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_VALUE);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();

				if (resultParam != null && bypassFrameResult.getResultValue() != null) {
					resultParam.put("value", String.valueOf(bypassFrameResult.getResultValue()));
					bypassResult.setResultValue(resultParam);
				}

				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		} else if (bd.getCommand().equals("cmdSetValue")) {
			logger.info("Received cmdSetValue");
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.SET_VALUE);

				Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
				logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		}else if ((bd.getCommand().equals("cmdGetLoadProfileOnDemand"))||(bd.getCommand().equals("cmdGetLoadProfileOnDemandMbb"))) {
//			logger.info("Received cmdGetLoadProfileOnDemand");
			logger.info("Received " + bd.getCommand());
			try {
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_PROFILE_BUFFER);
				if (bypassFrameResult.isFinished() == true && 
						(bypassFrameResult.getLastProcedure() == Procedure.GET_PROFILE_BUFFER ||
						bypassFrameResult.getLastProcedure() == Procedure.GET_REGISTER_UNIT )) {
					makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), bypassFrameResult.getLastProcedure());
					HashMap<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
//					logger.debug("###### [ModemId={}] cmdGetLoadProfileOnDemand resultParam ==> {}", bd.getModemId(), resultParam.toString());
					logger.debug("###### [ModemId={}] " + bd.getCommand() + " resultParam ==> {}", bd.getModemId(), resultParam.toString());
					
					if (resultParam != null && 0 < bypassFrameResult.getValueSize()) {
						/*
						 * resultList 구성 형식
						 * resultList.get(0) : channel 정보 _{classId : 71(Limiter), attributeNo : 3(capture_object)}
						 * resultList.get(x) : channel 에 따른 data 정보 _{classId : 71(Limiter), attributeNo : 2(buffer)}
						 * 					   resultList.get(0)의 channel 순서대로 데이터가 정렬되어 올라옴.
						 * 
						 * ex) resultList.get(0) = {(classId=8,attribute=2,obisCode=0000010000FF),(classId=1,attribute=2,obisCode=0000600B00FF)}
						 *     resultList.get(1) = {20160602070000,1} //Clock,eventCode
						 *     resultList.get(2) = {20160602073000,1}
						 * 	   resultList.get(3) = {20160602073200,2}
						 * 
						 */
						String obisCode = (String) bd.getArgMap().get("obisCode");
						if ( obisCode.matches("\\d+\\.\\d+\\.99\\.(1|2)\\.\\d+\\.255")
								|| obisCode.matches("\\d+\\.\\d+\\.24\\.3\\.\\d+\\.255") ) {
							logger.info("#### Load Profile ####");
							// OBIS    CLASS    ATTR    LENGTH    TAG    DATA or LEN/DATA
							//  6        2       1        2        1          *

							List<HashMap<String, Object>> channelList = (List<HashMap<String, Object>>) bypassFrameResult.getResultValue("channelData");
							if (channelList != null ){
								logger.debug("######### Channel List = {}", channelList.toString());
							}
							
							byte[] rawData = (byte[]) bypassFrameResult.getResultValue("rawData");
							logger.debug("######### Total LOAD_PROFILE = {}", Hex.decode(rawData));
							
//							List<Object> resultList = (List<Object>) bypassFrameResult.getResultValue("listData");							
//							logger.debug("######### Total LIST = {}", resultList.toString());

							Map<String, Object> resultValue = (HashMap<String, Object>) new HashMap<String, Object>();
							byte[] headArray = new byte[]{};
							headArray = DataUtil.append(headArray, DataUtil.obisTobyteArray((String) bd.getArgMap().get("obisCode")));								
							headArray = DataUtil.append(headArray, DataUtil.get2ByteToInt(Integer.parseInt(String.valueOf(bd.getArgMap().get("classId")))));
							byte[] attr = new byte[1];
							attr[0] = DataUtil.getByteToInt(Integer.parseInt(String.valueOf(bd.getArgMap().get("attributeNo")))); 
							headArray = DataUtil.append(headArray, attr);
							headArray = DataUtil.append(headArray, DataUtil.get2ByteToInt(rawData.length));
							rawData = DataUtil.append(headArray, rawData);  // TAGDATA
							if(!bd.getCommand().equals("cmdGetLoadProfileOnDemandMbb")) {
								resultValue.put("rawData", rawData);
							}
							
							// Channel Data
							resultValue.put("channelData",(List<HashMap<String, Object>>) bypassFrameResult.getResultValue("channelData"));
							
							OBIS cumulatives[] =  new OBIS [4];
							cumulatives[0] = OBIS.CUMULATIVE_ACTIVEENERGY_IMPORT;
							cumulatives[1] = OBIS.CUMULATIVE_ACTIVEENERGY_EXPORT;
							cumulatives[2] = OBIS.CUMULATIVE_REACTIVEENERGY_IMPORT;
							cumulatives[3] = OBIS.CUMULATIVE_REACTIVEENERGY_EXPORT; 
							for ( int i = 0; i < cumulatives.length; i++ ){
								if ( bypassFrameResult.getResultValue(cumulatives[i].name()) != null ){
									resultValue.put(cumulatives[i].name(), bypassFrameResult.getResultValue(cumulatives[i].name()));
								}
								String keyUnit = cumulatives[i].name() + "_UNIT"; 
								if ( bypassFrameResult.getResultValue(keyUnit) != null ){
									resultValue.put(keyUnit, bypassFrameResult.getResultValue(keyUnit));
								}
							}
//							String activeImpName = ENERGY_LOAD_PROFILE.ActiveEnergyImport.name();
//							if ( bypassFrameResult.getResultValue(activeImpName) != null ){
//								resultValue.put(activeImpName, bypassFrameResult.getResultValue(activeImpName));
//							}
//							String activeExpName = ENERGY_LOAD_PROFILE.ActiveEnergyExport.name();
//							if ( bypassFrameResult.getResultValue(activeExpName) != null ){
//								resultValue.put(activeExpName,bypassFrameResult.getResultValue(activeExpName) );
//							}
//							String reactiveImpName =  ENERGY_LOAD_PROFILE.ReactiveEnergyImport.name();
//							if ( bypassFrameResult.getResultValue(reactiveImpName) != null){
//								resultValue.put(reactiveImpName, bypassFrameResult.getResultValue(reactiveImpName));
//							}
//							String reactiveExpName = ENERGY_LOAD_PROFILE.ReactiveEnergyExport.name();
//							if ( bypassFrameResult.getResultValue(reactiveExpName) != null ){
//								resultValue.put(reactiveExpName, bypassFrameResult.getResultValue(reactiveExpName));
//							}
							
							if(bd.getCommand().equals("cmdGetLoadProfileOnDemandMbb")) {
					            //Class clazz = null;
					            //clazz = Class.forName("DLMSKaifaMDSaver");
					            DLMSKaifaMDSaver saver = (DLMSKaifaMDSaver)DataUtil.getBean(DLMSKaifaMDSaver.class);
						        MeterData md = saver.parseAndSaveData(bd.getMeterId(), bd.getModemId(), rawData, resultValue);
						        if(md != null && md.getMap() != null){
						        	resultValue.put("detail", saver.getOndemandDetailString(md.getMap()));
						        }
						        else {
						        	resultValue.put("detail", "");
						        }
						        // Remove data which value is not string;
								for ( int i = 0; i < cumulatives.length; i++ ){
									resultValue.remove(cumulatives[i].name());
									String keyUnit = cumulatives[i].name() + "_UNIT"; 
									resultValue.remove(keyUnit);
								}
								resultValue.remove("channelData");
							}
							
							bypassResult.setResultValue(resultValue);
							bypassResult.setCommnad(bd.getCommand());
							bypassResult.setSuccess(true);
						}

					}
					logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());

				}

			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e.getStackTrace());
			}
		}
		// -> INSERT START 2016/08/24 SP117
		else if( bd.getCommand().equals("cmdGetRelayStatusAll") )
		{
			logger.info("Received cmdGetRelayStatusAll");
			try {
				// makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_DISCONNECT_CONTROL);
				makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), Procedure.GET_REGISTER_VALUE);
				if( (bypassFrameResult.isFinished() == true) && 
					((bypassFrameResult.getLastProcedure() == Procedure.GET_REGISTER_VALUE) ||
					 (bypassFrameResult.getLastProcedure() == Procedure.GET_DISCONNECT_CONTROL)) )
				{
					makeMeterParamCommonResult(bypassFrameResult, bd.getCommand(), bypassFrameResult.getLastProcedure());
					//HashMap<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
					//resultValue.put( "RelayStatus", bypassFrameResult.getResultValue("RelayStatus"));
					//resultValue.put( "LoadControlStatus", bypassFrameResult.getResultValue("LoadControlStatus"));
					//resultValue.put( "LoadControlMode", bypassFrameResult.getResultValue("LoadControlMode"));

					// -> UPDATE START 2016/09/20 SP-117
					// Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
					// resultParam.put( "RelayStatus"      , bypassFrameResult.getResultValue("RelayStatus")       );
					// resultParam.put( "LoadControlStatus", bypassFrameResult.getResultValue("LoadControlStatus") );
					// resultParam.put( "LoadControlMode"  , bypassFrameResult.getResultValue("LoadControlMode")   );
					// bypassResult.setResultValue( resultParam );
					
					MeterDao meterDao = DataUtil.getBean(MeterDao.class);
					Meter meter = meterDao.get( bd.getMeterId() );
		            DLMSKaifaMDSaver saver = (DLMSKaifaMDSaver)DataUtil.getBean(DLMSKaifaMDSaver.class);
					Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();
					resultParam.put( "Relay Status"     , bypassFrameResult.getResultValue("Relay Status")       );
					resultParam.put( "LoadControlStatus", bypassFrameResult.getResultValue("LoadControlStatus") );
					resultParam.put( "LoadControlMode"  , bypassFrameResult.getResultValue("LoadControlMode")   );
					if( resultParam.get("Relay Status") == RELAY_STATUS_KAIFA.Connected ) {
				        saver.updateMeterStatusNormal( meter );
					}
					else if( resultParam.get("Relay Status") == RELAY_STATUS_KAIFA.Disconnected ) {
				        saver.updateMeterStatusCutOff( meter );
					}
					bypassResult.setResultValue( resultParam );
					// <- UPDATE END   2016/09/20 SP-117
					
					// Map<String, Object> resultParam = (LinkedHashMap<String, Object>) bypassResult.getResultValue();

					// if (resultParam != null && bypassFrameResult.getResultValue() != null) {
					//	resultParam.put("value", String.valueOf(bypassFrameResult.getResultValue()));
					//	bypassResult.setResultValue(resultParam);
					// }

					logger.debug("[{}] Result = {}", bd.getCommand(), resultParam.toString());
				}
			} catch (Exception e) {
				logger.error("{} Excute Error - {}", bd.getCommand(), e);
			}
		}
		// <- INSERT END   2016/08/24 SP117

		/*
		 * 모든 통신이 끝나면 Nullbypass Close요청
		 */
		
		logger.debug("### [" + bd.getCommand() + "] Execute Bypass - {}", (bypassFrameResult.isResultState() == true ? "success" : "fail"));

		
		if (bypassFrameResult.isFinished() || !bypassFrameResult.isResultState()) {
			logger.debug("BypassFrameResult is fineshed.");
			isBypassFinish = true;
			
			
			bd.getFrameFactory().stop(session);
			
			/**
			 *  2016-08-01
			 * Modem측에서 Thread를 여러개 띄울 형편이 안되 reqNullBypassColse는 요청하지 않고 생략하는것으로 한다.
			 * => Modem쪽에서는 NullBypassPasOpen요청시 설정한 timeout시간만큼 대기하고 있다가 자동으로 close처리한다고함.
			 * 추후 필요시 주석 풀고 사용할것.
			 * 
			 *
			 * 			Map<String, Object> rboResult = session.getBypassClient().reqNullBypassClose();
			 *          logger.debug("Req Bypass Close - ", rboResult.toString());
			 */
			

		}
	}

	private void makeMeterParamCommonResult(BypassFrameResult bypassFrameResult, String command, Procedure procedure) throws Exception {
		// 실패시는 모두 저장
		if (!bypassFrameResult.isResultState()) {
			bypassResult = new BypassResult();
			bypassResult.setCommnad(command);
			bypassResult.setSuccess(false);

			Map<String, Object> resultParam = new LinkedHashMap<String,Object>();
			resultParam.put("RESULT_STEP", bypassFrameResult.getLastProcedure() == null ? bypassFrameResult.getStep().name() : bypassFrameResult.getLastProcedure().name());
			resultParam.put("RESULT_VALUE", "Fail");
			bypassResult.setResultValue(resultParam);
		} else {
			try {
				logger.debug("####여기야 ==> {}", bypassFrameResult.toString());
				if (bypassFrameResult.isFinished() == true && bypassFrameResult.getLastProcedure() == procedure) {

					Map<String, Object> resultParam = new LinkedHashMap<String, Object>();
					resultParam.put("RESULT_STEP", bypassFrameResult.getLastProcedure().name());
					resultParam.put("RESULT_VALUE", "Success");

					bypassResult = new BypassResult();
					bypassResult.setCommnad(command);
					bypassResult.setSuccess(true);
					bypassResult.setResultValue(resultParam);
				}
			} catch (Exception e) {
				logger.error("Procedure save error - {}", e);
			}
		}

	}

	public BypassResult getBypassResult(MultiSession session) throws Exception
    {
        long stime = System.currentTimeMillis();
        long prevtime = System.currentTimeMillis();	// INSERT SP-628
        long ctime = 0;
        
        logger.debug("session.isConnected? = {}, isBypassFinish? = {}", session.isConnected(), isBypassFinish);
        
        /**
         * Session connect가 늦게 되는경우 null값을 반환하는 문제가 있어서 추가해줌.
         */
        if(session.isConnected() == false){
        	logger.debug("Session is not still connected. so i'm waiting 2second.");
        	waitResponse(2000);
        }
        
        // INSERT START SP-628
        int retry = 0;
    	BypassDevice bd = session.getBypassDevice();    		
    	if (bd.getFrameFactory() instanceof BypassSORIAFactory) {
    		BypassSORIAFactory factory = (BypassSORIAFactory)bd.getFrameFactory();
            String prevStep = factory.getStepName();
            while (session.isConnected()) {
            	if (factory.isHandshakeFinish()) {
            		break;
            	}
            	else {
        			waitResponse(500);
                    ctime = System.currentTimeMillis();
                    
                    if(((ctime - prevtime)/1000) >= handshakeTimeout) {
                    	logger.debug("DLMS handshake responese Timeout. Step[" +  factory.getStepName() + "]");
                    	if (!prevStep.equals(factory.getStepName())){
                    		retry = 0;
                    	}
                    	
                    	if (retry >= handshakeRetry) {
                        	logger.debug("DLMS handshake retry failed. Step[" +  factory.getStepName() + "]");
                        	throw new Exception("[DLMS handshake Response Timeout : " + handshakeTimeout + "x" + handshakeRetry +"]");
                    	}
                    	logger.debug("DLMS handshake retry start. Retry Times[" + (retry+1) + "] Step["+  factory.getStepName() + "]");
                    	factory.retryHandshake();
                    	retry++;
                    	prevStep = factory.getStepName();                    	
                    	prevtime = System.currentTimeMillis();
                    }
            	}
            }
    	}
        // INSERT END SP-628
        
        while(session.isConnected()){
    		if (isBypassFinish) {
    			return this.bypassResult;
    		} else {
    			waitResponse(500);
                ctime = System.currentTimeMillis();
                
                if(((ctime - stime)/1000) > responseTimeout)
                {
                    throw new Exception("[BypassResult Response Timeout : " + responseTimeout +"]");
                }
    		}
        }
        return null;
    }
	
    
    /**
     * wait util received command response data
     */
    public void waitResponse(int waitTime)
    {
        synchronized(resMonitor)
        { 
            try { resMonitor.wait(waitTime);
            } catch(InterruptedException ie) {ie.printStackTrace();}
        }
    }

    /*
     * SORIA Kaifa Meter Key Set.
     */
	public void getKaifaCustomGetMeterKeyString(BypassFrameResult bypassFrameResult, Map<String, Object> resultParam) {
		if (bypassFrameResult.getResultValue(Procedure.GET_SORIA_METER_KEY_A.name()) != null) {
			resultParam.put(Procedure.GET_SORIA_METER_KEY_A.name(), String.valueOf(bypassFrameResult.getResultValue(Procedure.GET_SORIA_METER_KEY_A.name())));
		}else if (bypassFrameResult.getResultValue(Procedure.GET_SORIA_METER_KEY_B.name()) != null) {
			resultParam.put(Procedure.GET_SORIA_METER_KEY_B.name(), String.valueOf(bypassFrameResult.getResultValue(Procedure.GET_SORIA_METER_KEY_B.name())));
		}else if (bypassFrameResult.getResultValue(Procedure.GET_SORIA_METER_KEY_C.name()) != null) {
			resultParam.put(Procedure.GET_SORIA_METER_KEY_C.name(), String.valueOf(bypassFrameResult.getResultValue(Procedure.GET_SORIA_METER_KEY_C.name())));
		}
		
		for(Map.Entry<String, Object> entry : resultParam.entrySet()){
			logger.info("KaifaCustomGetMeterKeyString ==> " + entry.getKey() + ", " + entry.getValue());
		}
	}
	

}
