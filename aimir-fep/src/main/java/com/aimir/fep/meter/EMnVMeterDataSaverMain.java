/**
 * (@)# EMnVMeterDataSaverMain.java
 *
 * 2015. 5. 2.
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
package com.aimir.fep.meter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.mvm.MeasurementHistoryDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.DeviceVendorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.meter.entry.EMnVMeasurementData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.meter.parser.MeterDataParser;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException.EMnVExceptionReason;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeterType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVMeteringDataType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVModebusVendorType;
import com.aimir.fep.protocol.emnv.frame.payload.DCUInfo;
import com.aimir.fep.protocol.emnv.frame.payload.EMnVMeteringDataFramePayLoad;
import com.aimir.fep.protocol.emnv.frame.payload.IModemInfo;
import com.aimir.fep.protocol.emnv.frame.payload.SubgigaModemInfo;
import com.aimir.fep.protocol.emnv.frame.payload.ZigBeeModemInfo;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.Compensator;
import com.aimir.model.device.EMnVSubGiga;
import com.aimir.model.device.EMnVZigBee;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.Inverter;
import com.aimir.model.device.LTE;
import com.aimir.model.device.MCU;
import com.aimir.model.device.MeasurementHistory;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.DeviceConfig;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * EMnV 검침데이터 저장
 * 
 * @author simhanger
 *
 */
@Service
@Transactional(value = "transactionManager")
public class EMnVMeterDataSaverMain {
	private static Logger log = LoggerFactory.getLogger(EMnVMeterDataSaverMain.class);

	@Autowired
	private MeasurementHistoryDao measurementHistoryDao;

	@Autowired
	private ModemDao modemDao;

	@Autowired
	private MeterDao meterDao;

	@Autowired
	private MCUDao mcuDao;

	@Autowired
	private SupplierDao supplierDao;

	@Autowired
	private DeviceModelDao deviceModelDao;

	@Autowired
	private DeviceVendorDao deviceVendorDao;

	@Autowired
	private CodeDao codeDao;
	
//	@Autowired
//	private EMnVEventUtil eventUtil;

	
	
	private static List<String> isProcessMeter = null;
	static {
		if (isProcessMeter == null) {
			isProcessMeter = new ArrayList<String>();
		}
	}

	public boolean save(EMnVMeteringDataFramePayLoad data) {
		boolean result = false;
		
		try {
			/**
			 * RAW데이터 저장
			 */
			boolean rawdataSaved = Boolean.parseBoolean(FMPProperty.getProperty("rawdata.save.enable", "false"));
			if (rawdataSaved) {
				MeasurementHistory mh = new MeasurementHistory();
				mh.setDeviceType(data.getMeterType().name());

				/*
				 * General Frame의 Source Address.
				 * DCU => mobile number
				 * LTE단독 => mobile number
				 * sub-giga => IPv6
				 * zigbee => EUI64
				 */
                mh.setId(TimeUtil.getCurrentLongTime());
				mh.setDeviceId(data.getModemInfo().getDeviceId());
				mh.setDataType(Integer.parseInt(FMPProperty.getProperty("hdm.data.type.md", "1")));
				//mh.setDataCount(mdHistoryData.getEntryCount());
				mh.setWriteDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));
				mh.setYyyymmdd(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMdd"));
				mh.setHhmmss(DateTimeUtil.getCurrentDateTimeByFormat("HHmmss"));
				mh.setRawData(data.getMdData());

				measurementHistoryDao.add(mh);
				log.debug("MeasurementHistory Save -[{}] ModemId={}", mh.getDataType(), mh.getDeviceId());
			}

			/**
			 * SRC ADDRESS 타입이 IPv6 => Sub-Giga, EUI_64 => ZigBee, MOBILE_NUMBER
			 * => DCU로 처리.
			 * 
			 * DCU 단독 ==> SRC_ADDRESS_TYPE = DCU, METER_TYPE = DCU , MODEM_INFO
			 * = DCU DCU + ZIGBEE_GTYPE ==> SRC_ADDRESS_TYPE = EUI64, METER_TYPE
			 * = GTYPE, MODEM_INFO = ZIGBEE DCU + ZIGBEE_ETYPE ==>
			 * SRC_ADDRESS_TYPE = EUI64, METER_TYPE = ETYPE, MODEM_INFO = ZIGBEE
			 * DCU + SUB_GIGA_GTYPE ==> SRC_ADDRESS_TYPE = IPv6, METER_TYPE =
			 * GTYPE, MODEM_INFO = SUB_GIGA DCU + SUB_GIGA_ETYPE ==>
			 * SRC_ADDRESS_TYPE = IPv6, METER_TYPE = ETYPE, MODEM_INFO =
			 * SUB_GIGA
			 * 
			 * LTE_GTYPE ==> SRC_ADDRESS_TYPE = MOBILE_NUMBER, METER_TYPE =
			 * GTYPE, MODEM_INFO = DCU LTE_ETYPE ==> SRC_ADDRESS_TYPE =
			 * MOBILE_NUMBER, METER_TYPE = ETYPE, MODEM_INFO = DCU
			 */
			// 미터타입별로 집중기아이디, 미터아이디, 모뎀아이디로 검증한다. 
			result = saveProcess(data.getMeterType(), data.getMeteringDataType(), data.getModemInfo().getDeviceId(), data.getModemInfo(), data.getMdData(), data.getModemImei());

			log.info("########## saveProcess 결과 ==> {}", result);
			log.info("    ");
			log.info("    ");
			

		} catch (Exception e) {			
			log.debug("Payload Save Error - {}", e);
		}
		
