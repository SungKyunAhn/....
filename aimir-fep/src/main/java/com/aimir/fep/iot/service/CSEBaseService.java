package com.aimir.fep.iot.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.common.RSCException;
import com.aimir.fep.iot.domain.resources.CSEBase;
import com.aimir.fep.iot.service.dao.CSEBaseDao;
import com.aimir.fep.iot.utils.CommonCode;
import com.aimir.fep.util.DataUtil;

@Service
public class CSEBaseService {
	private static final Log logger = LogFactory.getLog(CSEBaseService.class);

	@Autowired
	CSEBaseDao cseBaseDao;
	
	public CSEBase findOneCSEBaseByCSEID(String cseid) throws RSCException {
		CSEBase cseBase = null;
		
		Query query = new Query();
		query.addCriteria(Criteria.where("cseid").is(cseid));
		
		try {
			cseBase = (CSEBase)cseBaseDao.findOne(query);
		}catch(Exception e) {
			e.printStackTrace();
			throw new RSCException(CommonCode.RSC.INTERNAL_SERVER_ERROR, "Failed to retrieve the CSEBase.");
		}
		
		return cseBase;
	}
	
}
