package com.aimir.fep.iot.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

@Repository
public class NotiDAO {/*extends EgovAbstractDAO{
	private static final Log logger = LogFactory.getLog(NotiDAO.class);
	
	public HashMap<String, Object> getNotiInfo(String custNo, String evenTypeCd){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("CUST_NO", custNo);
		map.put("EVENT_TYPE_CD", evenTypeCd);

		return (HashMap<String, Object>)select("notiDAO.selectNotiInfo", map);
	}

	public HashMap<String, Object> getRegInfo(List<String> list){
		HashMap cellNos = new HashMap();
		cellNos.put("cellNos", list);
		List<HashMap<String, Object>> result = (List<HashMap<String, Object>>)list("notiDAO.selectRegInfo", cellNos);
		HashMap<String, Object> _map = new HashMap<String, Object>();
		for(HashMap<String, Object> map : result){
			String cellNo = map.get("CELLNO").toString();
			String regID = map.get("REG_ID").toString();
			_map.put(cellNo, regID);
		}
		return _map;
	}

	public Integer getGcmContentSeq(){
		return (Integer)select("notiDAO.getGcmContentSeq");
	}
	
	public String insertGcmMsg(HashMap<String, Object> param){
		return (String)insert("notiDAO.insertGcmMsg", param);
	}

	public void inserNotiLog(List<HashMap<String, Object>> list) {
		// TODO Auto-generated method stub
		for(int i = 0 ; i < list.size() ; i ++){
			insert("notiDAO.saveNotiLog", list.get(i));
		}
	}*/
	
}
