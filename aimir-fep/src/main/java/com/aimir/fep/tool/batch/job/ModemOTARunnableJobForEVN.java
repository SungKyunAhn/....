/**
 * 
 */
package com.aimir.fep.tool.batch.job;

import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.OTAType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.ModemDao;
import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.tool.batch.excutor.IBatchRunnable;
import com.aimir.fep.trap.actions.GV.EV_GV_evtOTADownloadEnd_Action;
import com.aimir.fep.trap.actions.GV.EV_GV_evtOTADownloadResult_Action;
import com.aimir.fep.trap.actions.GV.EV_GV_evtOTADownloadStart_Action;
import com.aimir.fep.trap.common.EV_Action.OTA_UPGRADE_RESULT_CODE;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.Device.DeviceType;
import com.aimir.model.device.Modem;
import com.aimir.util.DateTimeUtil;
import com.aimir.util.TimeUtil;

/**
 * @author simhanger
 *
 */
public class ModemOTARunnableJobForEVN implements IBatchRunnable {
	private static Logger logger = LoggerFactory.getLogger(ModemOTARunnableJobForEVN.class);
	private String name;
	private Target target;
	private Map<String, String> asyncModemGPRSParamMap;
	private final String COMMAND_NAME = "cmdOTAStart";

	int modemOTAWaitTime = Integer.parseInt(FMPProperty.getProperty("ota.firmware.modem.wait.gprs", "0"));

	public ModemOTARunnableJobForEVN(long id, OTAType otaType, Target target, Map<String, String> asyncModemGPRSParamMap) {
		this.target = target;
		this.asyncModemGPRSParamMap = asyncModemGPRSParamMap;

		name = id + "-RUN-" + target.getMeterId() + "_" + target.getModemId();

		logger.debug("# ModemOTARunnableJobForEVN Name={}, OtaType={}, Target={}", name, otaType.name(), target.toString());
	}

	@Override
	public void run() {
		logger.debug("#### ModemOTARunnable Start - name={}", name);
		/*
		 * OTA 시작 Event 저장
		 */
		ModemDao modemDao = DataUtil.getBean(ModemDao.class);
		Modem modem = modemDao.get(target.getModemId());
		TargetClass tClass = TargetClass.valueOf(modem.getModemType().name());

		String openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
		EV_GV_evtOTADownloadStart_Action action = new EV_GV_evtOTADownloadStart_Action();
		action.makeEvent(tClass, target.getModemId(), tClass, openTime, "HES");
		action.updateOTAHistory(target.getModemId(), DeviceType.Modem, openTime);
		
		
		/*
		 * 비동기 명령 저장
		 */
		long trnxId = -1l;
		try {
			trnxId = saveAsyncCommand(target, COMMAND_NAME, asyncModemGPRSParamMap, TimeUtil.getCurrentTime());
		} catch (ParseException e1) {
			logger.error("CurrentTime Parsing error - {}", e1.getMessage());
		}

		if (1 <= trnxId) { // 비동기 명령 저장 성공
//			JpaTransactionManager txManager = null;
//			TransactionStatus txStatus = null;

			try {
//				txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
//				txStatus = txManager.getTransaction(null);

				AsyncCommandLogDao asyncCommandLogDao = DataUtil.getBean(AsyncCommandLogDao.class);
				AsyncCommandResultDao asyncCommandResultDao = DataUtil.getBean(AsyncCommandResultDao.class);

				/*
				 * wait time이 0일경우 결과를 기다리지 않고 async 명령만 저장하고 끝낸다.
				 */
				if (modemOTAWaitTime == 0) {
					printResult(name, ResultStatus.SUCCESS, "Modem OTA Excute ok - " + target.getModemId());
				} else {
					Thread.sleep(modemOTAWaitTime * 1000);

					Integer lastStatus = asyncCommandLogDao.getCmdStatus(target.getModemId(), COMMAND_NAME);

					if (TR_STATE.Success.getCode() != lastStatus) {
						printResult(name, ResultStatus.getResultStatus(lastStatus), "Modem OTA Excute ok - " + target.getModemId());
					} else {
						List<AsyncCommandResult> asyncResult = asyncCommandResultDao.getCmdResults(target.getModemId(), trnxId, COMMAND_NAME); //ASYNC_COMMAND_RESULT에서 결과 값을 가져옴
						if (asyncResult == null || asyncResult.size() <= 0) {
							printResult(name, ResultStatus.getResultStatus(lastStatus), "Excute OTA but fail to result data saving. ModemID =  " + target.getModemId());
						} else { // Success
							for (int i = 0; i < asyncResult.size(); i++) {
								printResult(name, ResultStatus.getResultStatus(lastStatus), "Success get result - " + i + ". " + asyncResult.get(i).getResultValue());
							}
						}
					}
				}

//				txManager.commit(txStatus);
			} catch (Exception e) {
				printResult(name, ResultStatus.FAIL, "ERROR on AsyncCommand create Transaction - " + e.getMessage());
				logger.error("ERROR on AsyncCommand create Transaction - " + e.getMessage(), e);
//				if (txStatus != null) {
//					txManager.rollback(txStatus);
//				}
			}
		} else { // 비동기 명령 저장이 안된경우
			printResult(name, ResultStatus.FAIL, "AsyncCommand saving fail");
			logger.error("AsyncCommand saving fail");
			
			openTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
			EV_GV_evtOTADownloadEnd_Action action2 = new EV_GV_evtOTADownloadEnd_Action();
			action2.makeEvent(tClass, target.getModemId(), tClass, openTime, "0", OTA_UPGRADE_RESULT_CODE.OTAERR_TRN_FAIL, null, "HES");
			action2.updateOTAHistory(target.getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_TRN_FAIL, "AsyncCommand saving fail");

			
			EV_GV_evtOTADownloadResult_Action action3 = new EV_GV_evtOTADownloadResult_Action();
			action3.makeEvent(tClass, target.getModemId(), tClass, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_TRN_FAIL, null, "HES");
			action3.updateOTAHistory(target.getModemId(), DeviceType.Modem, openTime, OTA_UPGRADE_RESULT_CODE.OTAERR_TRN_FAIL);
		}

	}

