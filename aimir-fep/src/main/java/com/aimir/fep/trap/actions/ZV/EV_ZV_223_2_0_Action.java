package com.aimir.fep.trap.actions.ZV;

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
 * Event ID : 223.2.0 evtOtaEnd Processing 
 * 
 * 1) reqId			UINT	4 	OTA request ID
 * 2) taskId		UINT	4	OTA task ID
 * 3) upgradeType	BYTE	1	Upgrade type (0x01: Modem, 0x02: Sensor/Meter)
 * 4) imageKey		STREAM	N	Image key
 * 5) target		STRING	N	Target address
 * 6) size			UINT	4	전체 Image size
 * 7) offset		UINT	4	전송된 Image size
 * 8) result		BYTE	1	OTA result (표 13)
 * 9) lastStartTime	UINT	4	마지막 task start time
 * 10)lastEndTime	UINT	4	마지막 task end time
 * 11)elapse		UINT	4	소요시간(초)
 * 12)count		   	UINT	4	시도 횟수 
 */
/*
 * evtOtaEnd(223.2.0)
 */
@Component
public class EV_ZV_223_2_0_Action implements EV_Action {
	private static Logger log = LoggerFactory.getLogger(EV_ZV_223_2_0_Action.class);

	@Autowired
	private MCUDao mcuDao;
	private String target;
	private String size;
	private String result;
	private String lastStartTime;
	private String lastEndTime;
	private String elapse;
	private String count;

	/*
	 * Please don't change EVENT_MESSAGE message. because of concerned FIRMWARE_ISSUE_HISTORY searching in DB. 
	 */
	private final String EVENT_MESSAGE_END = "Ended writing FW";
	private final String EVENT_MESSAGE_RESULT = "OTA Result";
	
