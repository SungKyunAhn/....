package com.aimir.fep.iot.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.common.RSCException;
import com.aimir.fep.iot.domain.resources.RemoteCSE;
import com.aimir.fep.iot.service.dao.RemoteCseDao;
import com.aimir.fep.iot.utils.CommonCode;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.fep.iot.utils.MessageProperties;
import com.aimir.fep.iot.utils.SequenceUtils.SEQ_PREFIX;
import com.aimir.fep.util.DataUtil;

@Service
public class RemoteCseService {

	@Autowired
	RemoteCseDao remoteCseDao;
	
	public RemoteCSE findOneRemoteCSEByResoureName(String resourceName) throws RSCException {
		RemoteCSE remoteCse = null;
		
		Query query = new Query();
		query.addCriteria(Criteria.where("resourceName").is(resourceName));
		
		try {
			remoteCse = (RemoteCSE) remoteCseDao.findOne(query);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RSCException(CommonCode.RSC.INTERNAL_SERVER_ERROR, MessageProperties.getProperty("aimir.iot.dev.find.fail"));
		}

		return remoteCse;
	}

	
	public RemoteCSE findOneRemoteCSEByResourceName(String parentID, String resourceName) throws RSCException {
		RemoteCSE remoteCse = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("parentID").is(parentID));
		query.addCriteria(Criteria.where("resourceName").is(resourceName));

		try {
			remoteCse = (RemoteCSE) remoteCseDao.findOne(query);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RSCException(CommonCode.RSC.INTERNAL_SERVER_ERROR, MessageProperties.getProperty("aimir.iot.dev.find.fail"));
		}

		return remoteCse;
	}
	
	public RemoteCSE insert(String seq, RemoteCSE remoteCSE) throws Exception {
		try {
			String resourceId = CommonUtil.seqIDToResourceID(SEQ_PREFIX.REMOTE_CSE.getValue(), seq);
			String currentTime = CommonUtil.getNowTimestamp();
			
			remoteCSE.setResourceType(CommonCode.RESOURCE_TYPE.REMOTE_CSE.getValue());
			remoteCSE.setResourceID(resourceId);
			remoteCSE.setCreationTime(currentTime);
			remoteCSE.setLastModifiedTime(currentTime);
			remoteCSE.setExpirationTime(remoteCSE.getExpirationTime());
			remoteCSE.setExpirationDate(CommonUtil.timestampToDate(remoteCSE.getExpirationTime()));
			//remoteCSE.getAccessControlPolicyIDs().add(accessControlPolicyItem.getResourceID());
			remoteCSE.getLabels().addAll(remoteCSE.getLabels());
			remoteCSE.getAnnounceTo().addAll(remoteCSE.getAnnounceTo());
			remoteCSE.getAnnouncedAttribute().addAll(remoteCSE.getAnnouncedAttribute());
			remoteCSE.setCseType(remoteCSE.getCseType());
			//remoteCSE.getPointOfAccess().addAll(remoteCSE.getPointOfAccess());
			remoteCSE.setCSEBase(remoteCSE.getCSEBase());
			remoteCSE.setM2MExtID(remoteCSE.getM2MExtID());
			remoteCSE.setTriggerRecipientID(remoteCSE.getTriggerRecipientID());
			remoteCSE.setRequestReachability(remoteCSE.isRequestReachability());
			//remoteCSE.setPassCode(remoteCSE.getPassCode());
			//remoteCSE.setMappingYn(remoteCSE.getMappingYn());
			//remoteCSE.setResourceRef(new ResourceRef(mCommonService.getContentLocation(url, remoteCSEItem), remoteCSEItem.getResourceName(), CommonCode.RESOURCE_TYPE.REMOTE_CSE.getValue(), remoteCSEItem.getResourceID(), null));
			
			remoteCseDao.insert(remoteCSE);
			
			return remoteCSE;
		} catch (Exception e) {
			e.printStackTrace();
			throw new RSCException(CommonCode.RSC.INTERNAL_SERVER_ERROR, MessageProperties.getProperty("aimir.iot.dev.reg.fail"));
		}
	}
}
