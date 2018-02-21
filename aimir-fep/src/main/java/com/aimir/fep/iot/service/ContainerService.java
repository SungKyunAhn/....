package com.aimir.fep.iot.service;

import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.common.RSCException;
import com.aimir.fep.iot.domain.resources.Container;
import com.aimir.fep.iot.domain.resources.Resource;
import com.aimir.fep.iot.service.dao.ContainerDao;
import com.aimir.fep.iot.utils.CommonCode;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.fep.iot.utils.SequenceUtils.SEQ_PREFIX;
import com.aimir.fep.util.DataUtil;

@Service
public class ContainerService {
	private static Log logger = LogFactory.getLog(ContainerService.class);
	
	@Autowired
	ContainerDao containerDao;
	
	public Container findOneContainerByResourceName(String seq, String resourceName) throws RSCException {
		Container container = null;
		
		Query query = new Query();
		query.addCriteria(Criteria.where("resourceName").is(resourceName));
		
		try {
			container = (Container)containerDao.findOne(query);	
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("*** ["+seq+"] container Resource findOne exception:"+e.getMessage()+" ***");
			throw new RSCException(CommonCode.RSC.INTERNAL_SERVER_ERROR,CommonUtil.getMessage("aimir.iot.container.find.fail"));
		}
		
		return container;
	}
	
	public Container findOneContainerByResourceName(String seq, String parentID, String resourceName) throws RSCException {
		Container container = null;
		
		Query query = new Query();
		query.addCriteria(Criteria.where("parentID").is(parentID));
		query.addCriteria(Criteria.where("resourceName").is(resourceName));
				
		try {
			container = (Container)containerDao.findOne(query);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("*** ["+seq+"] container Resource findOne exception:"+e.getMessage()+" ***");
			throw new RSCException(CommonCode.RSC.INTERNAL_SERVER_ERROR,CommonUtil.getMessage("aimir.iot.container.find.fail"));
		}
		
		return container;
	}
	
	public Container insert(String seq, String url, Resource parent, Container container) throws RSCException {
		
		String resourceId = CommonUtil.seqIDToResourceID(SEQ_PREFIX.CONTAINER.getValue(), seq);
		String currentTime = CommonUtil.getNowTimestamp();
		
		container.setParentID(parent.getResourceID());
		container.setCreator(parent.getResourceID());
		container.setResourceType(RESOURCE_TYPE.CONTAINER.getValue());
		container.setResourceID(resourceId);
		container.setResourceName(CommonUtil.isEmpty(container.getResourceName()) ? resourceId : container.getResourceName());
		//container.setResourceName(value);
		//container.setExpirationTime(value);
		container.setExpirationDate(CommonUtil.timestampToDate(container.getExpirationTime()));
		//container.getAccessControlPolicyIDs().addAll(containerProfile.getAccessControlPolicyIDs());
		//container.getLabels().addAll(container.getLabels());
		container.setCreationTime(currentTime);
		container.setLastModifiedTime(currentTime);
		container.setStateTag(BigInteger.ZERO);
		container.getAnnounceTo().addAll(container.getAnnounceTo());
		container.getAnnouncedAttribute().addAll(container.getAnnouncedAttribute());
		//container.setMaxNrOfInstances(containerProfile.getMaxNrOfInstances());
		//container.setMaxByteSize(containerProfile.getMaxByteSize());
		//container.setMaxInstanceAge(containerProfile.getMaxInstanceAge());
		container.setCurrentNrOfInstances(BigInteger.ZERO);
		container.setCurrentByteSize(BigInteger.ZERO);
		//container.setContainerType(containerProfile.getContainerType());
		//container.setOntologyRef(containerProfile.getOntologyRef());
		//container.setHeartbeatPeriod(containerProfile.getHeartbeatPeriod());
		//container.setResourceRef(new ResourceRef(mCommonService.getContentLocation(url, cntrItem), cntrItem.getResourceName(), RESOURCE_TYPE.CONTAINER.getValue(), cntrItem.getResourceID(), null));
		
		try {
			containerDao.insert(container);
			
			return container;
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("*** ["+seq+"] container Resource create exception:"+e.getMessage()+" ***");
			throw new RSCException(CommonCode.RSC.INTERNAL_SERVER_ERROR,CommonUtil.getMessage("aimir.iot.container.create.fail"));
		}
	}
}
