/**
 * 
 */
package com.aimir.fep.tool.batch;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.dao.device.AsyncCommandResultDao;
import com.aimir.dao.device.OperationLogDao;
import com.aimir.dao.system.SupplierDao;
import com.aimir.fep.tool.MOECommandBatchTool.MOECommandType;
import com.aimir.fep.tool.batch.job.CurrentLoadLimitJob;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.sms.SendSMS;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.model.device.AsyncCommandResult;
import com.aimir.model.device.MMIU;
import com.aimir.model.device.Meter;
import com.aimir.model.device.Modem;
import com.aimir.model.device.OperationLog;
import com.aimir.model.system.Code;
import com.aimir.model.system.Supplier;
import com.aimir.util.TimeUtil;

/**
 * @author simhanger
 *
 */
public class AsyncCommandExcute_MOE {
	private static Logger log = LoggerFactory.getLogger(CurrentLoadLimitJob.class);

	public Map<String, Object> excute(MOECommandType commandType, Meter meter, Map<String, String> paramMap) {
		Map<String, Object> result = new HashMap<String, Object>();
		String modemDeviceSerial = "";

		try {
			Modem modem = meter.getModem();
			modemDeviceSerial = modem.getDeviceSerial();
			log.debug("Meter={}, Modem={}, commandType={}, param={}", meter.getMdsId(), modemDeviceSerial, commandType, paramMap.toString());

			if (modemDeviceSerial == null || "".equals(modemDeviceSerial)) {
				result.put("RESULT", false);
				result.put("RESULT_DESC", "FAIL : Phone number is empty!");
			} else if (!Protocol.SMS.equals(modem.getProtocolType())) {
				result.put("RESULT", false);
				result.put("RESULT_DESC", "FAIL : Invalid ProtocolType!");
			} else if (!"0102".equals(modem.getProtocolVersion())) {
				result.put("RESULT", false);
				result.put("RESULT_DESC", "FAIL : Invalid ProtoclVersion!");
			} else {

			}

			AsyncCommandLogDao asyncCommandLogDao = DataUtil.getBean(AsyncCommandLogDao.class);
			Long maxTrId = asyncCommandLogDao.getMaxTrId(modem.getDeviceSerial());
			String trnxId;
			if (maxTrId != null) {
				trnxId = String.format("%08d", maxTrId.intValue() + 1);
			} else {
				trnxId = "00000001";
			}

			/*
			 * 비동기 명령 저장 : SMS발송보다 먼저 저장함.
			 */
			saveAsyncCommandForMOE(modem.getDeviceSerial(), Long.parseLong(trnxId), commandType.getCmd(), paramMap, TimeUtil.getCurrentTime());

			/*
			 * SMS 발송
			 */
			String messageId = sendSMSForMOE(paramMap.get("oid"), trnxId, modemDeviceSerial, commandType.getCmd());
			log.debug("TransactionId = {}, returned messageId = {}", trnxId, messageId);
			
			/*
			 * 결과 처리
			 */
			if (messageId.equals("fail")) {
				log.info("FAIL : [" + modemDeviceSerial + "]Send SMS Fail.   ");

				result.put("RESULT", false);
				result.put("RESULT_DESC", "FAIL : [" + modemDeviceSerial + "]Send SMS Fail.   ");

			} else if (messageId.equals("error")) {
				log.info("FAIL : Invalid Ip Address or Port!   ");

				result.put("RESULT", false);
				result.put("RESULT_DESC", "FAIL : Invalid Ip Address or Port!   ");
			} else {
				// 상태가 바뀌는 시간을 기다려주기 위해 sleep
				if (commandType == MOECommandType.GET_DEMAND_PERIOD || commandType == MOECommandType.SET_DEMAND_PERIOD) {
					Thread.sleep(120000);
				} else {
					Thread.sleep(60000);
				}
				
				AsyncCommandLogDao logDao = DataUtil.getBean(AsyncCommandLogDao.class);
				Integer lastStatus = logDao.getCmdStatus(modem.getDeviceSerial(), commandType.getCmd());

				AsyncCommandResultDao resultDao = DataUtil.getBean(AsyncCommandResultDao.class);

				if (TR_STATE.Success.getCode() != lastStatus) {
					log.debug("FAIL : [" + modemDeviceSerial + "] Communication Error(" + commandType.getCmd() + ") but Send SMS Success.   ");

					result.put("RESULT", false);
					result.put("RESULT_DESC", "FAIL : [" + modemDeviceSerial + "] Communication Error(" + commandType.getCmd() + ") but Send SMS Success.   ");
				} else {
					log.debug("Success : [" + modemDeviceSerial + "] Command Result[" + commandType.getCmd() + "]");

					result.put("RESULT", true);

					if (commandType == MOECommandType.SET_CURRENT_LOAD_LIMIT) {
						List<AsyncCommandResult> acplist = resultDao.getCmdResults(modem.getDeviceSerial(), Long.parseLong(trnxId), null);
						if (acplist == null || acplist.size() <= 0) {
							result.put("RESULT", false);
							result.put("RESULT_DESC", "RESULT_STATUS=Empty~!!   ");
						} else {
							String tempStr = "";
							for (AsyncCommandResult param : acplist) {
								tempStr += param.getResultType().equals("RESULT_VALUE") ? "Set Current Load Limit => " + param.getResultValue() : "    ";
							}
							result.put("RESULT_DESC", tempStr);
						}
						log.debug("cmdSetCurrentLoadLimit returnValue =>> " + result.get("RESULT_DESC"));
					} else if (commandType == MOECommandType.GET_CURRENT_LOAD_LIMIT) {
						List<AsyncCommandResult> acplist = resultDao.getCmdResults(modem.getDeviceSerial(), Long.parseLong(trnxId), null);
						if (acplist == null || acplist.size() <= 0) {
							result.put("RESULT", false);
							result.put("RESULT_DESC", "RESULT_STATUS=Empty~!!   ");
						} else {
							String tempStr = "";
							for (AsyncCommandResult param : acplist) {
								tempStr += param.getResultType().equals("RESULT_VALUE") ? param.getResultValue() : "   ";
							}
							result.put("RESULT_DESC", tempStr);
						}
						log.debug("cmdGetCurrentLoadLimit returnValue =>> " + result.get("RESULT_DESC"));
					} else {
						throw new Exception("Unknown Command.");
					}
				}
			}
		} catch (Exception e) {
			log.error("FAIL : Send SMS Fail - [" + commandType.getCmd() + "][" + modemDeviceSerial + "]", e);

			result.put("RESULT", false);
			result.put("RESULT_DESC", "FAIL : Send SMS Fail - [" + commandType.getCmd() + "][" + modemDeviceSerial + "]");
		}

		return result;
	}

