package com.aimir.fep.iot.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.resources.DeviceInfo;
import com.aimir.fep.iot.domain.resources.Node;
import com.aimir.fep.iot.service.dao.DeviceInfoDao;
import com.aimir.fep.iot.utils.CommonCode.MGMT_DEFINITION;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.fep.iot.utils.SequenceUtils.SEQ_PREFIX;
import com.aimir.fep.util.DataUtil;

@Service
public class DeviceInfoService {
	private static Log logger = LogFactory.getLog(DeviceInfoService.class);
	
	@Autowired
	DeviceInfoDao deviceInfoDao;
	
	public DeviceInfo findOneDeviceInfoByResourceName(String seq, String resourceName) throws Exception {
		DeviceInfo deviceInfo = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("resourceName").is(resourceName));

		try {
			deviceInfo = (DeviceInfo) deviceInfoDao.findOne(query);
		} catch (Exception e) {
			logger.error("*** ["+seq+"] deviceInfo Resource findOne exception:"+e.getMessage()+" ***");
			throw e;
		}

		return deviceInfo;
	}
	
	public DeviceInfo findOneDeviceInfoByResourceName(String seq, String parentID, String resourceName) throws Exception {
		DeviceInfo deviceInfo = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("parentID").is(parentID));
		query.addCriteria(Criteria.where("resourceName").is(resourceName));

		try {
			deviceInfo = (DeviceInfo) deviceInfoDao.findOne(query);
		} catch (Exception e) {
			logger.error("*** ["+seq+"] deviceInfo Resource findOne exception:"+e.getMessage()+" ***");
			throw e;
		}

		return deviceInfo;
	}
	
	public DeviceInfo insert(String seq, Node parent, DeviceInfo deviceInfo) throws Exception {
		try {
			String resourceId = CommonUtil.seqIDToResourceID(SEQ_PREFIX.DEVICE_INFO.getValue(), seq);
			String currentTime = CommonUtil.getNowTimestamp();
			
			deviceInfo.setResourceType(RESOURCE_TYPE.MGMT_OBJ.getValue());
			deviceInfo.setResourceID(resourceId);
			deviceInfo.setResourceName(!CommonUtil.isEmpty(deviceInfo.getResourceName()) ? deviceInfo.getResourceName() : resourceId);
			deviceInfo.setParentID(parent.getResourceID());
			deviceInfo.setMgmtDefinition(MGMT_DEFINITION.DEVICE_INFO.getValue());
			deviceInfo.setExpirationTime(deviceInfo.getExpirationTime());
			deviceInfo.setExpirationDate(CommonUtil.timestampToDate(deviceInfo.getExpirationTime()));
			
			deviceInfo.setCreationTime(currentTime);
			deviceInfo.setLastModifiedTime(currentTime);
			//deviceInfoItem.getLabels().addAll(deviceInfoProfile.getLabels());
			//deviceInfoItem.getObjectIDs().addAll(deviceInfoProfile.getObjectIDs());
			//deviceInfoItem.getObjectPaths().addAll(deviceInfoProfile.getObjectPaths());
			//deviceInfoItem.setDescription(deviceInfoProfile.getDescription());
			//deviceInfoItem.setDeviceLabel(deviceInfoProfile.getDeviceLabel());
			//deviceInfoItem.setManufacturer(deviceInfoProfile.getManufacturer());
			//deviceInfoItem.setModel(deviceInfoProfile.getModel());
			//deviceInfoItem.setDeviceType(deviceInfoProfile.getDeviceType());
			//deviceInfoItem.setFwVersion(deviceInfoProfile.getFwVersion());
			//deviceInfoItem.setSwVersion(deviceInfoProfile.getSwVersion());
			//deviceInfoItem.setHwVersion(deviceInfoProfile.getHwVersion());
			//deviceInfoItem.setResourceRef(new ResourceRef(mCommonService.getContentLocation(url, deviceInfoItem), deviceInfoItem.getResourceName(), RESOURCE_TYPE.MGMT_OBJ.getValue(), deviceInfoItem.getResourceID(), MGMT_DEFINITION.DEVICE_INFO.getValue()));
			
			deviceInfoDao.insert(deviceInfo);
			
			return deviceInfo;
		}catch(Exception e) {
			logger.error("*** ["+seq+"] firmware Resource insert exception:"+e.getMessage()+" ***");
			throw e;
		}
	}
}
