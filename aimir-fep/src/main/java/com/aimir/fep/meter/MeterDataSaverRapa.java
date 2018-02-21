package com.aimir.fep.meter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.DeviceType;
import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.MeteringType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.SeverityType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.EventAlertDao;
import com.aimir.dao.device.EventAlertLogDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.DeviceVendorDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.meter.data.BillingData;
import com.aimir.fep.meter.entry.IMeasurementData;
import com.aimir.fep.protocol.rapa.BillDumpPacket;
import com.aimir.fep.protocol.rapa.BillingFrame;
import com.aimir.fep.protocol.rapa.EventDumpPacket;
import com.aimir.fep.protocol.rapa.EventFrame;
import com.aimir.fep.protocol.rapa.GwRosterDetailVo;
import com.aimir.fep.protocol.rapa.LPDumpPacket;
import com.aimir.fep.protocol.rapa.LPFrame;
import com.aimir.fep.protocol.rapa.MessageBody;
import com.aimir.fep.protocol.rapa.ReportFrame;
import com.aimir.fep.protocol.rapa.SUBBillDataFrame;
import com.aimir.fep.protocol.rapa.SUBLPCurrentMeterDataFrame;
import com.aimir.fep.protocol.rapa.SUBLPDataFrame;
import com.aimir.fep.protocol.rapa.parser.RapaMeter;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.LTE;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.DeviceVendor;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * class MeterDataSaverRapa.java
 *
 * @author ky.yoon@nuritelecom.com
 * @version 1.0 $Date: 2016. 11. 16.
 */
@Service
@Transactional(value = "transactionManager")
public class MeterDataSaverRapa extends AbstractMDSaver {
	
	protected static Log log = LogFactory.getLog(MeterDataSaverRapa.class);
	private static List<String> isProcessMeter = null;
	private String supplierName = "RAPA";

	static {
		if (isProcessMeter == null)
			isProcessMeter = new ArrayList<String>();
	}

	@Autowired
	private SupplierDao supplierDao;

	@Autowired
	ModemDao modemDao;

	@Autowired
	EventAlertDao eaDao;

	@Autowired
	EventAlertLogDao eaLogDao;
	
	@Autowired
	DeviceVendorDao deviceVendorDao;

	/**
	 * save
	 * 
	 * @throws Exception
	 */
	public void save(ReportFrame reportFrame) throws Exception {

		RapaMeter parser = new RapaMeter();

		parser.setMeterType(reportFrame.getMeterType());
		parser.setSclId(reportFrame.getSclId());
		parser.setLocTargetDevice(reportFrame.getLocTargetDevice());
		parser.parse(reportFrame.getMessageBody());

		MessageBody mdata = parser.getMessageBody();

		if (mdata instanceof LPFrame) {

			log.debug("METER TYPE : LPFrame");
			LPFrame lpFrame = (LPFrame) mdata;
			save(lpFrame);

		} else if (mdata instanceof BillingFrame) {

			log.debug("METER TYPE : BillingFrame");
			BillingFrame billingFrame = (BillingFrame) mdata;
			save(billingFrame);

		} else if (mdata instanceof EventFrame) {

			log.debug("METER TYPE : EventFrame");
			EventFrame eventFrame = (EventFrame) mdata;
			save(eventFrame);
		}
	}

