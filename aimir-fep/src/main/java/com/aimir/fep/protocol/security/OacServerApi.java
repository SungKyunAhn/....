package com.aimir.fep.protocol.security;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.FMPProperty;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class OacServerApi {
    private static Log logger = LogFactory.getLog(OacServerApi.class);
	private String oacServerURL = FMPProperty.getProperty("protocol.security.oacserver.webservice.url",
			"http://127.0.0.1/oac/api/");
	private String trustStorePath = FMPProperty.getProperty("protocol.security.oacserver.truststore",
			"");
	private String trustStorePassword = FMPProperty.getProperty("protocol.security.oacserver.truststore.password",
			"");
    private static final String KEY_MANAGER_FACTORY_ALGORITHM;
    private static final String PROTOCOL = FMPProperty.getProperty("protocol.ssl.protocol", "TLSv1.2");
    
    static {
        String algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
        if (algorithm == null) {
            algorithm = "SunX509";
        }

        KEY_MANAGER_FACTORY_ALGORITHM = algorithm;
    }
    
	public  JsonObject oacServerApi(String function, String params ) throws Exception 
	{ 
	   	URL url = null;   	
	   	//=> UPDATE 
		//String baseURL = "https://" + oacServerIpAddress + "/api/" + function;
	   	String baseURL = oacServerURL + function;
	   	HttpURLConnection urlConnection = null;
		JsonObject  json = null;
        InputStream in = null;
		try {
			String sendURL = null;
			if ( params != null && params.length() < 0 ){
				sendURL = baseURL + "&" + params;
			}
			else {
				sendURL = baseURL;
			}
			logger.info(sendURL);
		 	url = new URL(sendURL);
		 	
			if ( sendURL.startsWith("https:")){
		    	TrustManagerFactory tmf = TrustManagerFactory.getInstance(KEY_MANAGER_FACTORY_ALGORITHM);
		    	SSLSocketFactory factory =null;
	        	SSLContext ctx = SSLContext.getInstance(PROTOCOL);
	        	KeyStore ks = KeyStore.getInstance("JKS");
	        	HttpsURLConnection  httpsConnection = null;
	        	
	        	if ( !trustStorePath.equals("") && !trustStorePassword.equals("")){	
	        		in = new FileInputStream(trustStorePath);
	        		ks.load(in,  trustStorePassword.toCharArray());
	            	tmf.init(ks);
	            	ctx.init(null, tmf.getTrustManagers(), null);
	            	factory = ctx.getSocketFactory();
					httpsConnection = (HttpsURLConnection)url.openConnection();
					httpsConnection.setHostnameVerifier(new HostnameVerifier() {

	                    @Override
	                    public boolean verify(String arg0, SSLSession arg1) {
	                        // TODO Auto-generated method stub
	                        return true;
	                    }
	                    
	                });
					httpsConnection.setSSLSocketFactory(factory);
	        	}
	        	else {
	        	       // Create a trust manager that does not validate certificate chains
	                TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                            return null;
	                        }
	                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
	                        }
	                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
	                        }
	                    }
	                };
	        		ctx.init(null, trustAllCerts, new java.security.SecureRandom());
	        		HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());
	                // Create all-trusting host name verifier
	                HostnameVerifier allHostsValid = new HostnameVerifier() {
	                    public boolean verify(String hostname, SSLSession session) {
	                        return true;
	                    }
	                };
	                // Install the all-trusting host verifier
	                HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
					httpsConnection = (HttpsURLConnection)url.openConnection();
	        	}
				urlConnection = (HttpsURLConnection)httpsConnection;
			}
			else {
				urlConnection = (HttpURLConnection)url.openConnection();
			}
			logger.debug("Connection Timeout[" + urlConnection.getConnectTimeout() + "]");
			urlConnection.setConnectTimeout(10 * 1000);
			urlConnection.setRequestMethod("GET");
			urlConnection.connect();
			
			logger.debug(urlConnection.getResponseCode());
			logger.debug(urlConnection.getResponseMessage());
			
			if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
              	 InputStreamReader isr = new InputStreamReader(urlConnection.getInputStream(),
                         StandardCharsets.UTF_8);
                 try {
                	 BufferedReader reader = new BufferedReader(isr);
                	 
                	 StringBuilder sb = new StringBuilder();
                	 int cp;
                	 while ((cp = reader.read()) != -1) {
                	      sb.append((char) cp);
                	 }
                	 String  jsonText =  sb.toString();
                	 
                	 JsonParser jp = new JsonParser();
                	 JsonElement element = jp.parse(jsonText);
                	  json = element.getAsJsonObject();
                	 logger.debug("Json=" + json.toString());
                 } finally {
                	 isr.close();
                 }
			}
		}
		catch (Exception e) {
			logger.error(e,e);
		}
		finally{
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {}
            }
            if ( urlConnection != null){
            	urlConnection.disconnect();
            }
		}
		return json;
    }	
	
    public HashMap <String, String>  getMeterSharedKey(String serialNo, String meterId){
    	HashMap <String, String> resultMap = null; 
    	try {
    		JsonObject json = oacServerApi("get_sharedkey_and_pin_argument/" + serialNo + "/" + meterId, null);
    		if ( json == null || json.get("result_code") == null ){
    			logger.error("get_sharedkey_and_pin_argument fail.");
    		}
    		else {
	    		int result  = json.get("result_code").getAsInt();
	    		if ( result  != 0  ){
	    			logger.error("get_sharedkey_and_pin_argument fail. result = " + result);
	    		}
	    		else {
		    		// success
	    			
	    			/*
	    			 * Shared Key
	    			 */
		    		JsonObject sharedKeyObj = json.getAsJsonObject("shared_key");
		    		resultMap = new HashMap <String, String>();
		    		if ( sharedKeyObj != null ){ 	
		    			String masterKey = sharedKeyObj.get("master_key").getAsString();
		    			String unicastKey =  sharedKeyObj.get("encryption_unicast_key").getAsString();
		    			String multicastKey =  sharedKeyObj.get("hls_secret_unique_key").getAsString();
		    			String authenticationKey =  sharedKeyObj.get("authentication_key").getAsString();
		    			if ( masterKey != null ) resultMap.put("MasterKey", masterKey);
		    			if ( unicastKey != null) resultMap.put("UnicastKey", unicastKey);
		    			if ( multicastKey != null ) resultMap.put("MulticastKey", multicastKey);
		    			if ( authenticationKey != null ) resultMap.put("AuthenticationKey", authenticationKey);
			    		logger.debug("MasterKey:"+ masterKey + ", UnicastKey:" + unicastKey + ", MulticastKey:" + multicastKey + ", AuthenticationKey:" + authenticationKey);
		    		}
		    		
		    		/*
		    		 * PIN Code
		    		 */
		    		String pinCode  = json.get("pin_arg").getAsString();
		    		resultMap.put("pin_arg", pinCode);		    		
	    		}
    		}
    	}
    	catch ( Exception e){
    		logger.error(e,e);
    	}
	    return resultMap;
    }
    
    public HashMap <String, String>  getPanaMeterSharedKey(String serialNo, String meterId){
        HashMap <String, String> resultMap = null; 
        try {
            JsonObject json = oacServerApi("get_pana_sharedkey/" + serialNo + "/" + meterId, null);
            if ( json == null || json.get("result_code") == null ){
                logger.error("get_pana_sharedkey fail.");
            }
            else {
                int result  = json.get("result_code").getAsInt();
                if ( result  != 0  ){
                    logger.error("get_pana_sharedkey fail. result = " + result);
                }
                else {
                    // success
                    
                    /*
                     * Shared Key
                     */
                    JsonObject sharedKeyObj = json.getAsJsonObject("shared_key");
                    resultMap = new HashMap <String, String>();
                    if ( sharedKeyObj != null ){    
                        String masterKey = sharedKeyObj.get("master_key").getAsString();
                        String unicastKey =  sharedKeyObj.get("encryption_unicast_key").getAsString();
                        String multicastKey =  sharedKeyObj.get("hls_secret_unique_key").getAsString();
                        String authenticationKey =  sharedKeyObj.get("authentication_key").getAsString();
                        if ( masterKey != null ) resultMap.put("MasterKey", masterKey);
                        if ( unicastKey != null) resultMap.put("UnicastKey", unicastKey);
                        if ( multicastKey != null ) resultMap.put("MulticastKey", multicastKey);
                        if ( authenticationKey != null ) resultMap.put("AuthenticationKey", authenticationKey);
                        logger.debug("MasterKey:"+ masterKey + ", UnicastKey:" + unicastKey + ", MulticastKey:" + multicastKey + ", AuthenticationKey:" + authenticationKey);
                    }
                }
            }
        }
        catch ( Exception e){
            logger.error(e,e);
        }
        return resultMap;
    }
    
    private class NonAuthentication implements X509TrustManager {
    	@Override
    	public void checkClientTrusted(X509Certificate[] chain, String authType)
    			throws CertificateException {
    	}

    	@Override
    	public void checkServerTrusted(X509Certificate[] chain, String authType)
    			throws CertificateException {
    	}

    	@Override
    	public X509Certificate[] getAcceptedIssuers() {
    		return null;
    	}
    }
}
