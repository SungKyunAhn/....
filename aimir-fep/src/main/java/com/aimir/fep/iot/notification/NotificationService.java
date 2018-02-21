package com.aimir.fep.iot.notification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.dao.NotiDAO;
import com.aimir.fep.iot.dao.SmsDAO;

@Service
public class NotificationService {
	private static final Log logger = LogFactory.getLog(NotificationService.class);

	@Autowired
	NotiDAO notiDAO;
	
	@Autowired
	SmsDAO smsDAO;
	
	@Autowired
	SmartPhonePushMsg appPushMsg;
	
	public void notification(String custNo, String eventTypeCd){
		/*HashMap<String, Object> result = notiDAO.getNotiInfo(custNo, eventTypeCd);
		if(result == null) return;
		String custName = result.get("CUST_NAME") !=null ? result.get("CUST_NAME").toString() : "";
		String notiYn1 = result.get("NOTI_YN1") !=null ? result.get("NOTI_YN1").toString() : "";
		String notiMethod1 = result.get("NOTI_METHOD1") !=null ? result.get("NOTI_METHOD1").toString() : "";
		String mobileNo1 = result.get("MOBILE_NO1") !=null ? result.get("MOBILE_NO1").toString() : "";
		String name1 = result.get("NAME1") !=null ? result.get("NAME1").toString() : "";
		String notiYn2 = result.get("NOTI_YN2") !=null ? result.get("NOTI_YN2").toString() : "";
		String notiMethod2 = result.get("NOTI_METHOD2") !=null ? result.get("NOTI_METHOD2").toString() : "";
		String mobileNo2 = result.get("MOBILE_NO2") !=null ? result.get("MOBILE_NO2").toString() : "";
		String name2 = result.get("NAME2") !=null ? result.get("NAME2").toString() : "";
		String notiYn3 = result.get("NOTI_YN3") !=null ? result.get("NOTI_YN3").toString() : "";
		String notiMethod3 = result.get("NOTI_METHOD3") !=null ? result.get("NOTI_METHOD3").toString() : "";
		String mobileNo3 = result.get("MOBILE_NO3") !=null ? result.get("MOBILE_NO3").toString() : "";
		String name3 = result.get("NAME3") !=null ? result.get("NAME3").toString() : "";
		String notiYn4 = result.get("NOTI_YN4") !=null ? result.get("NOTI_YN4").toString() : "";
		String notiMethod4 = result.get("NOTI_METHOD4") !=null ? result.get("NOTI_METHOD4").toString() : "";
		String mobileNo4 = result.get("MOBILE_NO4") !=null ? result.get("MOBILE_NO4").toString() : "";
		String name4 = result.get("NAME4") !=null ? result.get("NAME4").toString() : "";
		String notiYn5 = result.get("NOTI_YN5") !=null ? result.get("NOTI_YN5").toString() : "";
		String notiMethod5 = result.get("NOTI_METHOD5") !=null ? result.get("NOTI_METHOD5").toString() : "";
		String mobileNo5 = result.get("MOBILE_NO5") !=null ? result.get("MOBILE_NO5").toString() : "";
		String name5 = result.get("NAME5") !=null ? result.get("NAME5").toString() : "";
		String notiYn6 = result.get("NOTI_YN6") !=null ? result.get("NOTI_YN6").toString() : "";
		String notiMethod6 = result.get("NOTI_METHOD6") !=null ? result.get("NOTI_METHOD6").toString() : "";
		String mobileNo6 = result.get("MOBILE_NO6") !=null ? result.get("MOBILE_NO6").toString() : "";
		String name6 = result.get("NAME6") !=null ? result.get("NAME6").toString() : "";
		String notiYn7 = result.get("NOTI_YN7") !=null ? result.get("NOTI_YN7").toString() : "";
		String notiMethod7 = result.get("NOTI_METHOD7") !=null ? result.get("NOTI_METHOD7").toString() : "";
		String mobileNo7 = result.get("MOBILE_NO7") !=null ? result.get("MOBILE_NO7").toString() : "";
		String name7 = result.get("NAME7") !=null ? result.get("NAME7").toString() : "";
		String eventCd = result.get("EVENT_TYPE_CD") !=null ? result.get("EVENT_TYPE_CD").toString() : "";
		String subject = result.get("SUBJECT") !=null ? result.get("SUBJECT").toString() : "";
		String message = result.get("MESSAGE") !=null ? result.get("MESSAGE").toString() : "";
		
		List<HashMap<String, Object>> appList = new ArrayList<HashMap<String, Object>>();
		List<HashMap<String, Object>> smsList = new ArrayList<HashMap<String, Object>>();
		List<String> cells = new ArrayList<String>();
		HashMap<String, Object> map = null;
		
		if(notiYn1.equals("Y") && notiMethod1.matches("A")){
			// 스마트폰 APP 호출
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name1);
			map.put("MOBILE_NO", mobileNo1);
			map.put("METHOD_GUBUN", "A");
			cells.add(mobileNo1);
			appList.add(map);
		}else if(notiYn1.equals("Y") && notiMethod1.matches("S")){
			// SMS 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name1);
			map.put("MOBILE_NO", mobileNo1);
			map.put("METHOD_GUBUN", "S");
			smsList.add(map);
		}else if(notiYn1.equals("Y") && notiMethod1.matches("L")){
			// SMS APP 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name1);
			map.put("MOBILE_NO", mobileNo1);
			map.put("METHOD_GUBUN", "L");
			appList.add(map);
			smsList.add(map);
		}

		if(notiYn2.equals("Y") && notiMethod2.matches("A")){
			// 스마트폰 APP 호출
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name2);
			map.put("MOBILE_NO", mobileNo2);
			map.put("METHOD_GUBUN", "A");
			cells.add(mobileNo2);
			appList.add(map);
		}else if(notiYn2.equals("Y") && notiMethod2.matches("S")){
			// SMS 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name2);
			map.put("MOBILE_NO", mobileNo2);
			map.put("METHOD_GUBUN", "S");
			smsList.add(map);
		}else if(notiYn2.equals("Y") && notiMethod2.matches("L")){
			// SMS APP 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name1);
			map.put("MOBILE_NO", mobileNo1);
			map.put("METHOD_GUBUN", "L");
			appList.add(map);
			smsList.add(map);
		}

		if(notiYn3.equals("Y") && notiMethod3.matches("A")){
			// 스마트폰 APP 호출
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name3);
			map.put("MOBILE_NO", mobileNo3);
			map.put("METHOD_GUBUN", "A");
			cells.add(mobileNo3);
			appList.add(map);
		}else if(notiYn3.equals("Y") && notiMethod3.matches("S")){
			// SMS 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name3);
			map.put("MOBILE_NO", mobileNo3);
			map.put("METHOD_GUBUN", "S");
			smsList.add(map);
		}else if(notiYn3.equals("Y") && notiMethod3.matches("L")){
			// SMS APP 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name1);
			map.put("MOBILE_NO", mobileNo1);
			map.put("METHOD_GUBUN", "L");
			appList.add(map);
			smsList.add(map);
		}

		if(notiYn4.equals("Y") && notiMethod4.matches("A")){
			// 스마트폰 APP 호출
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name4);
			map.put("MOBILE_NO", mobileNo4);
			map.put("METHOD_GUBUN", "A");
			cells.add(mobileNo4);
			appList.add(map);
		}else if(notiYn4.equals("Y") && notiMethod4.matches("S")){
			// SMS 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name4);
			map.put("MOBILE_NO", mobileNo4);
			map.put("METHOD_GUBUN", "S");
			smsList.add(map);
		}else if(notiYn4.equals("Y") && notiMethod4.matches("L")){
			// SMS APP 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name1);
			map.put("MOBILE_NO", mobileNo1);
			map.put("METHOD_GUBUN", "L");
			appList.add(map);
			smsList.add(map);
		}

		if(notiYn5.equals("Y") && notiMethod5.matches("A")){
			// 스마트폰 APP 호출
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name5);
			map.put("MOBILE_NO", mobileNo5);
			map.put("METHOD_GUBUN", "A");
			cells.add(mobileNo5);
			appList.add(map);
		}else if(notiYn5.equals("Y") && notiMethod5.matches("S")){
			// SMS 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name5);
			map.put("MOBILE_NO", mobileNo5);
			map.put("METHOD_GUBUN", "S");
			smsList.add(map);
		}else if(notiYn5.equals("Y") && notiMethod5.matches("L")){
			// SMS APP 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name1);
			map.put("MOBILE_NO", mobileNo1);
			map.put("METHOD_GUBUN", "L");
			appList.add(map);
			smsList.add(map);
		}

		if(notiYn6.equals("Y") && notiMethod6.matches("A")){
			// 스마트폰 APP 호출
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name6);
			map.put("MOBILE_NO", mobileNo6);
			map.put("METHOD_GUBUN", "A");
			cells.add(mobileNo6);
			appList.add(map);
		}else if(notiYn6.equals("Y") && notiMethod6.matches("S")){
			// SMS 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name6);
			map.put("MOBILE_NO", mobileNo6);
			map.put("METHOD_GUBUN", "S");
			smsList.add(map);
		}else if(notiYn6.equals("Y") && notiMethod6.matches("L")){
			// SMS APP 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name1);
			map.put("MOBILE_NO", mobileNo1);
			map.put("METHOD_GUBUN", "L");
			appList.add(map);
			smsList.add(map);
		}
		
		if(notiYn7.equals("Y") && notiMethod7.matches("A")){
			// 스마트폰 APP 호출
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name7);
			map.put("MOBILE_NO", mobileNo7);
			map.put("METHOD_GUBUN", "A");
			cells.add(mobileNo7);
			appList.add(map);
		}else if(notiYn7.equals("Y") && notiMethod7.matches("S")){
			// SMS 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name7);
			map.put("MOBILE_NO", mobileNo7);
			map.put("METHOD_GUBUN", "S");
			smsList.add(map);
		}else if(notiYn7.equals("Y") && notiMethod7.matches("L")){
			// SMS APP 전송
			map = new HashMap<String, Object>();
			map.put("CUST_NAME", custName);
			map.put("CUST_NO", custNo);
			map.put("EVENT_TYPE_CD", eventCd);
			map.put("SUBJECT", subject);
			map.put("MESSAGE", message);
			map.put("NAME", name1);
			map.put("MOBILE_NO", mobileNo1);
			map.put("METHOD_GUBUN", "L");
			appList.add(map);
			smsList.add(map);
		}	
		
		if(smsList.size() !=0 ){
			// SMS 전송
			smsDAO.sendSMS(smsList);	
		}
		
		if(appList.size() !=0 ){
			appPushMsg.pushMsgForGCM(appList, cells);
		}
		
		if((smsList.size() !=0)&& (appList.size() !=0) ){
			//이력저장
			List<HashMap<String, Object>> notiList = new ArrayList<HashMap<String, Object>>();
			notiList.addAll(smsList);
			notiList.addAll(appList);
			notiDAO.inserNotiLog(notiList);
		}by ask*/
	}
	
}
