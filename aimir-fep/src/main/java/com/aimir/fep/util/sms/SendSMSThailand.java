package com.aimir.fep.util.sms;

import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.mrp.exception.MRPException;

public class SendSMSThailand implements SendSMS {
	private static Log log = LogFactory.getLog(SendSMSThailand.class);
	
	public String send(String mobileId, String sendMessage, Properties prop) throws MRPException { 
    	log.debug("<<<<<<<<<<<< Send SMS in Thailand >>>>>>>>>>>"); 
    	
        String smsHost = prop.getProperty("sms.hostname","127.0.0.1");
        String smsPort = prop.getProperty("sms.port","81");
        String smsPath = prop.getProperty("sms.path","sendsms.php");
        String smsPassword = prop.getProperty("sms.password","meath");
    	
    	String mobileNumber = "66" + mobileId.substring(1);
        log.info(String.format("sendSMSTahiland\n phoneNumber[%s], smsHost[%s], smsPath[%s], password[%s]",  smsHost, smsPort, smsPath, smsPassword));
        log.info(String.format("sendMessage [%s], mobileNumber [%s]",sendMessage, mobileNumber));
        String messageId="";
        try {
			HttpClient client = new HttpClient();
			//smsHost:port/sendsms?phone=xxxxx&text=xxxx&password=meath
			PostMethod method = new PostMethod();
			method.setPath("http://"+smsHost+":"+smsPort+"/"+smsPath+"?");
			
		    //SENDSMS
			NameValuePair[] params = new NameValuePair[3];
		    params[0] = makeQueryString("phone",mobileNumber);
		    params[1] = makeQueryString("text", sendMessage);
		    params[2] = makeQueryString("password", smsPassword);
		    
		    method.setRequestBody(params);
		    
            //Timeout 설정
            HttpConnectionManagerParams managerParam = new HttpConnectionManagerParams();
			HttpConnectionManager manager = new SimpleHttpConnectionManager();
			managerParam.setConnectionTimeout(10*1000);
			client.setHttpConnectionManager(manager);
		    
		    int result = client.executeMethod(method);
		    log.debug("Result[" + result + "] Response[" + method.getResponseBodyAsString() + "]");
		    messageId="success";
		    return messageId;
        }
        catch (Exception e) {
            log.warn(e);
        }
        return "fail";
    }
	
    private NameValuePair makeQueryString(String name, String value) {
        NameValuePair param = new NameValuePair();
        param.setName(name);
        param.setValue(value);
        return param;
    }
}
