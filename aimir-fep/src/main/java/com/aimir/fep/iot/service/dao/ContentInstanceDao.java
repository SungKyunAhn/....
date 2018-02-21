package com.aimir.fep.iot.service.dao;

import org.springframework.stereotype.Repository;

import com.aimir.fep.iot.domain.resources.ContentInstance;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;

@Repository
public class ContentInstanceDao extends GenericMongoDao {
	public ContentInstanceDao() {
		collectionName = RESOURCE_TYPE.CONTENT_INSTANCE.getName();
		cls = ContentInstance.class;
	}
}
