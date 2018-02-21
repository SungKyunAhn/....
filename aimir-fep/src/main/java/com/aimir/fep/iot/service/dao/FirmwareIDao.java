package com.aimir.fep.iot.service.dao;

import org.springframework.stereotype.Repository;

import com.aimir.fep.iot.domain.resources.Firmware;
import com.aimir.fep.iot.utils.CommonCode.MGMT_DEFINITION;

@Repository
public class FirmwareIDao extends GenericMongoDao {
	public FirmwareIDao() {
		collectionName = MGMT_DEFINITION.FIRMWARE.getName();
		cls = Firmware.class;
	}

}
