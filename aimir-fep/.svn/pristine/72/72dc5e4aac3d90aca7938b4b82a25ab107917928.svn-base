package com.aimir.fep.util.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.mrp.exception.MRPException;

public class SendSMSECG implements SendSMS {
	
	private static Log log = LogFactory.getLog(SendSMSECG.class);
	
	public String send(String mobileNo, String msg, Properties prop) throws MRPException  {
		log.info("<<<<<<<<<<<< Send SMS in ECG >>>>>>>>>>>");
    	String baseURL = prop.getProperty("prepay.sms.baseUrl");
    	String SMSGHId = prop.getProperty("prepay.sms.id"); 
    	String SMSGHPass = prop.getProperty("prepay.sms.pass"); 
    	
    	URL url = null;
		HttpURLConnection urlConnection = null;
		String messageId = "fail";
		try {
	         String sendURL = baseURL + "messages/send?" + "From=ECG&To=" + URLEncoder.encode(mobileNo, "utf-8") 
	                 + "&Content=" + URLEncoder.encode(msg, "utf-8") + "&ClientId=" + SMSGHId + "&ClientSecret=" + SMSGHPass + "&RegisteredDelivery=true";

			log.info(sendURL);
	 	    
	 	    url = new URL(sendURL);
			urlConnection = (HttpURLConnection)url.openConnection();
			log.info("Connection Timeout[" + urlConnection.getConnectTimeout() + "]");
			urlConnection.setConnectTimeout(10 * 1000);
			urlConnection.setReadTimeout(10*1000);
			urlConnection.setDoOutput(true);
			
			log.info(urlConnection.getResponseCode());
			log.info(urlConnection.getResponseMessage());
		
			
			//URL에서 보내온 메세지 읽는 부분 : return message sample : {"Status":0,"MessageId":"f2ed79b703874d84b048adb24fec7d19","Rate":0.6,"NetworkId":"450"}
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine = null;

			while((inputLine = br.readLine()) != null) {
			  //messaeId만 저장하기 위함
                String returnData[] = inputLine.split(",");
                for (String data : returnData) {
                    if(data.contains("MessageId")) {
                        messageId = data.replace("\"MessageId\":\"", "");
                        messageId = messageId.replace("\"", "");
                        break;
                    }
                }
			}
			br.close();
			log.info("messageId : "+messageId);
			if(urlConnection.getResponseCode() == 201 || urlConnection.getResponseCode() == 200) {
				//return messageId;
			} else {
				messageId = "fail";
			}
			
		} catch (Exception e) {
			log.warn(e,e);
		} finally {
			if(urlConnection != null && urlConnection.getURL() != null)
				urlConnection.disconnect();
		}
		return messageId;
	}
}
