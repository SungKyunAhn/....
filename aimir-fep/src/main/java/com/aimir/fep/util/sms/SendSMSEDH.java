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

public class SendSMSEDH implements SendSMS {
	
	private static Log log = LogFactory.getLog(SendSMSEDH.class);
	
	public String send(String mobileNo, String msg, Properties prop) throws MRPException  {
		log.debug("<<<<<<<<<<<< Send SMS in EDH >>>>>>>>>>>");
    	String baseURL = prop.getProperty("prepay.sms.baseUrl");
    	String SMSGHId = prop.getProperty("prepay.sms.id"); 
    	String SMSGHPass = prop.getProperty("prepay.sms.pass"); 
    	
    	URL url = null;
		HttpURLConnection urlConnection = null;
		String messageId = "fail";
		try {
		    String unicode = "0056006f0074007200650061006c0069006d0065006e0074006100740069006f006e";
	         String sendURL = baseURL + "username="+SMSGHId + "&password="+SMSGHPass + "&msisdn=" + mobileNo 
	                 + "&message=" + URLEncoder.encode(msg, "UTF-8");

			log.info(sendURL);
	 	    
	 	    url = new URL(sendURL);
			urlConnection = (HttpURLConnection)url.openConnection();
			log.info("Connection Timeout[" + urlConnection.getConnectTimeout() + "]");
			urlConnection.setConnectTimeout(10 * 1000);
			urlConnection.setDoOutput(true);
			
			log.debug(urlConnection.getResponseCode());
			log.debug(urlConnection.getResponseMessage());
		
			
			//URL에서 보내온 메세지 읽는 부분 : return message sample : {"Status":0,"MessageId":"f2ed79b703874d84b048adb24fec7d19","Rate":0.6,"NetworkId":"450"}
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine = null;

			int seqno = 0;
			String state = "";
			StringTokenizer st = null;
			while((inputLine = br.readLine()) != null) {
			    log.debug(inputLine);
			  //messaeId만 저장하기 위함
			    st = new StringTokenizer(inputLine, "|");
			    if(st.hasMoreTokens()){
				    seqno = Integer.parseInt(st.nextToken());			    	
			    }
			    if(st.hasMoreTokens()){
				    state = st.nextToken();
				    log.debug("STATE[" + state + "]");			    	
			    }
			    if(st.hasMoreTokens()){
				    messageId = st.nextToken();
				    log.debug("MESSAGE_ID[" + messageId + "]");			    	
			    }
			}
			br.close();
			
			//return messageId;
			
		} catch (Exception e) {
			log.warn(e,e);
		} finally {
			if(urlConnection != null && urlConnection.getURL() != null)
				urlConnection.disconnect();
		}
		return messageId;
	}
}
