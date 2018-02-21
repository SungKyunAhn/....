package com.aimir.fep.iot.service.dao;

import org.springframework.stereotype.Repository;

import com.aimir.fep.iot.domain.resources.DeviceInfo;
import com.aimir.fep.iot.utils.CommonCode.MGMT_DEFINITION;

@Repository
public class DeviceInfoDao extends GenericMongoDao {
	public DeviceInfoDao() {
		collectionName = MGMT_DEFINITION.DEVICE_INFO.getName();
		cls = DeviceInfo.class;
	}
}
