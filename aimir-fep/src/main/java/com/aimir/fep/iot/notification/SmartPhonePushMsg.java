package com.aimir.fep.iot.notification;

import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aimir.fep.google.gcm.GCMServer;
import com.aimir.fep.iot.dao.NotiDAO;

@Service
public class SmartPhonePushMsg {
	
	GCMServer server = null;
	
	@Autowired
	NotiDAO notiDao;
	
	public void pushMsgForGCM(List<HashMap<String, Object>> list, List<String> cells){
		
		/*HashMap<String, Object> _map = notiDao.getRegInfo(cells); by ask*/
		HashMap<String, Object> param = new HashMap<String, Object>();

		for(HashMap<String, Object> map : list){
			/*Integer seq = notiDao.getGcmContentSeq(); by ask*/
			//String regId, 
			//String title,
			//String msg
			/*String regId = _map.get(map.get("MOBILE_NO")) != null ? _map.get(map.get("MOBILE_NO")).toString() : "";

			if(!regId.isEmpty()){
				// 등록
				param.put("CONTENT_ID", seq.toString());
				param.put("CUST_NO", map.get("CUST_NO"));
				param.put("CUST_NAME",  map.get("CUST_NAME"));
				param.put("NAME",  map.get("NAME"));
				param.put("EVENT_TYPE_CD",  map.get("EVENT_TYPE_CD"));
				param.put("MOBILE_NO",  map.get("MOBILE_NO"));
				param.put("SUBJECT",  map.get("SUBJECT"));
				param.put("MESSAGE",  map.get("MESSAGE"));
				param.put("STATUS",  map.get("STATUS"));
				notiDao.insertGcmMsg(param);
				server = new GCMServer(regId, map.get("SUBJECT").toString(), map.get("CUST_NAME")+ "님께서 " + map.get("MESSAGE").toString(), seq.toString());
				server.sendMsg();
			} by ask*/
			
		}
		
		
	}
	
}
