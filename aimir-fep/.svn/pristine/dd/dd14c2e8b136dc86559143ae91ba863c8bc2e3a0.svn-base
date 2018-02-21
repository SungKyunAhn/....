package com.aimir.fep.protocol.smsp.client.sms;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.DataCodings;
import org.jsmpp.bean.ESMClass;
import org.jsmpp.bean.NumberingPlanIndicator;
import org.jsmpp.bean.RegisteredDelivery;
import org.jsmpp.bean.SMSCDeliveryReceipt;
import org.jsmpp.bean.TypeOfNumber;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;

import com.aimir.util.TimeUtil;

/** 
 * SMS_Requester 
 * 
 * @version     1.0  2016.07.23 
 * @author		Sung Han LIM 
 */

public class SMPP_Submitter {
    private static Log logger = LogFactory.getLog(SMPP_Submitter.class);
    private static TimeFormatter timeFormatter = new AbsoluteTimeFormatter();
    private String eui_Id;
//    private static SMPPSession session;
    
    public String sendSMS(SMPPSession session, HashMap<String, Object> condition) throws IOException, ParseException, InterruptedException {
        
		String messageId = null;
		String hesPhonenumber = condition.get("hesPhonenumber").toString();
		String messageType = condition.get("messageType").toString();
		String currentTime = TimeUtil.getCurrentTime();
		
		String commandName = condition.get("commandName").toString();
		String commandCode = condition.get("commandCode").toString();
		String euiId = condition.get("euiId").toString();
		String MSISDN = condition.get("mobliePhNum").toString();
		byte[] sendMessage = (byte[]) condition.get("sendMessage");
		String cmdMap = condition.get("cmdMap").toString();
		
        // send Message
        // JpaTransactionManager txManager = null;
        // TransactionStatus txStatus = null;
        
        try {
            // set RegisteredDelivery
            final RegisteredDelivery registeredDelivery = new RegisteredDelivery();
            registeredDelivery.setSMSCDeliveryReceipt(SMSCDeliveryReceipt.SUCCESS_FAILURE);

            messageId = session.submitShortMessage("CMT", TypeOfNumber.UNKNOWN,
                    NumberingPlanIndicator.ISDN, hesPhonenumber, TypeOfNumber.INTERNATIONAL,
                    NumberingPlanIndicator.ISDN, MSISDN, new ESMClass(), (byte) 0, (byte) 1,
                    timeFormatter.format(new Date()), null, new RegisteredDelivery(SMSCDeliveryReceipt.SUCCESS_FAILURE),
                    (byte) 0, DataCodings.ZERO, (byte) 0, sendMessage);

            logger.info("====================================");
            logger.info("MSG [" + messageId + "] submitted");
            logger.info("====================================");
            
            // SMS 비동기 명령 저장 로직(S)
            // TODO
            // SMS 비동기 명령 저장 로직(E)
            
        } catch (PDUException e) {
            logger.error("FAIL - Invalid PDU parameter", e);
            messageId = "FAIL";
        } catch (ResponseTimeoutException e) {
            logger.error("FAIL - Response timeout", e);
            messageId = "FAIL";
        } catch (InvalidResponseException e) {
            logger.error("FAIL - Receive invalid respose", e);
            messageId = "FAIL";
        } catch (NegativeResponseException e) {
            logger.error("FAIL - Receive negative response", e);
            messageId = "FAIL";
        } catch (IOException e) {
            logger.error("FAIL - IO error occur", e);
            messageId = "FAIL";
        } catch (Exception e) { 
            logger.error(e, e);
            messageId = "FAIL";
        }
        
		Thread.sleep(20000); // 20초 대기
		logger.info("It's been 20 seconds MSG [" + messageId + "] sent. Check result.");
		
        return messageId;
    }
    
    public String getEui_Id() {
        return eui_Id;
    }

    public void setEui_Id(String eui_Id) {
        this.eui_Id = eui_Id;
    }
}