	private void saveAsyncCommandForMOE(String deviceSerial, Long trId, String cmd, Map<String, String> param, String currentTime) throws Exception {
		JpaTransactionManager txManager = null;
		TransactionStatus txStatus = null;
		
		log.debug("추적1");
		try {
			DefaultTransactionDefinition txDefine = new DefaultTransactionDefinition();
			//txDefine.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
			txDefine.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
			
			txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			txStatus = txManager.getTransaction(txDefine);
			
			log.debug("추적2");
			AsyncCommandLogDao commandLogDao = DataUtil.getBean(AsyncCommandLogDao.class);
			AsyncCommandLog asyncCommandLog = new AsyncCommandLog();
			asyncCommandLog.setTrId(trId);
			asyncCommandLog.setMcuId(deviceSerial);
			asyncCommandLog.setDeviceType(McuType.MMIU.name());
			asyncCommandLog.setDeviceId(deviceSerial);
			asyncCommandLog.setCommand(cmd);
			asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
			asyncCommandLog.setState(1);
			asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
			asyncCommandLog.setCreateTime(currentTime);
			asyncCommandLog.setRequestTime(currentTime);
			asyncCommandLog.setLastTime(null);
			commandLogDao.add(asyncCommandLog);
			log.debug("추적3");
			Integer num = 0;
			if (param != null && param.size() > 0) {
				log.debug("추적4");
				// parameter가 존재할 경우.
				AsyncCommandParamDao commandParamDao = DataUtil.getBean(AsyncCommandParamDao.class);
				Integer maxNum = commandParamDao.getMaxNum(deviceSerial, trId);

				if (maxNum != null) {
					num = maxNum + 1;
				}

				Iterator<String> iter = param.keySet().iterator();
				while (iter.hasNext()) {
					log.debug("추적5");
					String key = iter.next();

					AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
					asyncCommandParam.setMcuId(deviceSerial);
					asyncCommandParam.setNum(num);
					asyncCommandParam.setParamType(key);
					asyncCommandParam.setParamValue((String) param.get(key));
					asyncCommandParam.setTrId(trId);

					commandParamDao.add(asyncCommandParam);
					num += 1;
				}
				log.debug("추적6");
			}
			log.debug("추적7");
			txManager.commit(txStatus);
		} catch (Exception e) {
			log.debug("추적8");
			if (txStatus != null)
				txManager.rollback(txStatus);
			log.error("AsyncCommandLog saving error - ", e);
		} finally{
			log.debug("추적9");
			txStatus = null;
			txManager = null;
		}

	}

