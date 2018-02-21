package com.aimir.fep.trap.actions.SP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.FirmwareIssueHistoryDao;
import com.aimir.dao.device.MCUDao;
import com.aimir.dao.device.MeterDao;	// INSERT SP-707
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.model.device.Meter;	// INSERT SP-707
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/*
 * Event ID : 200.65.0 evtOTADownloadEnd Processing
 * 
 *  1) RequestID   - UINT
 *  2) UpgradeType - BYTE
 *  3) TargetID    - STRING
 *  4) Result      - BYTE
 *  5) Elapsed     - INT
 */
@Component
public class EV_SP_200_65_0_Action implements EV_Action {
	private static Logger logger = LoggerFactory.getLogger(EV_SP_200_65_0_Action.class);

	@Autowired
	MCUDao mcuDao;

	/*
	 * Please don't change EVENT_MESSAGE message. because of concerned FIRMWARE_ISSUE_HISTORY searching in DB. 
	 */
	private final String EVENT_MESSAGE = "Ended writing FW";

	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		logger.debug("[EV_SP_200_65_0_Action][evtOTADownloadEnd][{}] Execute.", EVENT_MESSAGE);

		try {
			String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

			String mcuId = trap.getMcuId();
			MCU mcu = mcuDao.get(mcuId);

			logger.debug("[EV_SP_200_65_0_Action][evtOTADownloadEnd] DCU = {}({}), EventCode = {}", trap.getMcuId(), trap.getIpAddr(), trap.getCode());

			String requestId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry"));
			logger.debug("[EV_SP_200_65_0_Action] requestId={}", requestId);

			String upgradeType = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry"));
			logger.debug("[EV_SP_200_65_0_Action] upgradeType={}", OTA_UPGRADE_TYPE.getItem(upgradeType));

			String targetId = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry"));
			logger.debug("[EV_SP_200_65_0_Action] targetId={}", targetId);

			String excuteResult = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry.3"));
			int resultCode = -1;
			try {
				resultCode = Integer.parseInt(excuteResult);
			} catch (Exception e) {
				logger.error("Result code parsing error -" + excuteResult + " : " + e, e);
			}
			logger.debug("[EV_SP_200_65_0_Action] result={}", OTA_UPGRADE_RESULT_CODE.getItem(resultCode).getDesc());

			String elapse = StringUtil.nullToBlank(event.getEventAttrValue("intEntry"));
			logger.debug("[EV_SP_200_65_0_Action] elapse={}", elapse);

			if (mcu != null) {
				mcu.setLastCommDate(issueDate);

				event.setActivatorType(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass());
				event.setActivatorId(targetId);
				event.setLocation(mcu.getLocation());

				EventAlertAttr ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", getEventMessage(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass(), elapse, OTA_UPGRADE_RESULT_CODE.getItem(resultCode), "DCU"));
				event.append(ea);

				/*
				 * History 정보 Update
				 */
				DeviceType deviceType = null;
				if (OTA_UPGRADE_TYPE.getItem(upgradeType) == OTA_UPGRADE_TYPE.METER) {
					deviceType = DeviceType.Meter;
				} else if (OTA_UPGRADE_TYPE.getItem(upgradeType) == OTA_UPGRADE_TYPE.MODEM) {
					deviceType = DeviceType.Modem;
				} else {
					deviceType = DeviceType.MCU;
				}

				updateOTAHistory(targetId, deviceType, issueDate, OTA_UPGRADE_RESULT_CODE.getItem(resultCode), null);
			} else {
				logger.debug("[EV_SP_200_65_0_Action][evtOTADownloadEnd] DCU = {}({}) : Unknown MCU", trap.getMcuId(), trap.getIpAddr());
			}
		} catch (Exception e) {
			logger.error("[EV_SP_200_65_0_Action][evtOTADownloadEnd] Error - ", e);
		}
	}

	/**
	 * EV_SP_200_65_0 Event Make
	 * 
	 * @param activatorType
	 * @param activatorId
	 * @param targetType
	 * @param openTime
	 * @param elapseTime
	 * @param isSuccess
	 * @param message
	 * @param operatorType
	 *            - HES or DCU
	 */

	public void makeEvent(TargetClass activatorType, String activatorId, TargetClass targetType, String openTime, String elapseTime, OTA_UPGRADE_RESULT_CODE resultCode, String message, String operatorType) {
		logger.debug("[EV_SP_200_65_0_Action][evtOTADownloadEnd] MakeEvent.");

		//String resultValue = "[Ended writing FW] Target Type=[" + targetType.name() + "], Elapse Time=[" + elapseTime + "], Result=[" + resultCode.getDesc() + "], OperatorType=[" + operatorType + "]";
		String resultValue = getEventMessage(targetType, elapseTime, resultCode, operatorType);
		if (message != null && !message.equals("")) {
			resultValue += ", Msg=[" + message + "]";
		}

		EventAlertLog eventAlertLog = new EventAlertLog();
		eventAlertLog.setStatus(EventStatus.Open);
		eventAlertLog.setOpenTime(openTime);

		try {
			EventUtil.sendEvent("OTA", activatorType, activatorId, openTime, new String[][] { { "message", resultValue } }, eventAlertLog);
			logger.debug("[EV_SP_200_65_0_Action][openTime={}] evtOTADownloadEnd - {}", openTime, resultValue);
		} catch (Exception e) {
			logger.error("Event save Error - " + e, e);
		}
	}

	/**
	 * Event message make
	 * 
	 * @param targetType
	 * @param elapseTime
	 * @param resultCode
	 * @param operatorType
	 * @return
	 */
	private String getEventMessage(TargetClass targetType, String elapseTime, OTA_UPGRADE_RESULT_CODE resultCode, String operatorType) {
		StringBuilder builder = new StringBuilder();
		builder.append("[" + EVENT_MESSAGE + "]");
		builder.append("Target Type=[" + targetType.name() + "]");
		builder.append(", Elapse Time=[" + elapseTime + "]");
		builder.append(", Result=[" + resultCode.getDesc() + "]");
		builder.append(", OperatorType=[" + operatorType + "]");

		return builder.toString();
	}
	
	/**
	 * History information Update.
	 * 
	 * @param deviceId
	 * @param deviceType
	 * @param openTime
	 */
	public void updateOTAHistory(String deviceId, DeviceType deviceType, String openTime, OTA_UPGRADE_RESULT_CODE resultCode, String desc) {
		logger.debug("Update OTA History params. DeviceId={}, DeviceType={}, OpentTime={}, ResultCode={}, Desc={}", deviceId, deviceType.name(), openTime, resultCode, desc);
		
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		FirmwareIssueHistoryDao firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);

		/*
		 * 개별 Device OTA 이력 UPDATE. 
		 *  - DCU의 경우 Trap이벤트에 issuedate, firmwareid 정보가 없기때문에 가장 최근에 실행한 Device 의 이력을 업데이트하는 방식으로 진행함. 
		 */
		
		String saveResult = resultCode.getDesc() + ((desc != null && !desc.equals("")) ? desc : "");
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);
			firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE, deviceId, deviceType, openTime, saveResult);
			txManager.commit(txStatus);
		} catch (Exception e) {
			logger.error("ERROR on FirmwareIssueHistory update Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}
		
		/*
		 * FirmwareIssue Update
		 */
		try {
			txStatus = txManager.getTransaction(null);
			firmwareIssueHistoryDao.updateOTAHistoryIssue(EVENT_MESSAGE, deviceId, deviceType);
			txManager.commit(txStatus);
		} catch (Exception e) {
			logger.error("ERROR on FirmwareIssue update Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}
	}
	
	// INSERT START SP-707
	/**
	 * History information Update 2.
	 * 
	 * @param deviceId
	 * @param deviceType
	 * @param openTime
	 * @param resultCode
	 * @param desc
	 * @param fwVersion
	 */
	public void updateOTAHistory2(String deviceId, DeviceType deviceType, String openTime, OTA_UPGRADE_RESULT_CODE resultCode, String desc, String fwVersion) {
		logger.debug("Update OTA History 2 params. DeviceId={}, DeviceType={}, OpentTime={}, ResultCode={}, Desc={}, F/W Version={}", 
						deviceId, deviceType.name(), openTime, resultCode, desc, fwVersion);
		
		try {
			updateOTAHistory(deviceId, deviceType, openTime, resultCode, desc);
			
			updateMeterFWVersion(deviceId, deviceType, resultCode, fwVersion);
		} catch (Exception e) {
			logger.error("ERROR on FirmwareIssue update Transaction - " + e.getMessage(), e);
		}
	}

	/**
	 * Update F/W Version(SW Version) of Meter
	 * 
	 * @param deviceId
	 * @param deviceType
	 * @param resultCode
	 * @param fwVresion 
	 */
	private void updateMeterFWVersion(String deviceId, DeviceType deviceType, OTA_UPGRADE_RESULT_CODE resultCode, String fwVersion) {
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		MeterDao meterDao = DataUtil.getBean(MeterDao.class);
		
		if (deviceId == null || deviceType != DeviceType.Meter || resultCode == null || 
			!resultCode.getDesc().equals("Success") || fwVersion == null || fwVersion.equals(""))
			return;
		
		try {
			/*
			 * Meter FW Version UPDATE
			 */
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);
			Meter meter = meterDao.get(deviceId);
			meter.setSwVersion(fwVersion);
			txManager.commit(txStatus);
			logger.debug("[EV_SP_200_65_0_Action] updateMeterFWVersion F/W Version =[" + fwVersion + "]");
		} catch (Exception e) {
			logger.error("[EV_SP_200_65_0_Action] updateMeterFWVersion Error - " + e, e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
			//throw e;
		}
	}
	// INSERT END SP-707
	
}