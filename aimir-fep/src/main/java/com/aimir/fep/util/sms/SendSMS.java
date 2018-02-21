package com.aimir.fep.util.sms;

import java.util.Properties;

import com.aimir.fep.protocol.mrp.exception.MRPException;

public interface SendSMS {
	/**
	 * 각 사이트별 SMS를 보내는 로직
	 * 
	 * @param mobileId
	 * @param sendMessage
	 * @return
	 */
	public String send(String mobileId, String sendMessage, Properties prop) throws MRPException;
}
