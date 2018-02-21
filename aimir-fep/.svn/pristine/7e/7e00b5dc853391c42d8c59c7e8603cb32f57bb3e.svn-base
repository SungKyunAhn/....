package com.aimir.fep.util.sms;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.mrp.exception.MRPException;

public class SendSMSTest implements SendSMS {
	
	private static Log log = LogFactory.getLog(SendSMSTest.class);
	
	/**
	 * 데모 용으로 만든 SendSMS파일
	 * 테스트 용 ( 어플 이름 : SMS Gateway/ 어플 만든이 : EASCIT NET )
	 * 테스트 방법 : 1. http://www.ezsmsgateway.com 사이트 접속 후 계정 생성
	 *           2. SMS Gateway 어플 다운 후 생성한 계정으로 로그인
	 *           3. Test 진행 
	 * 
	 */
	public String send(String mobileNo, String msg, Properties prop) throws MRPException  {
	    log.debug("<<<<<<<<<<<< Send SMS in Test >>>>>>>>>>>");
        
        URL url = null;
        HttpURLConnection urlConnection = null;
        String account = prop.getProperty("prepay.sms.id");
        String pass = prop.getProperty("prepay.sms.pass");
        boolean UseGateWayApp = false;
        String messageId="";
        try{
        	UseGateWayApp = Boolean.getBoolean(prop.getProperty("prepay.sms.useGatewayApp"));        	
        }catch (Exception e){        	
        }
        
        try {
            String sendURL = prop.getProperty("prepay.sms.baseUrl") + "?a="+mobileNo+"&b="+URLEncoder.encode(msg, "utf-8")+"&e="+account+"&p="+pass;
            
            log.info("[Use 'www.ezsmsgateway.com' Test=" + UseGateWayApp + "] " + sendURL);
            
            if(UseGateWayApp){
                url = new URL(sendURL);
                urlConnection = (HttpURLConnection)url.openConnection();
    			log.info("Connection Timeout[" + urlConnection.getConnectTimeout() + "]");
    			urlConnection.setConnectTimeout(10 * 1000);
                urlConnection.setDoOutput(true);
                
                log.debug(urlConnection.getResponseCode());
                log.debug(urlConnection.getResponseMessage());
                messageId = "success";
                return messageId;
            }
        } catch (Exception e) {
            log.warn(e,e);
        } finally {
            if(urlConnection != null && urlConnection.getURL() != null)
                urlConnection.disconnect();
        }
        
        return "fail";
	}
}