	private long saveAsyncCommand(Target target, String cmd, Map<String, String> param, String currentTime) {
		Long trnxId = -1l;

		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		try {
			AsyncCommandLogDao asyncCommandLogDao = DataUtil.getBean(AsyncCommandLogDao.class);
			AsyncCommandParamDao asyncCommandParamDao = DataUtil.getBean(AsyncCommandParamDao.class);

			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(null);

			/*
			 * 모뎀이 서버에 접속하여 수행해야할 Command가 무엇인지 구분할 방법이 따로 없기 때문에 Transaction ID를 사용하여 구분하도록 한다.
			 */
			trnxId = asyncCommandLogDao.getMaxTrId(target.getModemId());
			if (trnxId == null || trnxId <= 0) {
				trnxId = 1l;
			} else {
				trnxId++;
			}

			AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
			asyncCommandLog.setTrId(trnxId);
			asyncCommandLog.setMcuId(target.getModemId());
			asyncCommandLog.setDeviceType(McuType.MMIU.name()); // MMIU
			//asyncCommandLog.setDeviceType(otaType.name());  // MODEM_GPRS
			asyncCommandLog.setDeviceId(target.getModemId());
			asyncCommandLog.setCommand(cmd);
			asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
			asyncCommandLog.setState(1);
			asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
			asyncCommandLog.setCreateTime(currentTime);
			asyncCommandLog.setRequestTime(currentTime);
			asyncCommandLog.setLastTime(null);
			asyncCommandLogDao.add(asyncCommandLog);
			logger.debug("## Save Async CommandLog - {}", asyncCommandLog.toJSONString());

			Integer num = 0;
			if (param != null && param.size() > 0) {

				//parameter가 존재할 경우.
				Integer maxNum = asyncCommandParamDao.getMaxNum(target.getModemId(), trnxId);

				if (maxNum != null) {
					num = maxNum + 1;
				}

				Iterator<String> iter = param.keySet().iterator();
				while (iter.hasNext()) {
					String key = iter.next();

					AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
					asyncCommandParam.setMcuId(target.getModemId());
					asyncCommandParam.setNum(num);
					asyncCommandParam.setParamType(key);
					asyncCommandParam.setParamValue((String) param.get(key));
					asyncCommandParam.setTrId(trnxId);

					asyncCommandParamDao.add(asyncCommandParam);
					logger.debug("## Save Async Command Param - {}", asyncCommandParam.toJSONString());
					num += 1;
				}
			}

			txManager.commit(txStatus);
		} catch (Exception e) {
			logger.error("ERROR on AsyncCommand create Transaction - " + e.getMessage(), e);
			if (txStatus != null) {
				txManager.rollback(txStatus);
			}
			trnxId = -1l;
		}

		return trnxId;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void printResult(String title, ResultStatus status, String desc) {
		logger.info(title + "," + status.name() + "," + desc);
	}
}
