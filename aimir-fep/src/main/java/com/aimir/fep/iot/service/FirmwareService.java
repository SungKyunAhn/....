package com.aimir.fep.iot.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.resources.Firmware;
import com.aimir.fep.iot.domain.resources.Node;
import com.aimir.fep.iot.service.dao.FirmwareIDao;
import com.aimir.fep.iot.utils.CommonCode.MGMT_DEFINITION;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.fep.iot.utils.SequenceUtils.SEQ_PREFIX;
import com.aimir.fep.util.DataUtil;

@Service
public class FirmwareService {
	private static Log logger = LogFactory.getLog(FirmwareService.class);
	
	@Autowired
	FirmwareIDao firmwareDao;
	
	public Firmware findOneFirmware(String seq, String resourceName) throws Exception {
		Firmware frirmware = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("resourceName").is(resourceName));

		try {
			frirmware = (Firmware) firmwareDao.findOne(query);
		} catch (Exception e) {
			logger.error("*** ["+seq+"] firmware Resource findOne exception:"+e.getMessage()+" ***");
			throw e;
		}

		return frirmware;
	}
	
	public Firmware findOneFirmwareByResourceName(String seq, String parentID, String resourceName) throws Exception {
		Firmware frirmware = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("parentID").is(parentID));
		query.addCriteria(Criteria.where("resourceName").is(resourceName));

		try {
			frirmware = (Firmware) firmwareDao.findOne(query);
		} catch (Exception e) {
			logger.error("*** ["+seq+"] firmware Resource findOne exception:"+e.getMessage()+" ***");
			throw e;
		}

		return frirmware;
	}
	
	public Firmware insert(String seq, Node parent, Firmware firmware) throws Exception {
		try {
			String resourceId = CommonUtil.seqIDToResourceID(SEQ_PREFIX.FIRMWARE.getValue(), seq);
			String currentTime = CommonUtil.getNowTimestamp();
			
			firmware.setResourceType(RESOURCE_TYPE.MGMT_OBJ.getValue());
			firmware.setResourceID(resourceId);
			firmware.setResourceName(!CommonUtil.isEmpty(firmware.getResourceName()) ? firmware.getResourceName() : resourceId);
			firmware.setName(!CommonUtil.isEmpty(firmware.getResourceName()) ? firmware.getResourceName() : resourceId);
			firmware.setParentID(parent.getParentID());
			firmware.setMgmtDefinition(MGMT_DEFINITION.FIRMWARE.getValue());
			firmware.setExpirationTime(firmware.getExpirationTime());
			firmware.setExpirationDate(CommonUtil.timestampToDate(firmware.getExpirationTime()));

			firmware.setCreationTime(currentTime);
			firmware.setLastModifiedTime(currentTime);
			//firmwareItem.getLabels().addAll(firmwareProfile.getLabels());
			//firmwareItem.getObjectIDs().addAll(firmwareProfile.getObjectIDs());
			//firmwareItem.getObjectPaths().addAll(firmwareProfile.getObjectPaths());
			//firmwareItem.setDescription(firmwareProfile.getDescription());
			//firmwareItem.setVersion(firmwareProfile.getVersion());
			//firmwareItem.setName(firmwareProfile.getName());
			//firmwareItem.setURL(firmwareProfile.getURL());
			//firmwareItem.setUpdate(firmwareProfile.isUpdate());
			//firmwareItem.setUpdateStatus(firmwareProfile.getUpdateStatus());
			//firmwareItem.setResourceRef(new ResourceRef(mCommonService.getContentLocation(url, firmwareItem), firmwareItem.getResourceName(), RESOURCE_TYPE.MGMT_OBJ.getValue(), firmwareItem.getResourceID(), MGMT_DEFINITION.FIRMWARE.getValue()));
			
			firmwareDao.insert(firmware);
			
			return firmware;
		}catch(Exception e) {
			logger.error("*** ["+seq+"] firmware Resource insert exception:"+e.getMessage()+" ***");
			throw e;
		}
		
	}
	
}
