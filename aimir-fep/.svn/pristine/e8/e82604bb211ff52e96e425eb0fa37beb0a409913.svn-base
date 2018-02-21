package com.aimir.fep.iot.controller.adapter.msgBridge;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyStore;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.common.RSCException;
import com.aimir.fep.iot.domain.resources.ContentInstance;
import com.aimir.fep.util.FMPProperty;

@Service
public class ContentInstanceInteractionHttpRequest {
	/** The logger. */
	private Logger logger = Logger.getLogger(this.getClass());

	private static int requestIndex = 0;
	private String INT_SYS_URL = FMPProperty.getProperty("mp.oneM2M.integration.url");
	private String INCSEAddress = INT_SYS_URL + "/iSecIP/iSec";

	private static final boolean debugPrint = true;

	/**
	 * CSERegistrationHttpsMessage Method
	 * @param CSEProfile
	 * @return responseString
	 * @throws Exception
	 * Mobius로 CSE 등록을 하기 위한 Method로서 HTTPS POST를 사용함
	 */
	public String ContentInstanceRegistrationHttpsMessage(ContentInstance contentInstance) throws RSCException {
		logger.debug("====================================================================");
		logger.debug("ContentInstanceRegistrationHttpsMessage start!!!");
		logger.debug("====================================================================");
		/*HttpPost httpPost = null;
		String	sslYn="Y";
		String	selfSignYn="Y";
		HttpResponse response = null;
		String resultString = "";
		CloseableHttpClient httpclient=null;
		HttpClientBuilder cb = null;
		String requestBody = 		
				"<m2m:cin\n" +
						"xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"\n" +
						"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n" +
						"<cnf>" + contentInstance.getContentInfo() + "</cnf>\n" + 
						"<cr>" + contentInstance.getCreator() + "</cr>\n" + 
						"<ct>" + contentInstance.getCreationTime() + "</ct>\n" + 
						//"<con>" + Base64.encode(uploadThingData.content.getBytes()) + "</con>\n" +
						"<con>" + contentInstance.getContent()  + "</con>\n" +
				"</m2m:cin>";
		try{
			
			StringEntity entity = new StringEntity(new String(requestBody.getBytes()));
			if( sslYn.equals("Y")) {
				if(selfSignYn.equals("Y") ) {
			         cb = HttpClientBuilder.create();
			        SSLContextBuilder sslcb = new SSLContextBuilder();
			        sslcb.loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()), new TrustSelfSignedStrategy());
			        cb.setSslcontext(sslcb.build());
			        httpclient = cb.build();
			        httpPost = new HttpPost(INCSEAddress + "/remoteCSE-" + contentInstance.getCseid() + "/container-" + contentInstance.getResourceName());
				} else {
					 cb = HttpClientBuilder.create();
						httpclient = cb.build();
						httpPost = new HttpPost(INCSEAddress + "/remoteCSE-" + contentInstance.getCseid() + "/container-" + contentInstance.getResourceName());
				}
			} else {
				 cb = HttpClientBuilder.create();
				httpclient = cb.build();
				httpPost = new HttpPost(INCSEAddress + "/remoteCSE-" + contentInstance.getCseid() + "/container-" + contentInstance.getResourceName());
			}
	
			httpPost.addHeader("Accept", "application/xml");
			httpPost.addHeader("Content-Type", "application/xml;rty=4");
			httpPost.addHeader("locale", "ko");
			httpPost.addHeader("X-M2M-Origin", contentInstance.getCseid());
			httpPost.addHeader("X-M2M-RI", Integer.toString(requestIndex));
	        httpPost.addHeader("X-M2M-NM", contentInstance.getResourceName());
	        httpPost.addHeader("dKey",  contentInstance.getdKey());
	        httpPost.setEntity(entity);
	        requestIndex++;
	       
	        response = httpclient.execute(httpPost);
	        
	        logger.debug("Status Line from Portal : " + response.getStatusLine());
	        
	        HttpEntity responseEntity = response.getEntity();

			BufferedReader br = new BufferedReader
			(new InputStreamReader(responseEntity.getContent(), "UTF-8"));
			String receivedData = null;
			while((receivedData = br.readLine()) != null) {
				resultString = resultString + receivedData;
			}

			logger.debug(resultString);
			IPCmrServer.trigger=true;
		}catch(Exception e){
			IPCmrServer.trigger=false;
			RecoveryFileIO.makeFileForRecovery(contentInstance);
			e.printStackTrace();
			throw new RSCException(RSC.INTERNAL_SERVER_ERROR, "RemoteCSE interaction Error");
		}finally{
			httpPost.abort();
			try{
				httpclient.close();
			}catch(IOException e){
				throw new RSCException(RSC.INTERNAL_SERVER_ERROR, "RemoteCSE interaction Error");
			}
		}
		Header[] headers = response.getAllHeaders();
		for ( Header header : headers )
		{
			logger.debug("(D) Key : "+header.getName()+" , Value : "+header.getValue());
		}
		logger.debug("====================================================================");
		logger.debug("ContentInstanceRegistrationHttpsMessage end!!!");
		logger.debug("====================================================================");

		return resultString; by ask*/
		return null; // by ask
	}
}
