package com.aimir.fep.iot.dao;

import org.springframework.stereotype.Repository;

import com.aimir.dao.GenericDao;
import com.aimir.fep.iot.model.vo.LocationVO;
import com.aimir.model.iot.DcuTbl;

@Repository("locDAO")
public class LocDAO {/*extends GenericDao<DcuTbl, String> {

	public String insertLoc(LocationVO locVO);
	
	public String getAddr(LocationVO locVO);*/

}

/*public class LocDAO {extends EgovAbstractDAO{

public String insertLoc(LocationVO locVO){
    return (String)insert("locDAO.insertLOC", locVO);
}

public String getAddr(LocationVO locVO){
	return (String)select ("locDAO.getAddr", locVO);
}*/