	/**
	 * save
	 * 
	 * @param LPFrame
	 * @throws Exception
	 */
	private void save(LPFrame frame) throws Exception {
		
		try {			
			log.debug("[LPFrame] save START");
			
			if (frame != null) {
				// modem check
				String deviceSerial = frame.getSclId();
				String locTargetDevice = frame.getLocTargetDevice();
				

				log.debug("[LPFrame] DUMP PACKET COUNT => " + frame.getLPDumpPacketList().size());

				for (LPDumpPacket dp : frame.getLPDumpPacketList()) {

					String meterId = dp.getMeterId();

					log.debug("[LPFrame] 현재검침 COUNT => " + dp.getdCountVal());

					for (SUBLPCurrentMeterDataFrame sdf : dp.getSubLPCurrentMeterDataFrameList()) {

						String meteringDate = sdf.getStrMTime().substring(0, 8); // 년월일
						String meteringTime = sdf.getStrMTime().substring(10, 16); // 시분초
						String meteringDateTime = meteringDate + meteringTime;
						String lastReadDate = meteringDateTime;
						double meteringValue = sdf.getApVal();

						log.debug("[LPFrame] 현재검침 SAVE => meterId : " + meterId + " / deviceSerial : " + deviceSerial + " locTargetDevice : " + locTargetDevice
								+ " / meteringDate : " + meteringDate + " / meteringTime : " + meteringTime
								+ " / AP : " + meteringValue);
						
						// modem
						LTE modem = validateModem(deviceSerial, locTargetDevice, null, lastReadDate);

						// meter
						EnergyMeter meter = validateMeter(meterId, modem, lastReadDate);

						// 검침데이터 save
						saveMeteringData(MeteringType.Normal, meteringDate, meteringTime, meteringValue, meter,
								DeviceType.Modem, deviceSerial, DeviceType.Meter, meterId, null);

					}

					log.debug("[LPFrame] LP 검침 COUNT => " + dp.getlCountVal());

					for (SUBLPDataFrame sdf : dp.getSubLPDataFrameList()) {

						String meteringDate = sdf.getStrMTime().substring(0, 8); // 년월일
						String meteringTime = sdf.getStrMTime().substring(10, 16); // 시분초
						String meteringDateTime = meteringDate + meteringTime;
						String lastReadDate = meteringDateTime;
						double baseValue = 0;

						double[][] lplist = new double[2][1];
						lplist[0][0] = sdf.getFapVal();
						lplist[1][0] = sdf.getWcVal();

						int[] flaglist = { 0, 0 };
						
						// modem
						LTE modem = validateModem(deviceSerial, locTargetDevice, null, lastReadDate);
						
						// meter update
						EnergyMeter meter = validateMeter(meterId, modem, lastReadDate);

						log.debug("[LPFrame] LP 검침 SAVE => meterId : " + meterId + " deviceSerial : " + deviceSerial + " locTargetDevice : " + locTargetDevice
								+ "/ meteringDate : " + meteringDate + " / meteringTime : " + meteringTime 
								+ " / FAP : " + sdf.getFapVal() + " / WC : " + sdf.getWcVal());

						// LP데이터 save
						saveLPData(MeteringType.Manual, meteringDate, meteringTime, lplist, flaglist, baseValue, meter,
								DeviceType.Modem, deviceSerial, DeviceType.Meter, meterId);
					}
				} // end for
			}

			log.debug("[LPFrame] save END");

		} catch (Exception e) {			
			log.error("[LPFrame] save failed : " + e, e);
			throw e;
		}
	}

	/**
	 * save
	 * 
	 * @param BillingFrame
	 * @throws Exception
	 */
	private void save(BillingFrame frame) throws Exception {

		try {
			
			log.debug("[BillingFrame] save START");
			
			if (frame != null) {

				String deviceSerial = frame.getSclId();
				String locTargetDevice = frame.getLocTargetDevice();
				
				log.debug("[BillingFrame] DUMP PACKET COUNT => " + frame.getBillDumpPacketList().size());
				
				for (BillDumpPacket dp : frame.getBillDumpPacketList()) {

					String meterId = dp.getMeterId();
					
					log.debug("[BillingFrame] SUB DUMP PACKET COUNT => " + dp.getdCountVal());
					
					for (SUBBillDataFrame sdf : dp.getSubBillDataFrame()) {

						String meteringDate = sdf.getStrMTime().substring(0, 8); // 년월일
						String meteringTime = sdf.getStrMTime().substring(10, 16); // 시분초
						String meteringDateTime = meteringDate + meteringTime;
						String lastReadDate = meteringDateTime;

						BillingData billData = new BillingData();
						billData.setActiveEnergyRateTotal(sdf.getSapVal());
						billData.setBillingTimestamp(meteringDate);
						
						LTE modem = validateModem(deviceSerial, locTargetDevice, null, lastReadDate);
						// meter
						EnergyMeter meter = validateMeter(meterId, modem, lastReadDate);
						
						log.debug("[BillingFrame] SAVE => meterId : " + meterId + " deviceSerial : " + deviceSerial + " locTargetDevice : " + locTargetDevice
								+ "/ meteringDate : " + meteringDate + " / meteringTime : " + meteringTime 
								+ " / SAP : " + sdf.getSapVal());

						saveDailyBilling(billData, meter, null, null, DeviceType.Meter, meterId);
					}
				}
			}
			
			log.debug("[BillingFrame] save END");
			
		} catch (Exception e) {			
			log.error("[LPFrame] save failed : " + e, e);
			throw e;
		}
	}

