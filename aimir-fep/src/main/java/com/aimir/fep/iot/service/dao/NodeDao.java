package com.aimir.fep.iot.service.dao;

import org.springframework.stereotype.Repository;

import com.aimir.fep.iot.domain.resources.Node;
import com.aimir.fep.iot.domain.resources.RemoteCSE;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;

@Repository
public class NodeDao extends GenericMongoDao {
	public NodeDao() {
		collectionName = RESOURCE_TYPE.NODE.getName();
		cls = Node.class;
	}
}
