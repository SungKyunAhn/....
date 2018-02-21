package com.aimir.fep.trap.actions.ZV;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;

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
 * Event ID : 223.1.0 evtOtaStart Processing 
 * 
 *  1) reqId          - UINT : OTA request ID
 *  2) taskId         - UINT  : OTA task ID
 *  3) upgradeType    - BYTE : Upgrade type (0x01: Modem, 0x02: Sensor/Meter)
 *  4) imageKey       - STREAM : Image key
 *  5) target         - STRING : Target address
 *  6) size			  - UINT : 전체 Image size
 */
@Component
public class EV_ZV_223_1_0_Action implements EV_Action {
	private static Logger log = LoggerFactory.getLogger(EV_ZV_223_1_0_Action.class);

	@Autowired
	MCUDao mcuDao;

	/*
	 * Please don't change EVENT_MESSAGE message. because of concerned FIRMWARE_ISSUE_HISTORY searching in DB. 
	 */
	private final String EVENT_MESSAGE = "Started writing FW";

	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("[EV_ZV_223_1_0_Action][evtOtaStart][{}] Execute.", EVENT_MESSAGE);

		try {
			String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

			String mcuId = trap.getMcuId();
			MCU mcu = mcuDao.get(mcuId);

			log.debug("[EV_ZV_223_1_0_Action][evtOtaStart] DCU = {}({}), EventCode = {}", trap.getMcuId(), trap.getIpAddr(), trap.getCode());

			String reqId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry"));
			log.debug("[EV_ZV_223_1_0_Action] reqId={}", reqId);

			String taskId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.1"));
			log.debug("[EV_ZV_223_1_0_Action] taskId={}", taskId);

			String upgradeType = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry"));
			log.debug("[EV_ZV_223_1_0_Action] upgradeType={}", OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass().name());

			String imageKey = StringUtil.nullToBlank(event.getEventAttrValue("streamEntry"));
			log.debug("[EV_ZV_223_1_0_Action] imageKey={}", imageKey);

			String target = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry"));
			log.debug("[EV_ZV_223_1_0_Action] target={}", target);

			String size = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.2"));
			log.debug("[EV_ZV_223_1_0_Action] size={}", size);

			if (mcu != null) {
				mcu.setLastCommDate(issueDate);

				event.setActivatorType(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass());
				event.setActivatorId(target);
				event.setLocation(mcu.getLocation());

				EventAlertAttr ea = EventUtil.makeEventAlertAttr("message", "java.lang.String", getEventMessage(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass(), "DCU"));
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

				updateOTAHistory(target, deviceType, issueDate);

			} else {
				log.debug("[EV_ZV_223_1_0_Action][evtOtaStart] DCU = {}({}) : Unknown DCU", trap.getMcuId(), trap.getIpAddr());
			}
		} catch (Exception e) {
			log.error("[EV_ZV_223_1_0_Action][evtOtaStart] Error - ", e);
		}
	}

	/**
	 * Event message make
	 * 
	 * @param targetType
	 * @param operatorType
	 * @return
	 */
	private String getEventMessage(TargetClass targetType, String operatorType) {
		StringBuilder builder = new StringBuilder();
		builder.append("[" + EVENT_MESSAGE + "]");
		builder.append("Target Type=[" + targetType.name() + "]");
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
	public void updateOTAHistory(String deviceId, DeviceType deviceType, String openTime) {
		log.debug("Update OTA History params. DeviceId={}, DeviceType={}, OpentTime={}", deviceId, deviceType.name(), openTime);

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
			firmwareIssueHistoryDao.updateOTAHistory(EVENT_MESSAGE, deviceId, deviceType, openTime, "OK");
			txManager.commit(txStatus);
		} catch (Exception e) {
			log.error("ERROR on FirmwareIssueHistory update Transaction - " + e.getMessage(), e);
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
			log.error("ERROR on FirmwareIssue update Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}
	}
}
