package com.aimir.fep.iot.service.dao;

import org.springframework.stereotype.Repository;

import com.aimir.fep.iot.domain.resources.MgmtCmd;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;

@Repository
public class MgmtCmdDao extends GenericMongoDao {
	public MgmtCmdDao() {
		collectionName = RESOURCE_TYPE.MGMT_CMD.getName();
		cls = MgmtCmd.class;
	}
}