	private String sendSMSForMOE(String oid, String trnxId, String mobileNo, String command) {
		String result = "";

		try {
			int seq = new Random().nextInt(100) & 0xFF;

//			Properties prop = new Properties();
//			prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));

			Properties prop = FMPProperty.getProperties();
			String smsClassPath = prop.getProperty("moe.smsClassPath");
			String serverIp = prop.getProperty("moe.server.sms.serverIpAddr") == null ? "" : prop.getProperty("moe.server.sms.serverIpAddr").trim();
			String serverPort = prop.getProperty("moe.server.sms.serverPort") == null ? "" : prop.getProperty("moe.server.sms.serverPort").trim();

			if ("".equals(serverIp) || "".equals(serverPort)) {
				result = "error";
				log.debug("========>>> [{" + command + "}] Message Send Error: Invalid Ip Address or port!");
			} else {
				String smsMsg = cmdMsg((byte) seq, oid, serverIp.replaceAll("\\.", ","), serverPort);
				SendSMS obj = (SendSMS) Class.forName(smsClassPath).newInstance();
				Method m = obj.getClass().getDeclaredMethod("send", String.class, String.class, Properties.class);
				// result = (String) m.invoke(obj, mobileNo.replace("-",
				// "").trim(), smsMsg, prop);
				result = "success";
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}

	private String cmdMsg(byte seq, String oid, String ip, String port) {
		int sequence = (int) (seq & 0xFF);
		String smsMsg = "NT,";
		if (sequence >= 10 && sequence < 100) {
			smsMsg += "0" + sequence;
		} else if (sequence < 10) {
			smsMsg += "00" + sequence;
		} else {
			smsMsg += "" + sequence;
		}
		smsMsg += ",Q,B," + oid + "," + ip + "," + port;

		return smsMsg;
	}

	//public void saveOperationLog(Supplier supplier, Code targetTypeCode, String targetName, String userId, Code operationCode, Integer status, String errorReason) {
	public void saveOperationLog(int supplierId, Code targetTypeCode, String targetName, String userId, Code operationCode, Integer status, String errorReason, String desc) {
		try {
//			log.info("supplierId = " + supplierId);
//			log.info("targetTypeCode = " + (targetTypeCode != null ? targetTypeCode.getName() : "null"));
//			log.info("targetName = " + (targetName != null ? targetName : "null"));
//			log.info("userId = " + (userId != null ? userId : "null"));
//			log.info("operationCode = " + (operationCode != null ? operationCode.getName() : "null"));
//			log.info("status = " + status);
//			log.info("errorReason = " + (errorReason != null ? errorReason : "null"));
//			log.info("desc = " + (desc != null ? desc : "null"));

			JpaTransactionManager txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
			TransactionStatus txStatus = txManager.getTransaction(null);
			
			SupplierDao supplierDao = DataUtil.getBean(SupplierDao.class);
			Supplier supplier = supplierDao.get(supplierId);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			Calendar today = Calendar.getInstance();
			String currDateTime = sdf.format(today.getTime());

			OperationLog operationLog = new OperationLog();
			operationLog.setOperatorType(0); // system
			operationLog.setOperationCommandCode(operationCode);
			operationLog.setYyyymmdd(currDateTime.substring(0, 8));
			operationLog.setHhmmss(currDateTime.substring(8, 14));
			operationLog.setYyyymmddhhmmss(currDateTime);
			operationLog.setDescription(desc);
			operationLog.setErrorReason(errorReason);
			operationLog.setResultSrc("");
			operationLog.setStatus(status);
			operationLog.setTargetName(targetName);
			operationLog.setTargetTypeCode(targetTypeCode);
			operationLog.setUserId(userId);
			operationLog.setSupplier(supplier);

//			log.info("operation log: " + operationLog.toString());

			OperationLogDao operationLogDao = DataUtil.getBean(OperationLogDao.class);
			operationLogDao.add(operationLog);

			txManager.commit(txStatus);
		} catch (Exception e) {
			log.error("Operation log saving error - " + e.getMessage());
		}

	}

}