	/**
	 * save
	 * 
	 * @param EventFrame
	 * @throws Exception
	 */
	private void save(EventFrame frame) throws Exception {

		try {
			log.debug("[EventFrame] save START");
			
			if (frame != null) {

				String deviceSerial = frame.getSclId();
				String locTargetDevice = frame.getLocTargetDevice();
				
				log.debug("[EventFrame] DUMP PACKET COUNT => " + frame.getEventDumpPacketList().size());
				
				for (EventDumpPacket dp : frame.getEventDumpPacketList()) {

					String openTime = dp.getStrCTime().substring(0, 8) + "" + dp.getStrCTime().substring(10, 16); // 정전발생시간
					
					// 미터등록
					String meteringDate = dp.getStrMTime().substring(0, 8); // 년월일
					String meteringTime = dp.getStrMTime().substring(10, 16); // 시분초
					String meteringDateTime = meteringDate + meteringTime;

					// 모뎀등록
					LTE modem = validateModem(deviceSerial, locTargetDevice, null, meteringDateTime);
					
					validateMeter(dp.getMeterId(), modem, meteringDateTime);

					log.debug("[EventFrame] SAVE => meterId : " + dp.getMeterId() + " deviceSerial : " + deviceSerial + " locTargetDevice : " + locTargetDevice
							+ "/ meteringDate : " + meteringDate + " / meteringTime : " + meteringTime 
							+ " / openTime : " + openTime);
					
					// 이벤트로그 등록
					saveEventAlertLog(dp.getMeterId(), openTime);
				}
			}
			
			log.debug("[EventFrame] save END");
			
		} catch (Exception e) {
			
			log.error("[EventFrame] save failed : " + e, e);
			throw e;
		}
	}
	
	/**
	 * save
	 * 
	 * @param GwRosterDetailVo
	 * @throws Exception
	 */
	public void save(GwRosterDetailVo vo) throws Exception {
		
		try {
			
			String deviceSerial = vo.getId(); //sclid
			String locTargetDevice = vo.getLocTargetDevice();
			String installDate = vo.getCreationTime().replaceAll("-", "").replaceAll(":", "").replaceAll("T", "");
			
			log.debug("[save] MODEM SAVE START");
			
			validateModem(deviceSerial, locTargetDevice, installDate, null);
			
			log.debug("[save] MODEM SAVE END");
		} catch(Exception e) {
			log.error("[EventFrame] save failed : " + e, e);
			throw e;
		}	
	}

