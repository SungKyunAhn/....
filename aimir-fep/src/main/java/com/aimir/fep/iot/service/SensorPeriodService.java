package com.aimir.fep.iot.service;

import org.springframework.stereotype.Service;

import com.aimir.fep.iot.dao.SensorPeriodDAO;
import com.aimir.fep.iot.model.vo.DeviceSearchVO;
import com.aimir.fep.iot.model.vo.LoginVO;
import com.aimir.fep.iot.model.vo.SensorPeriodVO;
import com.aimir.fep.iot.mqtt.MqttClientUtil;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SensorPeriodService {
	
	@Autowired
	SensorPeriodDAO dao;

	public int insertSensorPeriodLog(DeviceSearchVO sensorVo, String wid) {
		// TODO Auto-generated method stub
		
		//if(!(sensorVo.getChkList()!=null)){
		if(sensorVo.getChkList()!=null){
			SensorPeriodVO spVo = null;
			//String [] targets = sensorVo.getChkList();
			/*LoginVO user = EgovUserDetailsHelper.isAuthenticated() ? (LoginVO) EgovUserDetailsHelper.getAuthenticatedUser() : null; by ask*/
			spVo = new SensorPeriodVO();
			spVo.setTARGET_ID(wid);
			spVo.setREQUEST_GUBUN("N");
			spVo.setRESULT_CD("0000");
			spVo.setGROUP_CD("0");
			/*return dao.insertSensorPeriodLog(spVo); by ask*/;
		}else{
			SensorPeriodVO spVo;
			spVo = new SensorPeriodVO();
			spVo.setTARGET_ID("all");
			spVo.setREQUEST_GUBUN("N");
			spVo.setRESULT_CD("0000");
			spVo.setGROUP_CD("0");
			/*return dao.insertSensorPeriodLog(spVo); by ask*/
		}
		return 0; //by ask
	}

	
	public void sendWearableInfoCmd(String request,String state) {
		// TODO Auto-generated method stub
		DeviceSearchVO sensorVo = new DeviceSearchVO();
		
		MqttClientUtil mqttClient; 
		String mgmtCmdResourceName="periodUpdate";	

		List<String> tmp = new ArrayList<String>();

		tmp.add(request);
		sensorVo.setChkList(tmp);   

		String oper_state= state;


		try {
			mqttClient = new MqttClientUtil();

			for(int j=0 ; j<sensorVo.getChkList().size();j++ ) {
				//추가 차병준
				//sensorVo.getChkList().get(j) null 체크 추가
				if(sensorVo.getChkList().get(j) != null){
					int seq = insertSensorPeriodLog(sensorVo, sensorVo.getChkList().get(j));
					String topic="all";

					String controlValue="<?xml version='1.0' encoding='UTF-8' standalone='yes'?>"
							+ "<exin>"
							+ "<resourceID>all</resourceID>"
							+ "<parentID>all</parentID>"
							+ "<execTarget>all</execTarget>"
							+ "<execReqArgs>"
							+seq+",0x0A,"+sensorVo.getChkList().get(j)+","+oper_state
							+"</execReqArgs>"
							+ "</exin>";
					/*mqttClient.publishFullPayload(topic, mgmtCmdResourceName+","+controlValue); by ask*/
				}			
			}
			

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
