/**
 * 
 */
package com.aimir.fep.tool.batch.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import com.aimir.constants.CommonConstants.ResultStatus;
import com.aimir.dao.system.CodeDao;
import com.aimir.fep.command.mbean.CommandGW;
import com.aimir.fep.tool.MOECommandBatchTool.MOECommandType;
import com.aimir.fep.tool.batch.AsyncCommandExcute_MOE;
import com.aimir.fep.tool.batch.excutor.IBatchRunnable;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.model.system.Code;

/**
 * @author simhanger
 *
 */
@Service(value = "CurrentLoadLimitJob")
public class CurrentLoadLimitJob implements IBatchRunnable {
	private static Logger log = LoggerFactory.getLogger(CurrentLoadLimitJob.class);

	private MOECommandType commandType;
	private Meter meter;
	private String mcuId;
	private int interval;
	private AsyncCommandExcute_MOE excute;
	private String judgeTime;
	private String threshold;
	private Code operationCode = null;
	private Code targetTypeCode = null;

	private ApplicationContext ctx;

	public CurrentLoadLimitJob() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param commandType
	 * @param ctx
	 * @param meter
	 * @param mcuId
	 * @param interval
	 * @param judgeTime
	 * @param threshold
	 */
	public CurrentLoadLimitJob(ApplicationContext ctx, MOECommandType commandType, Meter meter, String mcuId, int interval, String judgeTime, String threshold) {
		this.ctx = ctx;
		this.commandType = commandType;
		this.meter = meter;
		this.mcuId = mcuId;
		this.interval = interval;
		this.judgeTime = judgeTime;
		this.threshold = threshold;
	}

	@Transactional(value = "transactionManager")
	public void run() {
		excute = new AsyncCommandExcute_MOE();

		JpaTransactionManager txManager = ctx.getBean(JpaTransactionManager.class);
		TransactionStatus txStatus = txManager.getTransaction(null);

		CodeDao codeDao = DataUtil.getBean(CodeDao.class);
		targetTypeCode = codeDao.getCodeIdByCodeObject("1.3.1.1"); // EnergyMeter
		operationCode = codeDao.getCodeIdByCodeObject("8.1.40"); // Current load limit

		if (mcuId == null || mcuId.equals("")) {
			excuteGPRSType();
		} else {
			excutePLCType();
		}

		txManager.commit(txStatus);
	}

	/**
	 * PLC Modem 이용인 경우 DCU 통해서 처리
	 */
	private ResultStatus excutePLCType() {
		log.debug("## PLC Type excute..");
		ResultStatus result = ResultStatus.FAIL;

		CommandGW commandGw = DataUtil.getBean(CommandGW.class);
		int portNumber = 0;
		@SuppressWarnings("rawtypes")
		Map resultTable = null;

		/*
		 * 1. Connect Tunnel Create
		 */
		try {
			resultTable = commandGw.cmdCreateTunnel(mcuId, meter.getMdsId(), 30, 0);
			if (resultTable != null && resultTable.size() > 0) {
				@SuppressWarnings("unchecked")
				Iterator<String> keys = resultTable.keySet().iterator();

				String keyVal = null;
				while (keys.hasNext()) {
					keyVal = (String) keys.next();
					portNumber = Integer.parseInt(resultTable.get(keyVal).toString());
					break;
				}
			}
		} catch (Exception ex) {
			printResult(meter.getMdsId(), ResultStatus.FAIL, "Connection Init Error[" + ex.getMessage() + "]");
			return result;
		}

		/*
		 * 2. Connect Bypass
		 */
		try {
			List<java.lang.String> datas = new ArrayList<java.lang.String>();
			datas.add("cmd," + commandType.getCmd());
			if (commandType == MOECommandType.SET_CURRENT_LOAD_LIMIT) {
				datas.add("judgeTime," + judgeTime);
				datas.add("threshold," + threshold);
			}

			resultTable = commandGw.cmdConnectByPass(mcuId, portNumber, datas.toArray(new String[0]));
			printResult(meter.getMdsId(), ResultStatus.SUCCESS, resultTable.toString());

			if (operationCode != null) {
				int supplierId = meter.getSupplierId();
				excute.saveOperationLog(supplierId, targetTypeCode, meter.getMdsId(), "system", operationCode, ResultStatus.SUCCESS.getCode(), ResultStatus.SUCCESS.name(), resultTable.toString());
			}
		} catch (Exception e) {
			printResult(meter.getMdsId(), ResultStatus.FAIL, "Data Connect Error[" + e.getMessage() + "]");

			try {
				commandGw.cmdDeleteTunnel(mcuId, meter.getMdsId());
			} catch (Exception e1) {
			}

			return result;
		}

		/*
		 * 3. Connect Tunnel Delete
		 */
		try {
			commandGw.cmdDeleteTunnel(mcuId, meter.getMdsId());
		} catch (Exception e) {
			printResult(meter.getMdsId(), ResultStatus.FAIL, "Connect Close Error[" + e.getMessage() + "]");
		}

		return ResultStatus.SUCCESS;
	}

	/**
	 * GPRS Modem 이용인경우 SMS 발송 방식으로 처리
	 */
	private void excuteGPRSType() {
		log.debug("## GPRS Type excute..");
		Map<String, String> params = new HashMap<String, String>();
		if (commandType == MOECommandType.SET_CURRENT_LOAD_LIMIT) {
			params.put("oid", "244.0.0"); // GPRS Connect : OID - 244.0.0
			params.put("judgeTime", judgeTime); // Current over limit duration
												// judge time
			params.put("threshold", threshold); // Current over limit threshold
		} else if (commandType == MOECommandType.GET_CURRENT_LOAD_LIMIT) {
			params.put("oid", "244.0.0"); // GPRS Connect : OID - 244.0.0
		} else {
			log.error("Unknown Command.");
			printResult(meter.getMdsId(), ResultStatus.INVALID_PARAMETER, "Unknown Command.");
			return;
		}

		Map<String, Object> result = excute.excute(commandType, meter, params);

		ResultStatus resultStatus = Boolean.parseBoolean(String.valueOf(result.get("RESULT"))) == true ? ResultStatus.SUCCESS : ResultStatus.FAIL;
		String resultDesc = String.valueOf(result.get("RESULT_DESC"));
		log.debug("ResultStatus = {}, ResultValue = {}", resultStatus, resultDesc);

		printResult(meter.getMdsId(), resultStatus, resultDesc);

		if (operationCode != null) {
			String opMessage = "";
			if (250 <= resultDesc.length()) {
				opMessage = resultDesc.substring(0, 250) + "....";
			} else {
				opMessage = resultDesc;
			}

			int supplierId = meter.getSupplierId();
			excute.saveOperationLog(supplierId, targetTypeCode, meter.getMdsId(), "system", operationCode, ResultStatus.SUCCESS.getCode(), ResultStatus.SUCCESS.name(), opMessage);
		}
	}

	@Override
	public String getName() {
		return meter.getMdsId();
	}

	@Override
	public void printResult(String title, ResultStatus status, String desc) {
		log.info(title + "," + status.name() + "," + desc);
	}

}
