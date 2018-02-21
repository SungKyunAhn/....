package com.aimir.fep.trap.actions.GV;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.MeterStatus;
import com.aimir.constants.CommonConstants.MeterType;
import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.device.MeterDao_MOE;
import com.aimir.dao.device.ModemDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EnergyMeter;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/*
 * Event ID : GV_200.0.0 (Vietnam GPRS Modem Install)
 * <br>GPRS Modem
 *
 *
 *
    2016-12-24 00:02:10,012 INFO  [com.aimir.fep.trap.common.EventMaker.getEventAlertLog(EventMaker.java:92)]  - Before Event [GD_200.1.0]
	2016-12-24 00:02:10,014 INFO  [com.aimir.fep.trap.common.EventMaker.getEventAlertLog(EventMaker.java:103)]  - After Event: EventAlert [autoClosed=null, descr=Equipment installation, eventAlertType=Event, id=26, monitor=SaveAndMonitor, msgPattern=Install, name=Equipment Installation, oid=200.1.0,203.8.0,201.1.0,203.105.0,GD_200.1.0,NG_220.1.0,NG_200.1.0, severity=Information, timeout=840, troubleAdvice=null]
	2016-12-24 00:02:10,025 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.3]
	2016-12-24 00:02:10,026 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.3] NAME[ResetInterval] VALUE[1440]
	2016-12-24 00:02:10,026 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.4]
	2016-12-24 00:02:10,026 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.4] NAME[MeteringInterval] VALUE[360]
	2016-12-24 00:02:10,026 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.1]
	2016-12-24 00:02:10,026 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.1] NAME[EUI] VALUE[3572470588281340]
	2016-12-24 00:02:10,026 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.2]
	2016-12-24 00:02:10,027 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.2] NAME[Time] VALUE[20161224000140]
	2016-12-24 00:02:10,027 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.7]
	2016-12-24 00:02:10,027 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.7] NAME[APNAddress] VALUE[net.asiacell.com]
	2016-12-24 00:02:10,027 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:213)]  - oid length great than 3 : 200.1.7.hex
	2016-12-24 00:02:10,027 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:216)]  - after make normal oid value : 200.1.7
	2016-12-24 00:02:10,027 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.7]
	2016-12-24 00:02:10,028 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.7] NAME[APNAddress.hex] VALUE[6E65742E6173696163656C6C2E636F6D]
	2016-12-24 00:02:10,028 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.8]
	2016-12-24 00:02:10,028 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.8] NAME[APNID] VALUE[]
	2016-12-24 00:02:10,028 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.5]
	2016-12-24 00:02:10,028 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.5] NAME[ServerIP] VALUE[106.250.189.44]
	2016-12-24 00:02:10,028 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.6]
	2016-12-24 00:02:10,029 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.6] NAME[ServerPort] VALUE[8000]
	2016-12-24 00:02:10,029 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.15]
	2016-12-24 00:02:10,029 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.15] NAME[PhoneNumber] VALUE[]
	2016-12-24 00:02:10,029 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.14]
	2016-12-24 00:02:10,029 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.14] NAME[SimIMSI] VALUE[357247058828134]
	2016-12-24 00:02:10,029 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:213)]  - oid length great than 3 : 200.1.14.hex
	2016-12-24 00:02:10,030 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:216)]  - after make normal oid value : 200.1.14
	2016-12-24 00:02:10,030 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.14]
	2016-12-24 00:02:10,030 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.14] NAME[SimIMSI.hex] VALUE[3335373234373035383832383133340D]
	2016-12-24 00:02:10,030 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.17]
	2016-12-24 00:02:10,030 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.17] NAME[MeterCommStatus] VALUE[0]
	2016-12-24 00:02:10,030 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.16]
	2016-12-24 00:02:10,031 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.16] NAME[MeterID] VALUE[201507004262]
	2016-12-24 00:02:10,031 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:213)]  - oid length great than 3 : 200.1.16.hex
	2016-12-24 00:02:10,031 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:216)]  - after make normal oid value : 200.1.16
	2016-12-24 00:02:10,031 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.16]
	2016-12-24 00:02:10,031 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.16] NAME[MeterID.hex] VALUE[3230313530373030343236320000000000000000]
	2016-12-24 00:02:10,032 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.11]
	2016-12-24 00:02:10,032 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.11] NAME[FwVer] VALUE[257]
	2016-12-24 00:02:10,032 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.10]
	2016-12-24 00:02:10,032 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.10] NAME[ModelName] VALUE[NAMR-P402GP]
	2016-12-24 00:02:10,032 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:213)]  - oid length great than 3 : 200.1.10.hex
	2016-12-24 00:02:10,033 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:216)]  - after make normal oid value : 200.1.10
	2016-12-24 00:02:10,033 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.10]
	2016-12-24 00:02:10,033 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.10] NAME[ModelName.hex] VALUE[4E414D522D503430324750]
	2016-12-24 00:02:10,033 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.13]
	2016-12-24 00:02:10,033 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.13] NAME[HwVer] VALUE[6]
	2016-12-24 00:02:10,033 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.12]
	2016-12-24 00:02:10,033 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.12] NAME[BuildNumber] VALUE[3]
	2016-12-24 00:02:10,033 INFO  [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:221)]  - CODE=[GD_200.1.0] OID=200.1.9]
	2016-12-24 00:02:10,033 DEBUG [com.aimir.fep.trap.common.EventMaker.makeEventAlertAttr(EventMaker.java:229)]  - OID[200.1.9] NAME[APNPassword] VALUE[]
	2016-12-24 00:02:10,037 DEBUG [com.aimir.fep.trap.common.FMPEventTask.access(FMPEventTask.java:82)]  - Event : {"id":null,"EventAlert":26,"eventAlertName":"Equipment Installation","severity":"Information","supplier":1,"duration":"","message":"","status":"Open","occurCnt":1,"location":"DCU01","activatorType":"MMIU","activatorIp":"","activatorId":"3572470588281340","openTime":"12/24/16 12:01:40 AM","closeTime":"","writeTime":"12/24/16 12:02:10 AM"}
	2016-12-24 00:02:10,037 DEBUG [com.aimir.fep.trap.common.FMPEventTask.access(FMPEventTask.java:83)]  - EventAlert : EventAlert [autoClosed=null, descr=Equipment installation, eventAlertType=Event, id=26, monitor=SaveAndMonitor, msgPattern=Install, name=Equipment Installation, oid=200.1.0,203.8.0,201.1.0,203.105.0,GD_200.1.0,NG_220.1.0,NG_200.1.0, severity=Information, timeout=840, troubleAdvice=null]
	2016-12-24 00:02:10,037 DEBUG [com.aimir.fep.trap.common.FMPEventTask.access(FMPEventTask.java:95)]  - Event task Instance Class name[com.aimir.fep.trap.actions.EV_GD_200_1_0_Action]
	2016-12-24 00:02:10,037 DEBUG [com.aimir.fep.trap.actions.EV_GD_200_1_0_Action.execute(EV_GD_200_1_0_Action.java:86)]  - EventName[eventModemInstall]  EventCode[GD_200.1.0] Modem[3572470588281340]
	2016-12-24 00:02:10,042 INFO  [com.aimir.fep.trap.actions.EV_GD_200_1_0_Action.execute(EV_GD_200_1_0_Action.java:194)]  - Update modem=3572470588281340
	2016-12-24 00:02:10,058 INFO  [com.aimir.fep.trap.actions.EV_GD_200_1_0_Action.execute(EV_GD_200_1_0_Action.java:256)]  - Meter Install=201507004262
	2016-12-24 00:02:10,058 DEBUG [com.aimir.fep.trap.common.FMPEventTask.access(FMPEventTask.java:110)]  - EventAlert : EventAlert [autoClosed=null, descr=Equipment installation, eventAlertType=Event, id=26, monitor=SaveAndMonitor, msgPattern=Install, name=Equipment Installation, oid=200.1.0,203.8.0,201.1.0,203.105.0,GD_200.1.0,NG_220.1.0,NG_200.1.0, severity=Information, timeout=840, troubleAdvice=null]
	2016-12-24 00:02:10,059 DEBUG [com.aimir.fep.trap.common.FMPEventTask.access(FMPEventTask.java:172)]  - ### Event : {"id":1482526930043,"EventAlert":14,"eventAlertName":"Equipment Registration","severity":"Information","supplier":1,"duration":"","message":"Equipment Registration","status":"Open","occurCnt":1,"location":"DCU01","activatorType":"MMIU","activatorIp":"201507004262","activatorId":"3572470588281340","openTime":"12/24/16 12:02:10 AM","closeTime":"","writeTime":"12/24/16 12:02:10 AM"}
	2016-12-24 00:02:10,060 DEBUG [com.aimir.fep.trap.common.FMPEventTask.access(FMPEventTask.java:180)]  - Event : {"id":1482526930043,"EventAlert":14,"eventAlertName":"Equipment Registration","severity":"Information","supplier":1,"duration":"","message":"Equipment Registration","status":"Open","occurCnt":1,"location":"DCU01","activatorType":"MMIU","activatorIp":"201507004262","activatorId":"3572470588281340","openTime":"12/24/16 12:02:10 AM","closeTime":"","writeTime":"12/24/16 12:02:10 AM"}
 *
 */
