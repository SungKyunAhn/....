package com.aimir.fep.util.sms;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionStatus;

import com.aimir.constants.CommonConstants.OperatorType;
import com.aimir.constants.CommonConstants.TR_OPTION;
import com.aimir.constants.CommonConstants.TR_STATE;
import com.aimir.dao.device.AsyncCommandLogDao;
import com.aimir.dao.device.AsyncCommandParamDao;
import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.protocol.smsp.SMSConstants.COMMAND_TYPE;
import com.aimir.fep.protocol.smsp.command.frame.sms.RequestFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.sms.api.gabia.ApiClass;
import com.aimir.fep.util.sms.api.gabia.ApiResult;
import com.aimir.model.device.AsyncCommandLog;
import com.aimir.model.device.AsyncCommandParam;
import com.aimir.util.TimeUtil;

public class SMSServiceGabiaClient implements SMSServiceClient {

	private static Log log = LogFactory.getLog(SMSServiceGabiaClient.class);
	
	@Override
	public String execute(HashMap<String, Object> condition,
			List<String> paramList, String cmdMap) throws Exception {

    	String commandName = condition.get("commandName").toString();
    	String messageType = condition.get("messageType").toString();
    	String euiId = condition.get("euiId").toString();
    	String mobliePhNum = condition.get("mobliePhNum").toString();
    	String commandCode = condition.get("commandCode").toString();
    	String hashCode = condition.get("hashCode").toString();
    	String[] param = null;
    	//String sequence = null;
    	
		if (paramList != null) {
        	param = new String[paramList.size()];
			param = paramList.toArray(param);
    	}
		
		// Sequence 생성 로직 (S)
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmssSSS");
		String sequence = dateFormat.format(calendar.getTime());
    	RequestFrame reqFrame = new RequestFrame(messageType, sequence, hashCode, commandCode, param);
    	byte[] sendMessage = reqFrame.encode();

        // send Message
        JpaTransactionManager txManager = null;
        TransactionStatus txStatus = null;
        //String messageId = null;
        String result = null;
        
        try {
            String currentTime = TimeUtil.getCurrentTime();

            result = send(mobliePhNum, new String(sendMessage));

            log.info("====================================");
            log.info("MSG [" + sequence + "] submitted");
            log.info("====================================");
            
            // SMS 비동기 명령 저장 로직(S)
            txManager = (JpaTransactionManager) DataUtil.getBean("transactionManager");
            txStatus = txManager.getTransaction(null);

            AsyncCommandLogDao asyncCommandLogDao = DataUtil.getBean(AsyncCommandLogDao.class);
            AsyncCommandLog asyncCommandLog = new AsyncCommandLog();

            asyncCommandLog.setTrId(Long.parseLong(sequence));
            asyncCommandLog.setMcuId(euiId);
            asyncCommandLog.setDeviceType("MBB");
            asyncCommandLog.setDeviceId(euiId);
            asyncCommandLog.setCommand(commandName);
            asyncCommandLog.setTrOption(TR_OPTION.ASYNC_OPT_RETURN_DATA_SAVE.getCode());
            if(commandCode.contains(COMMAND_TYPE.NI.getTypeCode())
                    || commandCode.contains(COMMAND_TYPE.COAP.getTypeCode())
                    || commandCode.contains(COMMAND_TYPE.SNMP.getTypeCode()) ){
                asyncCommandLog.setState(TR_STATE.Waiting.getCode());
            }else{
                asyncCommandLog.setState(TR_STATE.Running.getCode());
            }            
            asyncCommandLog.setOperator(OperatorType.OPERATOR.name());
            asyncCommandLog.setCreateTime(currentTime);
            asyncCommandLog.setRequestTime(currentTime);
            asyncCommandLog.setLastTime(null);
            asyncCommandLogDao.add(asyncCommandLog);
            
            AsyncCommandParam asyncCommandParam = new AsyncCommandParam();
            AsyncCommandParamDao asyncCommandParamDao = DataUtil.getBean(AsyncCommandParamDao.class);
            
            // byte value of sms
            asyncCommandParam.setMcuId(euiId);
            asyncCommandParam.setNum(0);
            asyncCommandParam.setTrId(Long.parseLong(sequence));
            asyncCommandParam.setParamType("byte");
            asyncCommandParam.setParamValue(Hex.decode(sendMessage));
            asyncCommandParam.setTrType("SMS");
            asyncCommandParamDao.add(asyncCommandParam);
            
            // commandcode is used to set the protocol type (NI or COAP or ETC)
            asyncCommandParam = new AsyncCommandParam();
            asyncCommandParam.setMcuId(euiId);
            asyncCommandParam.setNum(1);
            asyncCommandParam.setTrId(Long.parseLong(sequence));
            asyncCommandParam.setParamType("CommandCode");
            asyncCommandParam.setParamValue(commandCode);
            asyncCommandParam.setTrType("CommandCode");
            asyncCommandParamDao.add(asyncCommandParam);
            
            if (cmdMap != null) {
                ObjectMapper cmdMapper = new ObjectMapper();
                Map<String, String> mapForCmd =  cmdMapper.readValue(cmdMap , new TypeReference<Map<String, String>>(){});
                
                if (mapForCmd != null) {
                    Iterator<String> keys = mapForCmd.keySet().iterator();
                    int paramNum = 2;              
                    while (keys.hasNext()) {
                        String key = keys.next();
    
                        asyncCommandParam = new AsyncCommandParam();
                        asyncCommandParam.setTrType("CMD");
                        asyncCommandParam.setParamType(key);
                        asyncCommandParam.setParamValue(mapForCmd.get(key));
                        asyncCommandParam.setMcuId(euiId);
                        asyncCommandParam.setNum(paramNum++);
                        asyncCommandParam.setTrId(Long.parseLong(sequence));
                        
                        asyncCommandParamDao.add(asyncCommandParam);
                    }
                }
            } 
            
            txManager.commit(txStatus);
            
            log.info("====================================");
            log.info("MSG [" + sequence + "] Info Save - OK");
            log.info("====================================");
            // SMS 비동기 명령 저장 로직(E)
            
        } catch (IOException e) {
            log.error("FAIL - IO error occur", e);
            sequence = "FAIL";
        } catch (Exception e) { 
            log.error(e, e);
            sequence = "FAIL";
        } finally {
        	if(result != null && result.equals("fail")){
        		sequence = "FAIL";
        	}
        }
		return sequence;
		
	}