	public void execute(FMPTrap trap, EventAlertLog event) throws Exception {
		log.debug("[EV_ZV_223_2_0_Action][evtOtaEnd][{}] Execute.", EVENT_MESSAGE_END);

		try {
			String issueDate = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");

			String mcuId = trap.getMcuId();
			MCU mcu = mcuDao.get(mcuId);

			log.debug("[EV_ZV_223_2_0_Action][evtOtaEnd] DCU = {}({}), EventCode = {}", trap.getMcuId(), trap.getIpAddr(), trap.getCode());

			String reqId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry"));
			log.debug("[EV_ZV_223_2_0_Action] reqId={}", reqId);

			String taskId = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.1"));
			log.debug("[EV_ZV_223_2_0_Action] taskId={}", taskId);

			String upgradeType = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry"));
			log.debug("[EV_ZV_223_2_0_Action] upgradeType={}", OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass().name());

			String imageKey = StringUtil.nullToBlank(event.getEventAttrValue("streamEntry"));
			log.debug("[EV_ZV_223_2_0_Action] imageKey={}", imageKey);

			String target = StringUtil.nullToBlank(event.getEventAttrValue("stringEntry"));
			log.debug("[EV_ZV_223_2_0_Action] target={}", target);

			size = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.2"));
			log.debug("[EV_ZV_223_2_0_Action] size={}", size);

			String offset = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.3"));
			log.debug("[EV_ZV_223_2_0_Action] offset={}", offset);

			result = StringUtil.nullToBlank(event.getEventAttrValue("byteEntry.1"));
			log.debug("[EV_ZV_223_2_0_Action] result={}", OTA_UPGRADE_RESULT_CODE.getItem(Integer.parseInt(result)));

			lastStartTime = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.4"));
			log.debug("[EV_ZV_223_2_0_Action] lastStartTime={}", lastStartTime);

			lastEndTime = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.5"));
			log.debug("[EV_ZV_223_2_0_Action] lastEndTime={}", lastEndTime);

			elapse = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.6"));
			log.debug("[EV_ZV_223_2_0_Action] elapse={}", elapse);

			count = StringUtil.nullToBlank(event.getEventAttrValue("uintEntry.7"));
			log.debug("[EV_ZV_223_2_0_Action] reqId={}", count);

			/*
			 * End Event를 먼저 저장한다.
			 */
			otaEndEventSave(OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass(), "DCU", upgradeType, target, issueDate);

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

				updateOTAHistory(target, deviceType, issueDate, EVENT_MESSAGE_RESULT);
				log.debug("[EV_ZV_223_2_0_Action][evtOTAResult][{}] Execute.", EVENT_MESSAGE_RESULT);
			} else {
				log.debug("[EV_ZV_223_2_0_Action][evtOTAResult] DCU = {}({}) : Unknown DCU", trap.getMcuId(), trap.getIpAddr());
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * OTA End Event save
	 * 
	 * @param targetType
	 * @param operatorType
	 * @param upgradeType
	 * @param activatorId
	 * @param issueDate
	 */
	private void otaEndEventSave(TargetClass targetType, String operatorType, String upgradeType, String activatorId, String issueDate) {
		String eventClassName = "OTA";

		StringBuilder builder = new StringBuilder();
		builder.append("[" + EVENT_MESSAGE_END + "]");
		builder.append("Target Type=[" + targetType.name() + "]");
		builder.append("Target Address=[" + target + "]");
		builder.append(", OperatorType=[" + operatorType + "]");

		EventAlertLog eventAlertLog = new EventAlertLog();
		eventAlertLog.setStatus(EventStatus.Open);
		eventAlertLog.setOpenTime(issueDate);

		try {
			EventUtil.sendEvent(eventClassName, OTA_UPGRADE_TYPE.getItem(upgradeType).getTargetClass(), activatorId, issueDate, new String[][] { { "message", builder.toString() } }, eventAlertLog);
			log.debug("[EV_ZV_223_2_0_Action][evtOtaEnd][openTime={}] evtOtaEnd - {}", issueDate, builder.toString());
		} catch (Exception e) {
			log.error("[EV_ZV_223_2_0_Action][evtOtaEnd] event save error - " + e.getMessage(), e);
		}

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

		updateOTAHistory(activatorId, deviceType, issueDate, EVENT_MESSAGE_END);
	}
	
	/**
	 * EV_ZV_223_2_0_Action Event Make
	 * 
	 * @param activatorType
	 * @param activatorId
	 * @param targetType
	 * @param openTime
	 * @param resultCode
	 * @param operatorType
	 *            - HES or DCU
	 */
	public void makeEvent(TargetClass activatorType, String activatorId, TargetClass targetType, String openTime, OTA_UPGRADE_RESULT_CODE resultCode, String message, String operatorType) {		
		log.debug("[EV_ZV_223_2_0_Action][evtOtaEnd] MakeEvent.");		

		String resultValue = "Target Type=[" + targetType.name() + "]" + ", OperatorType=[" + operatorType + "], ResultCode=[" + resultCode.name() + "], Msg = " + message; 

		EventAlertLog eventAlertLog = new EventAlertLog();
		eventAlertLog.setStatus(EventStatus.Open);
		eventAlertLog.setOpenTime(openTime);

		try {
			EventUtil.sendEvent("OTA", activatorType, activatorId, openTime, new String[][] { { "message", resultValue } }, eventAlertLog);
			log.debug("[EV_ZV_223_2_0_Action][openTime={}] evtOtaEndResult - {}", openTime, resultValue);
		} catch (Exception e) {
			log.error("Event save Error - " + e, e);
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
		builder.append("[" + EVENT_MESSAGE_RESULT + "]");
		builder.append("Target Type=[" + targetType.name() + "]");
		builder.append("Target Address=[" + target + "]");
		builder.append(", OperatorType=[" + operatorType + "]");
		builder.append(", Size=[" + size + "]");
		builder.append(", Result=[" + result + "]");
		builder.append(", LastStartTime=[" + lastStartTime + "]");
		builder.append(", LastEndTime=[" + lastEndTime + "]");
		builder.append(", Elapse=[" + elapse + "]");
		builder.append(", Count=[" + count + "]");

		return builder.toString();
	}

	/**
	 * History information Update.
	 * 
	 * @param deviceId
	 * @param deviceType
	 * @param openTime
	 */
	public void updateOTAHistory(String deviceId, DeviceType deviceType, String openTime, String eventMessage) {
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
			firmwareIssueHistoryDao.updateOTAHistory(eventMessage, deviceId, deviceType, openTime, "OK");
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
			firmwareIssueHistoryDao.updateOTAHistoryIssue(eventMessage, deviceId, deviceType);
			txManager.commit(txStatus);
		} catch (Exception e) {
			log.error("ERROR on FirmwareIssue update Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
		}
	}
}
