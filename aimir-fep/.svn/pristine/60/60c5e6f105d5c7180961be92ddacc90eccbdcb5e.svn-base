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

public class SendSMSKEMCO implements SendSMS {

	private static Log log = LogFactory.getLog(SendSMSKEMCO.class);

	public String send(String mobileNo, String msg, Properties prop) throws MRPException {
		log.debug("<<<<<<<<<<<< Send SMS in KEMCO >>>>>>>>>>>");
		String baseURL = prop.getProperty("kemco.sms.baseUrl");
		
		//For test  http://187.1.10.58:8082/message/01036598198/ENJ060875571187.1.30.111,8198.
		//msg = "ENJ060875571187.1.30.111,8198";
		
	//	mobileNo = "01036598198";   // 테스트 용
		String testString = ".";
		
		URL url = null;
		HttpURLConnection urlConnection = null;
		String messageId = "fail";
		try {
			String sendURL = baseURL + mobileNo + "/" + URLEncoder.encode(msg, "UTF-8");
//			String sendURL = baseURL + mobileNo + "/" + Base64.encodeBase64(msg.getBytes());

			log.info(sendURL);

			//url = new URL(sendURL);
			
			// 회사에서 쓰는 SMS는 마지막 콤마뒤에는 모두 자르는 현상이 있어서 어쩔수 없이 콤마하나 추가해줌.
			url = new URL(sendURL + testString); 
			
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(10 * 1000);
			log.info("Connection Timeout[" + urlConnection.getConnectTimeout() + "]");
			
			urlConnection.setDoOutput(true);

			log.debug(urlConnection.getResponseCode());
			log.debug(urlConnection.getResponseMessage());

			//URL에서 보내온 메세지 읽는 부분 : return message sample : {"Status":0,"MessageId":"f2ed79b703874d84b048adb24fec7d19","Rate":0.6,"NetworkId":"450"}
			BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
			String inputLine = null;

			int seqno = 0;
			String state = "";
			StringTokenizer st = null;
			while ((inputLine = br.readLine()) != null) {
				log.debug(inputLine);
				//messaeId만 저장하기 위함
				st = new StringTokenizer(inputLine, "|");
				if (st.hasMoreTokens()) {
					seqno = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					state = st.nextToken();
					log.debug("STATE[" + state + "]");
				}
				if (st.hasMoreTokens()) {
					messageId = st.nextToken();
					log.debug("MESSAGE_ID[" + messageId + "]");
				}
			}
			br.close();

			//return messageId;

		} catch (Exception e) {
			log.warn(e, e);
		} finally {
			if (urlConnection != null && urlConnection.getURL() != null)
				urlConnection.disconnect();
		}
		return messageId;
	}
}