	public String send(String mobileId, String sendMessage) throws MRPException {
		log.debug("<<<<<<<<<<<< Send SMS in Gabia >>>>>>>>>>>");
		String smsId = FMPProperty.getProperty("gabia.sms.id");
		String smsApiKey = FMPProperty.getProperty("gabia.sms.key");
		String smsSendNumber = FMPProperty.getProperty("gabia.sms.send.number");
		String result = "error";
		
		if(smsId != null && smsApiKey != null){
			ApiClass api = new ApiClass(smsId, smsApiKey);

			// 단문 발송 테스트
			String arr[] = new String[7];
			arr[0] = "sms"; // 발송 타입 sms or lms
			arr[1] = sendMessage.substring(0, 12); // 결과 확인을 위한 KEY ( 중복되지 않도록 생성하여 전달해 주시기 바랍니다. )
			arr[2] = "OTA"; //  LMS 발송시 제목으로 사용 SMS 발송시는 수신자에게 내용이 보이지 않음.
			arr[3] = sendMessage; // 본문 (90byte 제한)
			arr[4] = smsSendNumber; // 인증받은 발신 번호
			arr[5] = mobileId; // 수신 번호
			arr[6] = "0"; //예약 일자 "2013-07-30 12:00:00" 또는 "0" 0또는 빈값(null)은 즉시 발송 

			String responseXml = api.send(arr);
			ApiResult res = api.getResult(responseXml);
			log.info("[SEND SMS]To=" + mobileId + ", Msg=" + sendMessage + ", ResultCode =" + res.getCode() + ", ResultMsg=" + res.getMesg());
			
			if (res.getCode().compareTo("0000") == 0) {
				//String resultXml = api.getResultXml(responseXml);
				//log.debug("result xml : \n" + resultXml);
				result = "success";
			}else{
				//String resultXml = api.getResultXml(responseXml);
				//log.debug("result xml : \n" + resultXml);
				result = "fail";
			}
		}else{
			log.error("Please check sms properties.");
		}

		return result;
	}
}