		return result;
	}

	private boolean saveProcess(EMnVMeterType meterType, EMnVMeteringDataType meteringDataType, String modemId, IModemInfo modemInfo, byte[] mdData, String modemImei) {
		boolean result = false;
		//boolean isInverter = false;  // Meter or Invert 구분 여부
		String typeCheck = MeterType.EnergyMeter.name(); // Default는 애너지 미터
		Meter meter = null;
		try {
			/*
			 * 집중기 검증 및 등록
			 */
			MCU mcu = validateMCU(meterType, modemId);
//			if(mcu == null){
//				return false;
//			}

			/*
			 * 모뎀 검증 및 등록
			 */
			Modem modem = validateModem(meterType, mcu, modemInfo, modemImei);
			if(modem == null){
				return false;
			}

			// DeviceModel 설정. 미터타입으로 설정한다
			DeviceModel model = null;
			if (meterType == EMnVMeterType.E_TYPE_1_0 || meterType == EMnVMeterType.LTE_E_TYPE_1_0 || meterType == EMnVMeterType.SUBGIGA_E_TYPE_1_0 || meterType == EMnVMeterType.ZIGBEE_E_TYPE_1_0) {
				List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "E10_TYPE_METER"); 
				if (models.size() == 1) {
					model = models.get(0);
				}
			} else if (meterType == EMnVMeterType.E_TYPE_1_1 || meterType == EMnVMeterType.LTE_E_TYPE_1_1 || meterType == EMnVMeterType.SUBGIGA_E_TYPE_1_1 || meterType == EMnVMeterType.ZIGBEE_E_TYPE_1_1) {
				List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "E11_TYPE_METER"); // 아직 작업 안했음.
				if (models.size() == 1) {
					model = models.get(0);
				}
			} else if (meterType == EMnVMeterType.G_TYPE || meterType == EMnVMeterType.LTE_G_TYPE || meterType == EMnVMeterType.SUBGIGA_G_TYPE || meterType == EMnVMeterType.ZIGBEE_G_TYPE) {
				List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "G_TYPE_METER");
				if (models.size() == 1) {
					model = models.get(0);
				}
			} else if (meterType == EMnVMeterType.MODBUS || meterType == EMnVMeterType.LTE_MODBUS || meterType == EMnVMeterType.SUBGIGA_MODBUS || meterType == EMnVMeterType.ZIGBEE_MODBUS) {
		        EMnVModebusVendorType vendorType = null;
				byte[] vendorType_byte = new byte[EMnVConstants.MODBUS_VENDOR_TYPE_LEN];
				System.arraycopy(mdData, 0, vendorType_byte, 0, vendorType_byte.length);
				vendorType = EMnVModebusVendorType.getItem(vendorType_byte[0]);		        
		        log.debug("[MODBUS] VENDOER_TYPE[{}] ==> HEX=[{}]", vendorType, Hex.decode(vendorType_byte));
		        String vendorTypeName = "";
		        switch (vendorType) {
				case LS:
					//vendorTypeName = "SU055iP5A";
					vendorTypeName = "LS_INVERTER";
					break;
				case HYUNDAI:
					//vendorTypeName = "N700E";
					vendorTypeName = "HYUNDAI_INVERTER";
					break;
				case ROCKWELL:
					//vendorTypeName = "F500";
					vendorTypeName = "ROCKWELL_INVERTER";
					break;
				}
				
				List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), vendorTypeName); 
				if (models.size() == 1) {
					model = models.get(0);
				}
				typeCheck = MeterType.Inverter.name();
			}else if (meterType == EMnVMeterType.INVERTER_LOG || meterType == EMnVMeterType.LTE_INVERTER_LOG || meterType == EMnVMeterType.SUBGIGA_INVERTER_LOG || meterType == EMnVMeterType.ZIGBEE_INVERTER_LOG) {
				List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "DEFAULT_INVERTER"); // INVERTER LOG용으로 사용함.
				if (models.size() == 1) {
					model = models.get(0);
				}
				typeCheck = MeterType.Inverter.name();
			} else if (meterType == EMnVMeterType.COMPENSATOR) { 
				List<DeviceModel> models = deviceModelDao.getDeviceModelByName(modem.getSupplier().getId(), "DEFAULT_COMPENSATOR"); // device model에 COMPENSATOR등록해야함 
				if (models.size() == 1) {
					model = models.get(0);
				}
				typeCheck = MeterType.Compensator.name();
			}else {
				throw new EMnVSystemException(EMnVExceptionReason.UNKNOWN, EMnVExceptionReason.UNKNOWN.getDesc());
			}

			if(model == null){
				log.debug("meterType={} - {}", meterType, EMnVExceptionReason.UNREGISTERD_DEVICE_MODEL.getDesc());
				throw new EMnVSystemException(EMnVExceptionReason.UNREGISTERD_DEVICE_MODEL, EMnVExceptionReason.UNREGISTERD_DEVICE_MODEL.getDesc());
			}
			EMnVMeasurementData md = new EMnVMeasurementData(getPreMeter(modem, typeCheck));
			//md.setOnDemand(this.isOnDemand());
			//md.decode(mdData);
			
			
			try {
				md.prcMeterParsing(model, meterType, meteringDataType, mdData, modem.getSupplier().getId(), typeCheck);				
			} catch (Exception e) {
				return false;
			}

			/*
			 * 미터 검증 및 등록    : PulseConstant 도 등록한다.
			 * 미터 아이디를 알려면 obis코드를 다 까봐야 해서 
			 * pulseconstant를 사용해야 하는 lpData세팅은 미터검증후(기존에 등록된 미터의 pulseconst가져오기) 진행한다.
			 */
			meter = validateMeter(md.getMeterDataParser(), modem, model, typeCheck);
			if(meter == null){
				return false;
			}

			md.postMeterParsing();

			validateRelation(meter, modem);

			log.debug("METER_ID = {}, MODEM_ID = {}, METER_TYPE ={}, MODEM_TYPE = {}", new Object[] { meter.getMdsId(), modem.getDeviceSerial(), meter.getMeterType(), modem.getModemType() });

			/**
			 * 해당 미터의 Paser와 Saver를 불러와 데이터 저장
			 */
			try {
				// 처리중인지 검사한다.
				if (isProcessMeter.contains(modemId + ":" + meter.getMdsId())) {
					log.info("modemId = {}, meterId = {} on process. SKIP!!", modemId, meter.getMdsId());
				} else {
					isProcessMeter.add(modemId + ":" + meter.getMdsId());
				}

				long startTime = System.currentTimeMillis();
				result = eMnvMDSave(md);
				long endTime = System.currentTimeMillis();
				
				log.info("Save MD modemId[{}] meterId[{}] measurementdata length[{}]", new Object[] { modemInfo, meter.getMdsId(), mdData.length });
				log.info("######## FepProcessorDebug Elapse Time : " + (endTime - startTime) / 1000.0f + "s");
			} finally {
				// 리스트에서 처리된 것을 삭제한다.
				isProcessMeter.remove(modemId + ":" + meter.getMdsId());
			}

			result = true;
		} catch (Exception e) {
			log.error("MCU, Modem, Meter Validation Error.", e);
		}

		return result;
	}

	/**
	 * 집중기 등록 여부 검증. 없으면 자동등록
	 * 
	 * @param meterType
	 * @param mcuId
	 * @return
	 * @throws Exception
	 */
	private MCU validateMCU(EMnVMeterType meterType, String mcuId) throws Exception {
		MCU mcu = null;

		// LTE 단독일 경우 집중기 없음
		if (meterType == EMnVMeterType.LTE_G_TYPE || meterType == EMnVMeterType.LTE_E_TYPE_1_0 
				|| meterType == EMnVMeterType.LTE_E_TYPE_1_1 || meterType == EMnVMeterType.LTE_MODBUS || meterType == EMnVMeterType.LTE_INVERTER_LOG || meterType == EMnVMeterType.COMPENSATOR) {
			mcu = null;
		} else {
			mcu = mcuDao.get(mcuId);

			if (mcu == null) {

				/*
				 *  
				 *  집중기 자동등록 필요.
				 * 
				 * 
				 */

				throw new Exception("Invalid MCU[" + mcuId + "]");
			}
		}
		log.debug("MCU Validation ==> [{}][{}]", new Object[] { meterType.name(), mcuId });
		return mcu;
	}

	/**
	 * 모뎀 등록여부 검증. 없으면 자동등록
	 * 
	 * @param meterType
	 * @param mcu
	 * @param modemInfo
	 * @return
	 * @throws Exception
	 */
