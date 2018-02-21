/**
 * 
 */
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
import com.aimir.fep.trap.common.EV_Action;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.EventAlertAttr;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.device.MCU;
import com.aimir.notification.FMPTrap;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.StringUtil;

/*
 * Event ID : 200.59.0 evtFWUpdate Processing - Frimware Update
 *
 * 1) RequestID   - UINT 
 * 2) UpgradeType - BYTE 
 * 3) Version     - STRING
 * 
 */
@Component
public class EV_SP_200_59_0_Action implements EV_Action {
	private static Logger logger = LoggerFactory.getLogger(EV_SP_200_59_0_Action.class);

	@Autowired
	MCUDao mcuDao;

	/*
	 * Please don't change EVENT_MESSAGE message. because of concerned FIRMWARE_ISSUE_HISTORY searching in DB. 
	 */
	private final String EVENT_MESSAGE = "Frimware Update";

	@Override
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		logger.debug("[EV_SP_200_59_0_Action][evtFWUpdate][{}] Execute.", EVENT_MESSAGE);

		try {
			String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

			String mcuId = trap.getMcuId();
			MCU mcu = mcuDao.get(mcuId);

			logger.debug("[EV_SP_200_59_0_Action][evtFWUpdate] DCU = {}({}), EventCode = {}", trap.getMcuId(), trap.getIpAddr(), trap.getCode());

			String requestId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry"));
			logger.debug("[EV_SP_200_63_0_Action] requestId={}", requestId);

			String upgradeType = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry"));
			logger.debug("[EV_SP_200_63_0_Action] upgradeType={}", OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass().name());

			String version = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry"));
			logger.debug("[EV_SP_200_63_0_Action] imageUrl={}", version);

			if (mcu != null) {
				mcu.setLastCommDate(issueDate);

				event.setActivatorType(TargetClass.DCU);
				event.setActivatorId(trap.getSourceId());
				event.setLocation(mcu.getLocation());

				EventAlertAttr ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", getEventMessage(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass(), version, "DCU"));
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

				updateOTAHistory(trap.getSourceId(), deviceType, issueDate, version);
			} else {
				logger.debug("[EV_SP_200_59_0_Action][evtFWUpdate] DCU = {}({}) : Unknown MCU", trap.getMcuId(), trap.getIpAddr());
			}
		} catch (Exception e) {
			logger.error("[EV_SP_200_59_0_Action][evtFWUpdate] Error - ", e);
		}
	}

	/**
	 * EV_SP_200_59_0 Event Make
	 * 
	 * @param activatorType
	 * @param activatorId
	 * @param targetType
	 * @param openTime
	 * @param version
	 * @param operatorType
	 *            - Only DCU
	 */
	public void makeEvent(TargetClass activatorType, String activatorId, TargetClass targetType, String openTime, String version, String operatorType) {
		logger.debug("[EV_SP_200_59_0_Action][evtFWUpdate] MakeEvent.");

		//String resultValue = "[Frimware Update] Target Type=[" + targetType.name() + "], Version=[" + version + "], OperatorType=[" + operatorType + "]";
		String resultValue = getEventMessage(targetType, version, operatorType);

		EventAlertLog eventAlertLog = new EventAlertLog();
		eventAlertLog.setStatus(EventStatus.Open);
		eventAlertLog.setOpenTime(openTime);

		try {
			EventUtil.sendEvent("OTA", activatorType, activatorId, openTime, new String[][] { { "message", resultValue } }, eventAlertLog);
			logger.debug("[EV_SP_200_59_0_Action][openTime={}] evtFWUpdate - {}", openTime, resultValue);
		} catch (Exception e) {
			logger.error("Event save Error - " + e, e);
		}
	}

	/**
	 * Event message make
	 * 
	 * @param targetType
	 * @param version
	 * @param operatorType
	 * @return
	 */
	private String getEventMessage(TargetClass targetType, String version, String operatorType) {
		StringBuilder builder = new StringBuilder();
		builder.append("[" + EVENT_MESSAGE + "]");
		builder.append("Target Type=[" + targetType.name() + "]");
		builder.append(", Version=[" + version + "]");
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
	public void updateOTAHistory(String deviceId, DeviceType deviceType, String openTime, String resultStatus) {
		logger.debug("Update OTA History params. DeviceId={}, DeviceType={}, OpentTime={}, ResultStatus={}", deviceId, deviceType.name(), openTime, resultStatus);
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		FirmwareIssueHistoryDao firmwareIssueHistoryDao = DataUtil.getBean(FirmwareIssueHistoryDao.class);

		/*
		 * 개별 Device OTA 이력 UPDATE. 
		 *  - DCU의 경우 Trap이벤트에 issuedate, firmwareid 정보가 없기때문에 가장 최근에 실행한 Device 의 이력을 업데이트하는 방식으로 진행함. 
		 */
		try {
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE, deviceId, deviceType, openTime, resultStatus);
			
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



}
