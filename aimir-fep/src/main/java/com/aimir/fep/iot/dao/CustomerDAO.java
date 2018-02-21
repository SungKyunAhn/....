package com.aimir.fep.iot.dao;

import java.util.HashMap;

import org.springframework.stereotype.Repository;

@Repository("customerDAO")
public class CustomerDAO {/*extends EgovAbstractDAO{

    public CustomerVO selectCustNo(String deviceId){
        return (CustomerVO)select("custDAO.selectCustNo", deviceId);
    }

	public HashMap<String, String> selectScope(String devId) {
		// TODO Auto-generated method stub
		return (HashMap<String, String>)select("custDAO.selectScope", devId);
	}

	public void updateHRMFlag(HashMap<String, String> tmp) {
		// TODO Auto-generated method stub
		update("custDAO.updateHRMFlag", tmp);
	}

	public void updateLocFlag(HashMap<String, String> tmp) {
		// TODO Auto-generated method stub
		update("custDAO.updateLocFlag", tmp);
	}
	
	//2017 03 22 차병준
	public void insertSmsLog(HashMap<String, String> tmp) {
		// TODO Auto-generated method stub
	//	update("custDAO.updateLocFlag", tmp);
		insert("custDAO.insertSmsLog", tmp);
	}*/
}