//	@Transactional(value = "transactionManager", readOnly=true, propagation=Propagation.REQUIRES_NEW)
	@SuppressWarnings("null")
	private Modem validateModem(EMnVMeterType meterType, MCU mcu, IModemInfo modemInfo, String modemImei) {
		Modem modem = null;
		String modemProdMaker = null;
		
		try {
			modem = modemDao.get(modemInfo.getDeviceId());
			log.debug("Modem Validation ==> [{}][{}][{}]", new Object[] { meterType.name(), mcu, modemInfo.toString() });
			if (modem == null) {
				switch (modemInfo.getModemType()) {
				case LTE:
					modem = new LTE();
					((LTE) modem).setFwVer(DataUtil.getString(((DCUInfo) modemInfo).getFwVersion()).trim());
					((LTE) modem).setHwVer(DataUtil.getString(((DCUInfo) modemInfo).getHwVersion()).trim());
					((LTE) modem).setModemTime(DataUtil.getEMnvModemDate((((DCUInfo) modemInfo).getNowTime())).trim());
					((LTE) modem).setRsrp(DataUtil.getString(((DCUInfo) modemInfo).getRsrp()).trim());
					((LTE) modem).setRsrq(DataUtil.getString(((DCUInfo) modemInfo).getRsrq()).trim());
					((LTE) modem).setTxPower(DataUtil.getString(((DCUInfo) modemInfo).getTxPower()).trim());
					((LTE) modem).setPlmn(DataUtil.getString(((DCUInfo) modemInfo).getPlmn()).trim());
					((LTE) modem).setProdMaker(DataUtil.getString(((DCUInfo) modemInfo).getProdCompany()).trim());
					modemProdMaker = ((LTE) modem).getProdMaker();
					((LTE) modem).setProdMakeDate(DataUtil.getString(((DCUInfo) modemInfo).getProdDate()).trim());
					((LTE) modem).setProdSerial(DataUtil.getString(((DCUInfo) modemInfo).getProdNumber()).trim());
					((LTE) modem).setMdPeriod(DataUtil.getIntToByte(((DCUInfo) modemInfo).getMeteringPeriod()[0]));
					((LTE) modem).setPhoneNumber(((DCUInfo) modemInfo).getDeviceId().trim());    
					((LTE) modem).setImei(modemImei);
					break;
				case SubGiga:
					((EMnVSubGiga) modem).setFwVer(DataUtil.getString(((SubgigaModemInfo) modemInfo).getFwVersion()).trim());
					((EMnVSubGiga) modem).setHwVer(DataUtil.getString(((SubgigaModemInfo) modemInfo).getHwVersion()).trim());
					((EMnVSubGiga) modem).setEuiId(DataUtil.getString((((SubgigaModemInfo) modemInfo).getEuiId())).trim());
					((EMnVSubGiga) modem).setProdMaker(DataUtil.getString(((SubgigaModemInfo) modemInfo).getProdCompany()).trim());
					modemProdMaker = ((EMnVSubGiga) modem).getProdMaker();
					((EMnVSubGiga) modem).setProdMakeDate(DataUtil.getString(((SubgigaModemInfo) modemInfo).getProdDate()).trim());
					((EMnVSubGiga) modem).setProdSerial(DataUtil.getString(((SubgigaModemInfo) modemInfo).getProdNumber()).trim());
					
					
					//((LTE) modem).setImei(modemImei);  서브기가도 이게 필요할수있음. 추후 개발하여 적용할것.
					
					break;
				case ZigBee:
					((EMnVZigBee) modem).setFwVer(DataUtil.getString(((ZigBeeModemInfo) modemInfo).getFwVersion()).trim());
					((EMnVZigBee) modem).setHwVer(DataUtil.getString(((ZigBeeModemInfo) modemInfo).getHwVersion()).trim());
					((EMnVZigBee) modem).setChannelId(DataUtil.getIntToByte(((ZigBeeModemInfo) modemInfo).getChannel()[0]));
					((EMnVZigBee) modem).setPanId(DataUtil.getIntToByte(((ZigBeeModemInfo) modemInfo).getPanId()[0]));
					((EMnVZigBee) modem).setProdMaker(DataUtil.getString(((ZigBeeModemInfo) modemInfo).getProdCompany()).trim());
					modemProdMaker = ((EMnVZigBee) modem).getProdMaker();
					((EMnVZigBee) modem).setProdMakeDate(DataUtil.getString(((ZigBeeModemInfo) modemInfo).getProdDate()).trim());
					((EMnVZigBee) modem).setProdSerial(DataUtil.getString(((ZigBeeModemInfo) modemInfo).getProdNumber()).trim());
					
					//((LTE) modem).setImei(modemImei);  지그비도 이게 필요할수있음. 추후 개발하여 적용할것.
					break;
				default:
					break;
				}

				modem.setDeviceSerial(modemInfo.getDeviceId().trim());
				modem.setModemType(modemInfo.getModemType().name());
				if (mcu != null) {
					modem.setMcu(mcu);
				}
				modem.setInstallDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
				modem.setCommState(1);
				modem.setLastLinkTime(modem.getInstallDate().trim());
				
				
                String tt = modem.getDeviceSerial();
                log.debug("##### [모뎀검증1]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [현재시간-{}", new Object[]{tt, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim()});
                log.debug("##### [모뎀검증1]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [현재시간-{}", new Object[]{tt, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim()});
                log.debug("##### [모뎀검증1]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [현재시간-{}", new Object[]{tt, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim()});
				
				modem.setProtocolType(Protocol.SMS.name());

				if (mcu != null) {
					modem.setSupplier(mcu.getSupplier());
				} else {
					// LTE모뎀인 경우 집중기 정보가 없기때문에 디폴트 공급사 정보를 설정한다.
					modem.setSupplier(supplierDao.getAll().get(0));
				}

				// ############## 제작사명 + 미터타입 으로 모델 정보 추가 ######################
				List<DeviceVendor> vendors = deviceVendorDao.getDeviceVendorByName(modem.getSupplier().getId(), modemProdMaker);
				Map<String, Object> map = new HashMap<String, Object>();
				
				if(vendors == null || vendors.size() <= 0){
					log.error("ERROR - 미등록 vendor사 - {}, {}", modemProdMaker, modem.getDeviceSerial());
					throw new EMnVSystemException(EMnVExceptionReason.UNREGISTERD_VENDOR);
				}else{
					map.put("vendorId", vendors.get(0).getId());

					if (modem.getModemType() == ModemType.SubGiga) {
						map.put("subDeviceType", codeDao.getCodeIdByCode("1.2.1.101"));   // 1.2.1.101 = SubGiga
						List<DeviceModel> models = deviceModelDao.getDeviceModels(map);
						if (models.size() == 1) {
							modem.setModel(models.get(0));
						}
					} else if (modem.getModemType() == ModemType.ZigBee) {
						map.put("subDeviceType", codeDao.getCodeIdByCode("1.2.1.1"));     // 1.2.1.1 = ZRU
						List<DeviceModel> models = deviceModelDao.getDeviceModels(map);
						if (models.size() == 1) {
							modem.setModel(models.get(0));
						}
					} else if (modem.getModemType() == ModemType.LTE) {
						map.put("subDeviceType", codeDao.getCodeIdByCode("1.2.1.201"));  // 1.2.1.201 = LTE
						List<DeviceModel> models = deviceModelDao.getDeviceModels(map);
						if (models.size() == 1) {
							modem.setModel(models.get(0));
						}
					}
					
					// 새 모뎀 저장
					modemDao.add(modem);

		            EventUtil.sendEvent("Equipment Registration", TargetClass.valueOf(modem.getModemType().name()),
		                    modem.getDeviceSerial(),
		                    new String[][] {{"message", "ModemType[" + modem.getModemType().name() + "] MODEM[" + modemInfo.getDeviceId() + "] on saving metering value"}}
	                );					
				}
				

			} else {
				switch (modemInfo.getModemType()) {
				case LTE:
					((LTE) modem).setFwVer(DataUtil.getString(((DCUInfo) modemInfo).getFwVersion()).trim());
					((LTE) modem).setHwVer(DataUtil.getString(((DCUInfo) modemInfo).getHwVersion()).trim());
					((LTE) modem).setModemTime(DataUtil.getEMnvModemDate((((DCUInfo) modemInfo).getNowTime())).trim());
					((LTE) modem).setRsrp(DataUtil.getString(((DCUInfo) modemInfo).getRsrp()).trim());
					((LTE) modem).setRsrq(DataUtil.getString(((DCUInfo) modemInfo).getRsrq()).trim());
					((LTE) modem).setTxPower(DataUtil.getString(((DCUInfo) modemInfo).getTxPower()).trim());
					((LTE) modem).setPlmn(DataUtil.getString(((DCUInfo) modemInfo).getPlmn()).trim());
					((LTE) modem).setProdMaker(DataUtil.getString(((DCUInfo) modemInfo).getProdCompany()).trim());
					modemProdMaker = ((LTE) modem).getProdMaker();
					((LTE) modem).setProdMakeDate(DataUtil.getString(((DCUInfo) modemInfo).getProdDate()).trim());
					((LTE) modem).setProdSerial(DataUtil.getString(((DCUInfo) modemInfo).getProdNumber()).trim());
					((LTE) modem).setMdPeriod(DataUtil.getIntToByte(((DCUInfo) modemInfo).getMeteringPeriod()[0]));
					((LTE) modem).setPhoneNumber(((DCUInfo) modemInfo).getDeviceId().trim());    
					((LTE) modem).setImei(modemImei);
					
					break;
				case SubGiga:
					((EMnVSubGiga) modem).setFwVer(DataUtil.getString(((SubgigaModemInfo) modemInfo).getFwVersion()).trim());
					((EMnVSubGiga) modem).setHwVer(DataUtil.getString(((SubgigaModemInfo) modemInfo).getHwVersion()).trim());
					((EMnVSubGiga) modem).setEuiId(DataUtil.getString((((SubgigaModemInfo) modemInfo).getEuiId())).trim());
					((EMnVSubGiga) modem).setProdMaker(DataUtil.getString(((SubgigaModemInfo) modemInfo).getProdCompany()).trim());
					modemProdMaker = ((EMnVSubGiga) modem).getProdMaker();
					((EMnVSubGiga) modem).setProdMakeDate(DataUtil.getString(((SubgigaModemInfo) modemInfo).getProdDate()).trim());
					((EMnVSubGiga) modem).setProdSerial(DataUtil.getString(((SubgigaModemInfo) modemInfo).getProdNumber()).trim());
					
					//((LTE) modem).setImei(modemImei);  서브기가도 이게 필요할수있음. 추후 개발하여 적용할것.
					
					break;
				case ZigBee:
					((EMnVZigBee) modem).setFwVer(DataUtil.getString(((ZigBeeModemInfo) modemInfo).getFwVersion()).trim());
					((EMnVZigBee) modem).setHwVer(DataUtil.getString(((ZigBeeModemInfo) modemInfo).getHwVersion()).trim());
					((EMnVZigBee) modem).setChannelId(DataUtil.getIntToByte(((ZigBeeModemInfo) modemInfo).getChannel()[0]));
					((EMnVZigBee) modem).setPanId(DataUtil.getIntToByte(((ZigBeeModemInfo) modemInfo).getPanId()[0]));
					((EMnVZigBee) modem).setProdMaker(DataUtil.getString(((ZigBeeModemInfo) modemInfo).getProdCompany()).trim());
					modemProdMaker = ((EMnVZigBee) modem).getProdMaker();
					((EMnVZigBee) modem).setProdMakeDate(DataUtil.getString(((ZigBeeModemInfo) modemInfo).getProdDate()).trim());
					((EMnVZigBee) modem).setProdSerial(DataUtil.getString(((ZigBeeModemInfo) modemInfo).getProdNumber()).trim());
					
					//((LTE) modem).setImei(modemImei);  지그비도 이게 필요할수있음. 추후 개발하여 적용할것.
					break;
				default:
					break;
				}

				modem.setModemType(modemInfo.getModemType().name());
				modem.setLastLinkTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
				
                String tt = modem.getDeviceSerial();
                log.debug("##### [모뎀검증2]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [현재시간-{}", new Object[]{tt, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim()});
                log.debug("##### [모뎀검증2]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [현재시간-{}", new Object[]{tt, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim()});
                log.debug("##### [모뎀검증2]모뎀 마지막 연결시간 저장 체크 [모뎀={}] [현재시간-{}", new Object[]{tt, DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim()});
                
                log.debug("##### 모뎀 IMEI값 = {}", ((LTE) modem).getImei());
				
				if(modem.getProtocolType() == null){
					modem.setProtocolType(Protocol.SMS.name());
				}
				
				if (mcu != null) {
					if ((modem.getMcu() != null && !mcu.getSysID().equals(modem.getMcu().getSysID())) || modem.getMcu() == null)
						modem.setMcu(mcu);
				}
				
				// 업데이트
				modemDao.update(modem);
			}

		} catch (Exception e) {
			modem = null;
			log.debug("Exception - ", e);
		}
		
		return modem;
	}
	
	private Meter getPreMeter(Modem modem, String typeCheck) throws Exception {
		Meter meter = null;
		if( typeCheck.equals(MeterType.EnergyMeter.name()) )
			meter = new EnergyMeter();
		else if( typeCheck.equals(MeterType.Inverter.name()) )
			meter = new Inverter();
		else if( typeCheck.equals(MeterType.Compensator.name()) )
			meter = new Compensator();
		else
			log.debug("!!!!!!!!!!!!!!!!알수없는 TYPE!!!!!!!!!!!!!!!!");
		meter.setModem(modem);

		return meter;
	}

	/**
	 * 미터 등록 여부 검증. 없으면 자동등록
	 * 
	 * @param parser
	 * @param modem
	 * @param model
	 * @param isInverter
	 * @return
	 */
	private Meter validateMeter(MeterDataParser parser, Modem modem, DeviceModel model, String typeCheck) {
		Meter meter = null;
		
		try {
			meter = parser.getMeter();

			Meter temp = meterDao.get(meter.getMdsId());
			if (temp == null) {
				meter.setInstallDate(modem.getInstallDate());
				meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
				
				
				meter.setMeterType(CommonConstants.getMeterTypeByName(typeCheck));
				meter.setSupplier(modem.getSupplier());
				
				// 모뎀에 Location 설정시 연결된 모든 미터의 Location을 동일하게 변경한다.
				if(modem.getLocation() != null){
					meter.setLocation(modem.getLocation());					
				}
				
				if ((typeCheck.equals(MeterType.EnergyMeter.name())) && (meter.getPulseConstant() == null || meter.getPulseConstant() == 0.0)) {
					meter.setPulseConstant(Double.parseDouble(FMPProperty.getProperty("meter.pulse.constant.hmu")));
				}

				log.debug("Meter Model => {}", model);
				meter.setModel(model);
				meter.setModem(modem);
				
				// 최종통신시간 갱신
				meter.setLastReadDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
				
				log.debug("##### [SAVERMAIN] 임시 통신시간 저장 체크1 [미터={}][현재시간-{}]", meter.getMdsId(), DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
				log.debug("##### [SAVERMAIN] 임시 통신시간 저장 체크1 [미터={}][현재시간-{}]", meter.getMdsId(), DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
				log.debug("##### [SAVERMAIN] 임시 통신시간 저장 체크1 [미터={}][현재시간-{}]", meter.getMdsId(), DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
				
				if (meter.getMdsId() != null && !"".equals(meter.getMdsId())) {
					meterDao.add(meter);

	                EventUtil.sendEvent("Equipment Registration",
	                        TargetClass.valueOf(meter.getMeterType().getName()),
	                        meter.getMdsId(),
	                        new String[][] {{"message",
	                            "MeterType[" + meter.getMeterType().getName() +
	                            "] MCU[" + modem.getMcuId()+
	                            "] MODEM[" + meter.getModem().getDeviceSerial()+ "] on saving metering value"}}
	                );
				}
			} else {
				temp.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name()));
				
				// 모뎀에 Location 설정시 연결된 모든 미터의 Location을 동일하게 변경한다.
				if(modem.getLocation() != null){
					temp.setLocation(modem.getLocation());					
				}
				
				if(meter.getLpInterval() != null){
					temp.setLpInterval(meter.getLpInterval());
				}
				
				if (meter.getPulseConstant() != null && 0.0 < meter.getPulseConstant()) {
					temp.setPulseConstant(meter.getPulseConstant()); 
				}
				
				if(meter.getModemPort() != null && 0 <= meter.getModemPort()){
					temp.setModemPort(meter.getModemPort());					
				}
				
				// 최종통신시간 갱신
				meter.setLastReadDate(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
				
				log.debug("##### [SAVERMAIN] 임시 통신시간 저장 체크2 [미터={}][현재시간-{}]", meter.getMdsId(), DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
				log.debug("##### [SAVERMAIN] 임시 통신시간 저장 체크2 [미터={}][현재시간-{}]", meter.getMdsId(), DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
				log.debug("##### [SAVERMAIN] 임시 통신시간 저장 체크2 [미터={}][현재시간-{}]", meter.getMdsId(), DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss").trim());
				
				
				meterDao.update(temp);
				
				parser.setMeter(temp);
				
				return temp;
			}
		} catch (Exception e) {
			meter = null;
			log.debug("Exception - ", e);
		}


		return meter;
	}

	private void validateRelation(Meter meter, Modem modem) throws Exception {
		Modem orgModem = meter.getModem();

		if (orgModem == null || !orgModem.getDeviceSerial().equals(modem.getDeviceSerial())) {
			meter.setModem(modem);
			if (modem.getModemType() == ModemType.ZRU || modem.getModemType() == ModemType.ZEUPLS || modem.getModemType() == ModemType.LTE) {
				Set<Meter> m = new HashSet<Meter>();
				m.add(meter);
				modem.setMeter(m);

				// 모뎀 교체로 처리한다.
				if (orgModem != null) {
					EventUtil.sendEvent("Equipment Replacement", TargetClass.valueOf(modem.getModemType().name()), modem.getDeviceSerial(), new String[][] { { "equipType", modem.getModemType().name() }, { "oldEquipID", orgModem.getDeviceSerial() }, { "newEquipID", modem.getDeviceSerial() } });
				}
			} else if (modem.getModemType() == ModemType.ZEUMBus || modem.getModemType() == ModemType.SubGiga) {
				Set<Meter> m = modem.getMeter();
				if (m == null) {
					m = new HashSet<Meter>();
				}
				m.add(meter);
				modem.setMeter(m);
			}
			
			modemDao.update(modem);
			meterDao.update(meter);
		}
	}

	private boolean eMnvMDSave(IMeasurementData md) throws Exception {
		boolean result = false;
		DeviceConfig config = null;
		AbstractMDSaver saver = null;

		if (md.getMeterDataParser() == null) {
			log.warn("Parser is not exist");
			return false;
		}

		if (md.getMeterDataParser().getMeter().getModel() != null) {
			config = md.getMeterDataParser().getMeter().getModel().getDeviceConfig();
		}
		if (config == null || (config != null && (config.getSaverName() == null || "".equals(config.getSaverName())))) {
			config = md.getMeterDataParser().getMeter().getModem().getModel().getDeviceConfig();
		}

		log.debug("Saver => [{}}", config.getSaverName());

		saver = (AbstractMDSaver) DataUtil.getBean(Class.forName(config.getSaverName()));

		try {
			result = saver.save(md);
		} catch (Exception e) {
			log.error("Exception - ", e);
		}

		return result;
	}

}
