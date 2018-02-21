package com.aimir.fep.google.gcm;

import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.FMPProperty;

/**
 * <pre>
 * com.aimir.google.gcm
 * GCMServer.java
 * </pre>
 * @author  : 은미애
 * @Date    : 2015. 9. 23.
 * @Version : 
 */
public class GCMServer {
	// REGISTRATION_ID 값은  DB에서 가져와야 함. 앱이 실행되면 OPEN API를 통해서 등록요청을 함.
	// APIKEY : 서버 인증키 로서 현재 

	    private static final String APIKEY = FMPProperty.getProperty("google.gcm.server.apikey"); //GCM에서 발급받은 ApiKey를 입력
		private static final Log logger = LogFactory.getLog(GCMServer.class);
	    public String regId = null;
	    public String title = null;
	    public String msg = null;
	    public String msagId = null;
	    public String contentId = null;
	    public GCMServer(String regId, String title, String msg, String contentId){
	    	this.regId = regId;
	    	this.title = title;
	    	this.msg = msg;
	    	this.contentId = contentId;
	    }

	    /*public String sendMsg(){
	    	logger.debug("-----------------start send message to CGM Server --------------");
	    	logger.debug("regId : " + regId +", title : " + title + ", msg : " + msg);
	    	Sender sender = new Sender(APIKEY);
	    	*//**
             * message Build 부분에서 addData로 추가한 값은 어플리케이션의
             * onMessage(context, intent)에서 Intent로 전달되며
             * intent.getExtras().getString("title")형태로 얻어와 사용 가능하다.
             *//*
	    	try{
	    		 Message message = new Message.Builder()
//	             .addData("title", URLEncoder.encode(title, "UTF-8"))
//	             .addData("msg", URLEncoder.encode(msg, "UTF-8"))
	             .addData("contentId",String.valueOf(contentId))
	             .build();

	             //발송할 메시지, 발송할 타깃(RegistrationId, Retry 횟수)
	             Result result = sender.send(message, regId, 3);
	             if (result.getMessageId() != null) {
	                 System.out.println("Send success : contentID : " + result.getMessageId());
	                 msagId = result.getMessageId();
	             } else {
	                 String error = result.getErrorCodeName();
	                 System.out.println("Send fail : " + error);
	             }
	    	}catch(Exception e){
	    		logger.error(e);
	    	}
	    	logger.debug("-----------------end send message to CGM Server --------------");
	    	return msagId;
	    }
	    
	    public static void main(String[] args) {
		    String REGISTRATION_ID = "APA91bGkdncmOFbD_0kCCdQwKivlToLTH13SBu56wk92oBqJou43jkYc0ykkaWf-Q-0oFzkbcajENqHk_cl9pixgedHnMHBAkGDwenxRb-LZQjxJD-SorLM-fVUw89OmFvK7aIT5MCZYyQJJLi32DB6ORpXbaABGFg";//registration Id 입력
	        try {
	            String sendTlt = "타이틀 제목";
	            String sendMsg = "내용 : 메시지가 보입니다";
	  
	            Sender sender = new Sender(APIKEY);
	             
	            *//**
	             * message Build 부분에서 addData로 추가한 값은 어플리케이션의
	             * onMessage(context, intent)에서 Intent로 전달되며
	             * intent.getExtras().getString("title")형태로 얻어와 사용 가능하다.
	             *//*
	            Message message = new Message.Builder()
	            .addData("title", URLEncoder.encode(sendTlt, "UTF-8"))
	            .addData("msg", URLEncoder.encode(sendMsg, "UTF-8"))
	            .build();
	  
	            //발송할 메시지, 발송할 타깃(RegistrationId, Retry 횟수)
	            Result result = sender.send(message, REGISTRATION_ID, 3);
	            if (result.getMessageId() != null) {
	                System.out.println("Send success");
	            } else {
	                String error = result.getErrorCodeName();
	                System.out.println("Send fail : " + error);
	            }
	  
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }*/
}