@Component
public class EV_GV_200_1_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_GV_200_1_0_Action.class);

	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	@Autowired
	ModemDao modemDao;

	@Autowired
	MeterDao meterDao;

	@Autowired
	MeterDao_MOE meterDao_MOE;

	@Autowired
	CodeDao codeDao;

	@Autowired
	CommandGW commandGW;

	@Autowired
	ChangeLogDao clDao;

	@Autowired
	DeviceModelDao deviceModelDao;

	@Autowired
	SupplierDao supplierDao;

	@Autowired
	LocationDao locationDao;

	/**
	 * execute event action
	 *
	 * @param trap
	 *            - FMP Trap(GPRS Modem Event)
	 * @param event
	 *            - Event Alert Log Data
	 */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("EventName[eventModemInstall] " + " EventCode[" + trap.getCode() + "] Modem[" + trap.getSourceId() + "]");
		String currentTime = TimeUtil.getCurrentTimeMilli();
		String commDate = trap.getTimeStamp();
		String newModemId = event.getEventAttrValue("EUI");
		String installedDate = event.getEventAttrValue("Time");

		if (installedDate == null || "".equals(installedDate) || !installedDate.subSequence(0, 4).equals(TimeUtil.getCurrentTimeMilli().substring(0, 4))) {
			installedDate = currentTime;
		}

		String nameSpace = "";
		if (trap.getCode().indexOf("_") > 0) {
			nameSpace = trap.getCode().substring(0, 2);
		}
		int resetInterval = Integer.parseInt(event.getEventAttrValue("ResetInterval"));
		int meteringInterval = Integer.parseInt(event.getEventAttrValue("MeteringInterval"));
		String serverIp = event.getEventAttrValue("ServerIP");
		String serverPort = event.getEventAttrValue("ServerPort");
		String apnAddress = event.getEventAttrValue("APNAddress");
		String apnId = event.getEventAttrValue("APNID");
		String apnPassword = event.getEventAttrValue("APNPassword");
		String modelName = event.getEventAttrValue("ModelName");
		String fwVer = event.getEventAttrValue("FwVer");
		String buildNumber = event.getEventAttrValue("BuildNumber");
		String hwVer = event.getEventAttrValue("HwVer");
		String simNumber = event.getEventAttrValue("SimIMSI");
		String phoneNumber = event.getEventAttrValue("PhoneNumber");
		String meterId = event.getEventAttrValue("MeterID");
		int commStatus = Integer.parseInt((event.getEventAttrValue("MeterCommStatus") == null) ? "0" : (event.getEventAttrValue("MeterCommStatus")));
		String meterModel = event.getEventAttrValue("MeterModel");
		String ipAddr = trap.getIpAddr();
		MeterType meterType = MeterType.EnergyMeter;
		ModemType modemType = ModemType.MMIU;

		DeviceModel modemModel = deviceModelDao.findByCondition("name", modelName);
		if (fwVer != null && !"".equals(fwVer)) {
			fwVer = DataUtil.getVersionString(Integer.parseInt(fwVer));
		}
		if (hwVer != null && !"".equals(hwVer)) {
			hwVer = DataUtil.getVersionString(Integer.parseInt(hwVer));
		}
		if (buildNumber != null && !"".equals(buildNumber)) {
			buildNumber = DataUtil.getVersionString(Integer.parseInt(buildNumber));
		}
		
		TransactionStatus txstatus = null;

		try {
			txstatus = txmanager.getTransaction(null);

			// get modem
			Supplier supplier = supplierDao.getAll().get(0);

			MMIU modem = (MMIU) modemDao.get(newModemId);
			Meter meter = null;
			if (modem == null) {

				modem = new MMIU();
				modem.setDeviceSerial(newModemId);
				modem.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), installedDate));
				modem.setSupplier(supplier);
				modem.setModemType(modemType.name());
				modem.setLocation(locationDao.getAll().get(0));
				if (modemModel != null) {
					modem.setModel(modemModel);
				}
				if (phoneNumber != null && !"".equals(phoneNumber))
					modem.setPhoneNumber(phoneNumber);
				modem.setSimNumber(simNumber);
				modem.setFwRevision(buildNumber);
				modem.setFwVer(fwVer);
				modem.setHwVer(hwVer);
				modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), commDate));
				modem.setProtocolVersion(trap.getProtocolVersion());
				//modem.setProtocolType(Protocol.SMS.name());
				modem.setProtocolType(Protocol.GPRS.name());
				modem.setNameSpace(nameSpace);
				modem.setResetInterval(resetInterval);
				modem.setMeteringInterval(meteringInterval);
				modem.setApnAddress(apnAddress);
				modem.setApnId(apnId);
				modem.setApnPassword(apnPassword);
				modem.setIpAddr(ipAddr);
				modemDao.add(modem);
				log.info("Add modem=" + newModemId);
			} else {
				if (modemModel != null) {
					modem.setModel(modemModel);
				}
				if (phoneNumber != null && !"".equals(phoneNumber))
					modem.setPhoneNumber(phoneNumber);
				modem.setSimNumber(simNumber);
				modem.setFwRevision(buildNumber);
				modem.setFwVer(fwVer);
				modem.setHwVer(hwVer);
				modem.setLastLinkTime(DateTimeUtil.getDST(supplier.getTimezone().getName(), commDate));
				modem.setProtocolVersion(trap.getProtocolVersion());
				modem.setProtocolType(Protocol.GPRS.name());
				modem.setNameSpace(nameSpace);
				modem.setResetInterval(resetInterval);
				modem.setMeteringInterval(meteringInterval);
				modem.setApnAddress(apnAddress);
				modem.setApnId(apnId);
				modem.setApnPassword(apnPassword);
				modem.setIpAddr(ipAddr);
				modemDao.update(modem);
				log.info("Update modem=" + newModemId);

				EventUtil.sendEvent("Equipment Registration", TargetClass.MMIU, modem.getDeviceSerial(), currentTime, new String[][] {}, event);
			}

			if (meterId != null && !"".equals(meterId)) {

				//                meter = meterDao.get(meterId);
				//                
				//                if (meter != null && !meter.getMdsId().equals(meterId)) {
				//                    meter.setModem(null);
				//                }

				// 미터의 모뎀과 입력받은 모뎀이 다르면 관계를 생성한다.
				meter = meterDao.get(meterId);
				if (meter != null) {
					if (meter.getModem() != null) {
						if (!modem.getDeviceSerial().equals(meter.getModem().getDeviceSerial())) {
							meter.setModem(modem);
						}
					} else {
						meter.setModem(modem);
					}

					EventUtil.sendEvent("Equipment Registration", TargetClass.valueOf(meterType.name()), meterId, currentTime, new String[][] {}, event);
				} else {
					/*
					 * Licence check
					 */
					if (licenceCheck(meterId, modem.getDeviceSerial(), supplier) != 1) {
						meter = new EnergyMeter();
						meter.setMdsId(meterId);
						meter.setInstallDate(DateTimeUtil.getDST(supplier.getTimezone().getName(), installedDate));
						meter.setMeterType(CommonConstants.getMeterTypeByName(meterType.name()));
						meter.setLocation(locationDao.getAll().get(0));
						meter.setSupplier(supplier);
						meter.setModem(modem);
						meter.setMeterStatus(CommonConstants.getMeterStatusByName(MeterStatus.NewRegistered.name()));
						EventUtil.sendEvent("Equipment Installation", TargetClass.valueOf(meterType.name()), meterId, currentTime, new String[][] {}, event);
					}
				}

				if (meter != null) {

					if (meterModel != null) {
						DeviceModel mModel = deviceModelDao.findByCondition("name", meterModel);
						if (mModel != null) {
							meter.setModel(mModel);
						}
					}

					meter.setLpInterval(60);
					meterDao.saveOrUpdate(meter);
					log.info("Meter Install=" + meterId);
				}

			} else
				log.warn("Meter of Modem[" + newModemId + "] is NULL!");

			event.setActivatorId(newModemId);
			event.setActivatorType(TargetClass.MMIU);
			event.append(EventUtil.makeEventAlertAttr("modemID", "java.lang.String", newModemId));
			event.append(EventUtil.makeEventAlertAttr("message", "java.lang.String", "Modem is connected with a Meter[" + modelName + "," + meterId + "]"));

			txmanager.commit(txstatus);
		} catch (Exception e) {
			log.error(e, e);
			if (txstatus != null)
				txmanager.rollback(txstatus);
			throw e;
		}
	}

	private int licenceCheck(String meterSerial, String modemSerial, Supplier supplier) {

		int limitedCount = supplier.getLicenceMeterCount();
		String eventAlertName = "Excessive Number of Device Registration";
		String activatorType = TargetClass.EnergyMeter.name();
		String activatorId = " ";

		if (supplier.getLicenceUse() == 1) {
			int currentMeterCount = meterDao.getTotalMeterCount();

			if (!(currentMeterCount < limitedCount)) {
				EventUtil.sendEvent(eventAlertName, activatorType, activatorId, supplier);

				log.warn("##################################################################################");
				log.warn("Excessive Number of Device Registration. (limited quantity : " + limitedCount + ") - Meter=" + meterSerial + ", Modem=" + modemSerial);
				log.warn("##################################################################################");
			}
		}

		return supplier.getLicenceUse();
	}

}
