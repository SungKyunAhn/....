/**
 * (@)# SendSMSKEMCOGabia.java
 *
 * 2015. 6. 26.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.util.sms;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.util.sms.api.gabia.ApiClass;
import com.aimir.fep.util.sms.api.gabia.ApiResult;

/**
 * @author nuri
 *
 */
public class SendSMSKEMCOGabia implements SendSMS {
	private static Log log = LogFactory.getLog(SendSMSKEMCO.class);

	@Override
	public String send(String mobileId, String sendMessage, Properties prop) throws MRPException {
		log.debug("<<<<<<<<<<<< Send SMS in KEMCO >>>>>>>>>>>");
		String smsId = prop.getProperty("kemco.sms.id");
		String smsApiKey = prop.getProperty("kemco.sms.key");
		String smsSendNumber = prop.getProperty("kemco.sms.send.number");
		String result = "error";
		
//		log.debug("##############  테스트 코드 지울것 01036598198 #########");
//		mobileId = "01036598198";
//		sendMessage = "454E4A30303030303031353132352E3134312E3134342E3135302C38313939";
		
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
				String resultXml = api.getResultXml(responseXml);
				log.debug("result xml : \n" + resultXml);
				result = "success";
			}else{
				String resultXml = api.getResultXml(responseXml);
				log.debug("result xml : \n" + resultXml);
				result = "fail";
			}
		}else{
			log.error("Please check sms properties.");
		}

		return result;
	}

}
