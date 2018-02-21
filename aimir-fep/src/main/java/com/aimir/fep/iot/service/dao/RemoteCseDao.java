package com.aimir.fep.iot.service.dao;

import org.springframework.stereotype.Repository;

import com.aimir.fep.iot.domain.resources.RemoteCSE;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;

@Repository
public class RemoteCseDao extends GenericMongoDao {
	public RemoteCseDao() {
		collectionName = RESOURCE_TYPE.REMOTE_CSE.getName();
		cls = RemoteCSE.class;
	}
}
