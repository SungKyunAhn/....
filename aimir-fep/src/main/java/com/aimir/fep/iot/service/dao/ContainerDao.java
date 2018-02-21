package com.aimir.fep.iot.service.dao;

import org.springframework.stereotype.Repository;

import com.aimir.fep.iot.domain.resources.Container;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;

@Repository
public class ContainerDao extends GenericMongoDao {
	public ContainerDao() {
		collectionName = RESOURCE_TYPE.CONTAINER.getName();
		cls = Container.class;
	}
	
}
