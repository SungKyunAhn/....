package com.aimir.fep.util.sms;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.mrp.exception.MRPException;

public class SendSMSKPLC implements SendSMS {
	
	private static Log log = LogFactory.getLog(SendSMSECG.class);
	/**
	 * Sample URL 
	 * http://41.215.137.53/?METHOD=HTTP&USERNAME=nuri&PASSWORD=3f587fd58b44ce8d06795814e819563f&SOURCE=KenyaPower&MSISDN=254724385922&MESSAGE=Testing smartMeter
	 * id 		: nuri
	 * password : !@3qweASD
	 * (*password have to use MD5)
	 * 
	 */
	public String send(String mobileNo, String msg, Properties prop) throws MRPException  {
		log.info("<<<<<<<<<<<< Send SMS in KPLC >>>>>>>>>>>");
    	String baseURL = prop.getProperty("prepay.sms.baseUrl");
    	String USERNAME  = prop.getProperty("prepay.sms.id"); 
    	String PASSWORD  = prop.getProperty("prepay.sms.pass");
    	String SOURCE = prop.getProperty("sms.from");

    	PASSWORD = encryptMD5(PASSWORD);
    	log.info("msg : "+msg);
    	URL url = null;
		HttpURLConnection urlConnection = null;
		String messageId = "fail";
		try {
			String sendURL = baseURL + "?METHOD=HTTP&USERNAME=" + USERNAME + "&PASSWORD=" + PASSWORD 
	        		 + "&SOURCE="+SOURCE+"&MSISDN=" + URLEncoder.encode(mobileNo, "utf-8") + "&MESSAGE=" + URLEncoder.encode(msg, "utf-8");  

			log.info(sendURL);
	 	    
	 	    url = new URL(sendURL);
			urlConnection = (HttpURLConnection)url.openConnection();
			log.info("Connection Timeout[" + urlConnection.getConnectTimeout() + "]");
			urlConnection.setConnectTimeout(10 * 1000);
			urlConnection.setDoOutput(true);
			
			log.info(urlConnection.getResponseCode());
			log.info(urlConnection.getResponseMessage());
		

			//URL에서 보내온 메세지 읽는 부분 : return message sample : {"MESSAGE":"SUCCESS SEND", "MESSAGEID":86}
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine = null;

			while((inputLine = br.readLine()) != null) {
			  //messaeId만 저장하기 위함
                String returnData[] = inputLine.split(",");
                for (String data : returnData) {
                    if(data.contains("MESSAGEID")) {
                    	messageId = data.replace("\"", "");
                        messageId = messageId.replace("MESSAGEID:", "");
                        messageId = messageId.replace("}", "");
                        messageId = messageId.trim();
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
	
	private String encryptMD5(String str) {
		String MD5 = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte byteData[] = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}
			MD5 = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			MD5 = null;
		}
		return MD5.toLowerCase();
	} 
}
