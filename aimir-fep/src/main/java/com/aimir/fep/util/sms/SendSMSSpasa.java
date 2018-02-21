package com.aimir.fep.util.sms;

import java.net.URLEncoder;
import java.util.Properties;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpConnectionManager;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.mrp.exception.MRPException;

public class SendSMSSpasa implements SendSMS {
private static Log log = LogFactory.getLog(SendSMSSpasa.class);
	
	public String send(String mobileNo, String msg, Properties prop) throws MRPException { 
		log.debug("<<<<<<<<<<<< Send SMS in Spasa >>>>>>>>>>>"); 
		
		HttpClient client = new HttpClient();
		
		String baseURL = prop.getProperty("prepay.sms.baseUrl");
		String username = prop.getProperty("prepay.sms.id");
		String pass = prop.getProperty("prepay.sms.pass");
		String campaignId = prop.getProperty("prepay.sms.campaignId");
		String proxyServer = prop.getProperty("prepay.sms.proxy.server");
		int proxyPort = Integer.parseInt(prop.getProperty("prepay.sms.proxy.port"));
		
		PostMethod method = null;
		HostConfiguration proxy = null;
		String messageId = "fail";
		try {
//			sampleURL = http://relay1.za.oxygen8.com:8088/SPASA?CampaignID=41659&username=SPASA&password=Sp@sa123&MSISDN=0768232386&content=spasa%20sms%20test
			String sendURL = baseURL+"CampaignId="+campaignId+"&username="+username+"&password="+pass+
			        "&MSISDN="+URLEncoder.encode(mobileNo, "utf-8")+"&content="+URLEncoder.encode(msg, "utf-8");
			log.info("sendURL["+sendURL+"]");
			//proxy 설정
			proxy = new HostConfiguration();
            proxy.setProxy(proxyServer, proxyPort);
            
            //Timeout 설정
            HttpConnectionManagerParams managerParam = new HttpConnectionManagerParams();
			HttpConnectionManager manager = new SimpleHttpConnectionManager();
			managerParam.setConnectionTimeout(10*1000);
			client.setHttpConnectionManager(manager);
			
			method = new PostMethod(sendURL);
			method.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(0, false));
			int status  = client.executeMethod(proxy, method);
			String oxygenStatus = "";
			String statusMsg = "";
			//URL에서 보내온 메세지 읽는 부분 : 
			/*
			return message sample : 
			101
			Message accepted
			1.321.1463031288.2
			*/
			
			String body = method.getResponseBodyAsString();

			if(body != null && !body.isEmpty()) {
				//messaeId만 저장하기 위함
				if(body.contains("101")) {
					String[] response = body.split("\\n");
					if(response.length == 3) {
						messageId = response[2];
						statusMsg = response[1];
						oxygenStatus = response[0];
					} else {
						log.info("response contents : \n" + body);
					}
				} else {
					String[] response = body.split("\\n");
					if(response.length == 3) {
						oxygenStatus = response[0];
						log.warn(response[1]);
					}
				}
				messageId = messageId.replace("key=\"", "");
			}

			log.info("Status[" + status + "], OxygenStatus[" + oxygenStatus + "] statusMsg[" + statusMsg + "]");
			if(status == 200) {
				log.debug("messageId : "+messageId);
				//return messageId;
			}else {
				messageId="fail";
			}
		}
		catch (Exception e) {
			log.warn(e,e);
		} finally {
			method.releaseConnection();
		}
		
		return messageId;
    }
}