	/**
	 * saveEventAlertLog
	 * 
	 * @param mdevId
	 * @param opentime
	 */
	private void saveEventAlertLog(String mdevId, String opentime) {

		log.debug("saveEventAlertLog IN ");

		try {

			EventAlertLog event = new EventAlertLog();

			event.setOpenTime(opentime);
			event.setWriteTime(DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss"));

			event.setId(TimeUtil.getCurrentLongTime());
			event.setActivatorId(mdevId);
			event.setActivatorType(TargetClass.EnergyMeter);
			event.setOccurCnt(1);
			event.setSeverity(SeverityType.Major);
			event.setStatus(EventStatus.Open);

			Supplier supplier = new Supplier();
			Location location = new Location();

			Meter mt = meterDao.get(mdevId);
			supplier = mt.getSupplier() != null ? mt.getSupplier() : mt.getModem().getSupplier();
			location = mt.getLocation() != null ? mt.getLocation() : mt.getModem().getLocation();

			event.setLocation(location);
			event.setSupplier(supplier);

			EventAlert ea = eaDao.findByCondition("name", "Power Alarm");
			event.setEventAlert(ea);
			event.setMessage(ea.getMsgPattern());

			log.debug("Meter Event -> Event Alert! : " + event.getMessage());

			// save
			eaLogDao.add(event);

		} catch (Exception e) {
			
			log.error("[saveEventAlertLog] save failed : " + e, e);
		}
	}

	/**
	 * modem 유무 체크 후 정보 업데이트
	 * 
	 * @param deviceSerial
	 * @return
	 */
	private LTE validateModem(String deviceSerial, String locTargetDevice) throws Exception {

		return validateModem(deviceSerial, locTargetDevice, null, null); 
		
	}
	
	/**
	 * modem 유무 체크 후 정보 업데이트
	 * 
	 * @param deviceSerial
	 * @return
	 */
	private LTE validateModem(String deviceSerial, String locTargetDevice, String installDate, String meteringDateTime) throws Exception {

		LTE modem = null;
		
		log.debug("[validateModem] deviceSerial : " + deviceSerial);
		
		try {
			modem = (LTE) modemDao.get(deviceSerial);

			if (modem == null) {
				
				log.debug("[validateModem] modemDao add");
				modem = new LTE();
				
				modem.setDeviceSerial(deviceSerial); // deviceServial(locTargetDevice)
				modem.setPhoneNumber(locTargetDevice);
				modem.setMacAddr(locTargetDevice);
				modem.setModemType(ModemType.LTE.name());
				
				// 공급사
				Supplier supplier = supplierDao.getSupplierByName(supplierName);
				modem.setSupplier(supplier);
				modem.setSupplierId(supplier.getId());
				
				if(installDate != null) {
					modem.setInstallDate(installDate);
				}
				if(meteringDateTime != null) {
					modem.setLastLinkTime(meteringDateTime);
				}
				modem = (LTE) modemDao.add(modem);
				
			} else {
				
				log.debug("[validateModem] modemDao update");
				
				modem.setDeviceSerial(deviceSerial); // deviceServial(locTargetDevice)
				modem.setPhoneNumber(locTargetDevice);
				modem.setMacAddr(locTargetDevice);
				modem.setModemType(ModemType.LTE.name());
				
				// 공급사
				Supplier supplier = supplierDao.getSupplierByName(supplierName);
				modem.setSupplier(supplier);
				modem.setSupplierId(supplier.getId());
				
				if(installDate != null) {
					modem.setInstallDate(installDate);
				}
				if(meteringDateTime != null) {
					modem.setLastLinkTime(meteringDateTime);
				}
				
				modem = (LTE) modemDao.update(modem);
			}

		} catch (Exception e) {			
			log.error("[validateModem] save failed : " + e, e);
			throw e;
		}

		return modem;
	}

	/**
	 * modem 유무 체크 후 정보 업데이트
	 * 
	 * @param deviceSerial
	 * @return
	 */
	private EnergyMeter validateMeter(String mdsId, Modem modem, String meteringDateTime) throws Exception {

		EnergyMeter meter = null;
		
		log.debug("[validateMeter] mdsId : " + mdsId + " / meteringDateTime : " + meteringDateTime);
		
		try {
			meter = (EnergyMeter) meterDao.get(mdsId);

			int lpInterval = 15; // 15분 확인 할것!!(메일)

			if (meter == null) {
				
				log.debug("[validateMeter] meterDao add");
				meter = new EnergyMeter();

				meter.setMdsId(mdsId);
				meter.setLpInterval(lpInterval);
				meter.setWriteDate(DateTimeUtil.getDateString(new Date()));
				meter.setMeterType(CommonConstants.getMeterTypeByName(MeterType.EnergyMeter.name())); // 미터타입
				meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.Normal.name())); // 미터상태
				meter.setModem(modem);

				meter.setLastReadDate(meteringDateTime);

				Supplier supplier = supplierDao.getSupplierByName(supplierName);

				if (supplier != null) {
					meter.setSupplier(supplier);
					meter.setSupplierId(supplier.getId());
				}
				
				List<DeviceVendor> vendorList = deviceVendorDao.getAll();
				
				if(vendorList != null) {					
					
					if(vendorList.size() > 0) { 
						DeviceVendor vendor = vendorList.get(0); 
						List<DeviceModel> model = vendor.getDeviceModels();
						
						if(model != null) {
							if(model.size() > 0) meter.setModel(model.get(0));
						}
					}
				}
				
				meter.setModem(modem);
				meter = (EnergyMeter) meterDao.add(meter);

			} else {
				
				log.debug("[validateMeter] meterDao update");
				
				meter.setMdsId(mdsId);
				meter.setLastReadDate(meteringDateTime);
				meter.setModem(modem); // modem이 변경될 수 있음
				meter = (EnergyMeter) meterDao.update(meter);
			}

		} catch (Exception e) {			
			log.error("[validateMeter] save failed : " + e, e);
			throw e;
		}

		return meter;
	}

	@Override
	protected boolean save(IMeasurementData md) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}