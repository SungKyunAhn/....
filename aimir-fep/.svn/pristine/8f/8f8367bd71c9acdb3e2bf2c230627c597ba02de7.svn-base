package com.aimir.fep.iot.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.common.RSCException;
import com.aimir.fep.iot.domain.resources.Node;
import com.aimir.fep.iot.domain.resources.RemoteCSE;
import com.aimir.fep.iot.service.dao.NodeDao;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.fep.iot.utils.SequenceUtils.SEQ_PREFIX;
import com.aimir.fep.util.DataUtil;

@Service
public class NodeService {

	private static Log logger = LogFactory.getLog(NodeService.class);
	
	@Autowired
	NodeDao nodeDao;
	
	public Node findOneNodeByResourceName(String seq, String resourceName) throws Exception {

		Node node = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("resourceName").is(resourceName));

		try {
			node = (Node) nodeDao.findOne(query);
		} catch (Exception e) {
			logger.error("*** ["+seq+"] node Resource findOne exception:"+e.getMessage()+" ***");
			throw e;
		}

		return node;
	}
	
	public Node findOneNodeByResourceName(String seq, String parentID, String resourceName) throws Exception {

		Node node = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("parentID").is(parentID));
		query.addCriteria(Criteria.where("resourceName").is(resourceName));

		try {
			node = (Node) nodeDao.findOne(query);
		} catch (Exception e) {
			logger.error("*** ["+seq+"] node Resource findOne exception:"+e.getMessage()+" ***");
			throw e;
		}

		return node;
	}
	
	public Node insert(String seq, RemoteCSE remoteCSE) throws Exception {
		try {
			Node node = new Node();
			String resourceId = CommonUtil.seqIDToResourceID(SEQ_PREFIX.NODE.getValue(), seq);
			String currentTime = CommonUtil.getNowTimestamp();
			
			node.setResourceType(RESOURCE_TYPE.NODE.getValue());
			node.setResourceID(resourceId);
			node.setResourceName(remoteCSE.getResourceName());
			node.setParentID(remoteCSE.getParentID());
			node.setExpirationTime(remoteCSE.getExpirationTime());
			node.setExpirationDate(remoteCSE.getExpirationDate());
			node.getAccessControlPolicyIDs().addAll(remoteCSE.getAccessControlPolicyIDs());
			node.setCreationTime(currentTime);
			node.setLastModifiedTime(currentTime);
			node.getLabels().addAll(remoteCSE.getLabels());
			//nodeItem.getAnnounceTo().addAll(remoteCSEProfile.getAnnounceTo());
			//nodeItem.getAnnouncedAttribute().addAll(remoteCSEProfile.getAnnouncedAttribute());
			node.setNodeID(remoteCSE.getCSEID());
			node.setHostedCSELink(remoteCSE.getResourceID());
			//nodeItem.setResourceRef(new ResourceRef(mCommonService.getContentLocation(url, nodeItem), nodeItem.getResourceName(), CommonCode.RESOURCE_TYPE.NODE.getValue(), nodeItem.getResourceID(), null));
			
			nodeDao.insert(node);
			
			return node;
		}catch(Exception e) {
			logger.error("*** ["+seq+"] node Resource insert exception:"+e.getMessage()+" ***");
			throw e;
			
		}
	}
}
