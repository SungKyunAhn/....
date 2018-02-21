package com.aimir.fep.util.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.mrp.exception.MRPException;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.sms.api.gabia.ApiClass;
import com.aimir.fep.util.sms.api.gabia.ApiResult;

public class SendSMSEVN implements SendSMS {
	
	private static Log log = LogFactory.getLog(SendSMSEVN.class);
	
	@Override
	public String send(String mobileId, String sendMessage, Properties prop) throws MRPException {
		log.debug("<<<<<<<<<<<< Send SMS in EVN >>>>>>>>>>>");
		String smsId = prop.getProperty("evn.sms.id");
		String smsApiKey = prop.getProperty("evn.sms.key");
		String smsSendNumber = prop.getProperty("evn.sms.send.number");
		String result = "error";
		
		log.info("SEND SMS => [" + mobileId + "] [" + sendMessage + "]");
		
		result = "success";

		return result;
	}
}
