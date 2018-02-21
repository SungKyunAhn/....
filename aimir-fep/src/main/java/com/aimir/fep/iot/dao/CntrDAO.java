package com.aimir.fep.iot.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

@Repository("cntrDAO")
public class CntrDAO {/*extends EgovAbstractDAO{

    public String insertCntr(CNTRVO cntrVO){
        return (String)insert("cntrDAO.insertCntr", cntrVO);
    }
    
    public List<CNTRVO> findCntrListByDeviceId(String deviceId){
    	List<CNTRVO> list = (List<CNTRVO>) list("cntrDAO.selectCntrLists", deviceId);
    	return list;
    }

    public void deleteCntr(CNTRVO cntrVO){
    	delete("cntrDAO.deleteCntr", cntrVO);
    }

	public void increaseReceiveSensingCnt(int rCount) {
		// TODO Auto-generated method stub
		update("cntrDAO.increaseReceiveSensingCnt", rCount);
	}

	public void increaseSendSensingCnt(int sCount) {
		// TODO Auto-generated method stub
		update("cntrDAO.increaseSendSensingCnt", sCount);
	}*/
}

