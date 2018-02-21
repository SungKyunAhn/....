package com.aimir.fep.trap.actions.NG;

import java.util.Hashtable;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants;
import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.ChangeLogDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;
import com.aimir.dao.system.CodeDao;
import com.aimir.dao.system.DeviceModelDao;
import com.aimir.dao.system.LocationDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.system.Code;
import com.aimir.model.system.DeviceModel;
import com.aimir.model.system.Supplier;
import com.aimir.notification.FMPTrap;
import com.aimir.util.StringUtil;
import com.aimir.util.TimeUtil;

/**
 * Event ID : NG_200.1.0 (DCU Install) <br>
 * PLC-G3 DCU
 *
 * @author goodjob
 * @version $Rev: 1 $, $Date: 2015-07-08 10:00:00 +0900 $,
 */
@Component
public class EV_NG_200_1_0_Action implements EV_Action {
	private static Log log = LogFactory.getLog(EV_NG_200_1_0_Action.class);

	@Resource(name = "transactionManager")
	JpaTransactionManager txmanager;

	@Autowired
	MCUDao dcuDao;

	@Autowired
	MeterDao meterDao;

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

    /*
     * execute event action
     *
     * @param trap - FMP Trap(DCU Event)
     * @param event - Event Alert Log Data
     * 
     * 2.1.1	sysID	STRING	32			DCU ID
     * 2.1.2	sysType	UINT	4			DCU Type (표 3)
     * 2.1.3	sysName	STRING	64			DCU 명칭 (관리용 명칭)
     * 2.1.4	sysLocation	STRING	64			DCU 위치
     * 2.1.5	sysContact	STRING	64			DCU 담당자
     * 2.1.6	sysModel	STRING	32			DCU HW Model Name
     * 2.1.7	sysHwVersion	WORD	2			DCU HW Version (상위 Byte : Major, 하위 Byte : Minor)
     * 2.1.8	sysHwBuild	BYTE	1			DCU HW Build Number
     * 2.1.9	sysSwVersion	WORD	2			DCU SW Version (상위 Byte : Major, 하위 Byte : Minor)
     * 2.1.10	sysSwRevision	UINT	4			DCU SW Build Number
     * 2.1.11	sysPort	UINT	4			DCU Listen port number
     * 2.5.7    ntwEthPhy BYTE 6 Ethernet physical address  (2016.02.26 추가. 사업부요청)
     */
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("EventName[eventDcuInstall] " + " EventCode[" + trap.getCode() + "] Modem[" + trap.getSourceId() + "]");

		// Initialize
		//String mcuId = trap.getMcuId();
		String ipAddr = trap.getIpAddr();

		String sysID = trap.getSourceId();
		// int sysType = DataUtil.(event.getEventAttrValue("sysType"));
		String sysName = event.getEventAttrValue("sysName");
		String sysLocation = event.getEventAttrValue("sysLocation");
		// String sysContact = event.getEventAttrValue("sysContact");
		String sysModel = event.getEventAttrValue("sysModel");
		String sysHwVersion = event.getEventAttrValue("sysHwVersion");
		String sysHwBuild = event.getEventAttrValue("sysHwBuild");
		String sysSwVersion = event.getEventAttrValue("sysSwVersion");
		String sysSwRevision = event.getEventAttrValue("sysSwRevision");
		String sysPort = event.getEventAttrValue("sysPort");
		String ntwEthPhy = event.getEventAttrValue("ntwEthPhy.hex");

		if (sysSwVersion != null && !"".equals(sysSwVersion)) {
			sysSwVersion = DataUtil.getVersionString(Integer.parseInt(sysSwVersion));
		}
		if (sysHwVersion != null && !"".equals(sysHwVersion)) {
			sysHwVersion = DataUtil.getVersionString(Integer.parseInt(sysHwVersion));
		}

		//log.debug("sysTYPE["+sysType+"]");
		String protocolType = Protocol.GPRS.name();

		// make Target
		Hashtable<String, String> target = new Hashtable<String, String>();
		target.put("id", sysID);
		target.put("protocolType", protocolType);
		target.put("listenPort", sysPort);
		target.put("ipAddr", ipAddr);

		if (event.getActivatorType() != TargetClass.DCU) {
			log.warn("Activator Type is not MCU");
			return;
		}

		MCU mcu = new MCU();

		// Log
		StringBuffer logBuf = new StringBuffer();
		logBuf.append("ipAddr[" + ipAddr + "] sysID[" + sysID + "] sysName[" + sysName + "] sysLocation[" + sysLocation + "] sysModel[" + sysModel + "] sysHwVersion[" + sysHwVersion + "] sysHwBuild[" + sysHwBuild + "] sysSwVersion[" + sysSwVersion + "] sysSwRevision[" + sysSwRevision + "] sysPort[" + sysPort + "] ntwEthPhy[" + ntwEthPhy + "]");
		log.debug(logBuf.toString());

		TransactionStatus txstatus = null;
		try {
			txstatus = txmanager.getTransaction(null);

			mcu.setSysID(sysID);
			mcu.setIpAddr(ipAddr);
			McuType mcuTypeEnum = McuType.DCU;

			DeviceModel model = deviceModelDao.findByCondition("name", sysModel);
			if (model != null) {
				mcu.setDeviceModel(model);
			}

			mcu.setSysName(sysName);
			mcu.setSysLocation(sysLocation);
			mcu.setMcuType(CommonConstants.getMcuTypeByName(mcuTypeEnum.name()));
			mcu.setNetworkStatus(1);
			mcu.setInstallDate(TimeUtil.getCurrentTime());
			mcu.setLastCommDate(TimeUtil.getCurrentTime());
			mcu.setSysModel(sysModel);
			mcu.setProtocolType(CommonConstants.getProtocolByName(protocolType + ""));
			mcu.setSysHwVersion(sysHwVersion);
			mcu.setSysSwVersion(sysSwVersion);
			mcu.setSysSwRevision(sysSwRevision);
			mcu.setSysLocalPort(new Integer(sysPort));

			String a = "";
			String b = "";
			String c = "";
			String d = "";
			String e = "";
			String f = "";

			if (ntwEthPhy != null && !"".equals(ntwEthPhy)) {
				a = ntwEthPhy.substring(0, 2);
				b = ntwEthPhy.substring(2, 4);
				c = ntwEthPhy.substring(4, 6);
				d = ntwEthPhy.substring(6, 8);
				e = ntwEthPhy.substring(8, 10);
				f = ntwEthPhy.substring(10, 12);
			}
			log.info("### MAC Address = " + a + ":" + b + ":" + c + ":" + d + ":" + e + ":" + f);

			mcu.setMacAddr(a + ":" + b + ":" + c + ":" + d + ":" + e + ":" + f);
			mcu.setProtocolVersion("0102");
			mcu.setNameSpace("NG");

			Code meterStatus = codeDao.getCodeIdByCodeObject("1.1.4.1"); // normal
			mcu.setMcuStatus(meterStatus);

			// if not mcu, it's is created and installed.
			MCU existMcu = dcuDao.get(mcu.getSysID());
			if (existMcu == null) {
				log.info("mcu[" + mcu.getSysID() + "] is not existed!!");
				//Set Default Location            
				String defaultLocName = FMPProperty.getProperty("loc.default.name");
				if (defaultLocName != null && !"".equals(defaultLocName)) {
					if (locationDao.getLocationByName(StringUtil.toDB(defaultLocName)) != null && locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).size() > 0) {
						log.info("MCU[" + mcu.getSysID() + "] Set Default Location[" + locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0).getId() + "]");
						mcu.setLocation(locationDao.getLocationByName(StringUtil.toDB(defaultLocName)).get(0));
					} else {
						log.info("MCU[" + mcu.getSysID() + "] Default Location[" + defaultLocName + "] is Not Exist In DB, Set First Location[" + locationDao.getAll().get(0).getId() + "]");
						mcu.setLocation(locationDao.getAll().get(0));
					}
				} else {
					log.info("MCU[" + mcu.getSysID() + "] Default Location is Not Exist In Properties, Set First Location[" + locationDao.getAll().get(0).getId() + "]");
					mcu.setLocation(locationDao.getAll().get(0));
				}

				//Set Default Supplier
				String supplierName = new String(FMPProperty.getProperty("default.supplier.name").getBytes("8859_1"), "UTF-8");
				log.debug("Supplier Name[" + supplierName + "]");
				Supplier supplier = supplierName != null ? supplierDao.getSupplierByName(supplierName) : null;

				if (supplier != null && supplier.getId() != null && mcu.getSupplier() == null) {
					mcu.setSupplier(supplier);
				} else {
					log.info("MCU[" + mcu.getSysID() + "] Default Supplier is Not Exist In Properties, Set First Supplier[" + supplierDao.getAll().get(0).getId() + "]");
					mcu.setSupplier(supplierDao.getAll().get(0));
				}
				dcuDao.add(mcu);
			} else {
				log.info("mcu[" + existMcu.getSysID() + "] is existed!! - Location Id:" + existMcu.getLocation().getId() + " Location Name:" + existMcu.getLocation().getName());
				existMcu.setSysName(mcu.getSysName());
				existMcu.setSysLocation(mcu.getSysLocation());

				existMcu.setMcuType(mcu.getMcuType());
				existMcu.setDeviceModel(mcu.getDeviceModel());
				existMcu.setSysModel(mcu.getSysModel());

				existMcu.setIpAddr(mcu.getIpAddr());
				existMcu.setMacAddr(mcu.getMacAddr());
				existMcu.setSysLocalPort(mcu.getSysLocalPort());
				existMcu.setSysPhoneNumber(mcu.getSysPhoneNumber());
				existMcu.setProtocolType(CommonConstants.getProtocolByName(protocolType + ""));
				existMcu.setSysHwVersion(sysHwVersion);
				existMcu.setSysSwVersion(sysSwVersion);
				existMcu.setSysSwRevision(sysSwRevision);
				existMcu.setLastCommDate(TimeUtil.getCurrentTime());
				existMcu.setProtocolVersion(mcu.getProtocolVersion());
				existMcu.setNameSpace(mcu.getNameSpace());
				existMcu.setMacAddr(mcu.getMacAddr());
				existMcu.setMcuStatus(mcu.getMcuStatus());
				if (existMcu.getInstallDate() == null || "".equals(existMcu.getInstallDate()))
					existMcu.setInstallDate(TimeUtil.getCurrentTime());
				// 업데이트를 호출하지 않더라도 갱신이 된다.
				event.getEventAlert().setName("Equipment Registration");
				
				mcu.setSupplier(existMcu.getSupplier());
				mcu.setLocation(existMcu.getLocation());
			}

			event.setActivatorIp(mcu.getIpAddr());
			event.setSupplier(mcu.getSupplier());
			event.setLocation(mcu.getLocation());
		} finally {
			if (txstatus != null)
				txmanager.commit(txstatus);
		}

		log.debug("DCU Install Event Action Compelte");
	}

}
