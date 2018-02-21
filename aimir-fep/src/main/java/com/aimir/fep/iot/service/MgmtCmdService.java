package com.aimir.fep.iot.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.aimir.fep.iot.domain.common.RSCException;
import com.aimir.fep.iot.domain.resources.MgmtCmd;
import com.aimir.fep.iot.domain.resources.RemoteCSE;
import com.aimir.fep.iot.service.dao.MgmtCmdDao;
import com.aimir.fep.iot.utils.CommonCode;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;
import com.aimir.fep.iot.utils.CommonUtil;
import com.aimir.fep.iot.utils.SequenceUtils.SEQ_PREFIX;
import com.aimir.fep.util.DataUtil;

@Service
public class MgmtCmdService {
	private static Log logger = LogFactory.getLog(MgmtCmdService.class);
	
	@Autowired
	MgmtCmdDao mgmtCmdDao;
	
	public MgmtCmd findOneMgmtCmdByResourceName(String seq, String resourceName) throws Exception {
		MgmtCmd mgmtCmd = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("resourceName").is(resourceName));

		try {
			mgmtCmd = (MgmtCmd) mgmtCmdDao.findOne(query);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("*** ["+seq+"] firmware Resource findOne exception:"+e.getMessage()+" ***");
			throw new RSCException(CommonCode.RSC.INTERNAL_SERVER_ERROR,CommonUtil.getMessage("aimir.iot.mgmtCmd.find.fail"));
		}

		return mgmtCmd;
	}
	
	public MgmtCmd findOneMgmtCmdByResourceName(String seq, String parentID, String resourceName) throws Exception {
		MgmtCmd mgmtCmd = null;

		Query query = new Query();
		query.addCriteria(Criteria.where("parentID").is(parentID));
		query.addCriteria(Criteria.where("resourceName").is(resourceName));

		try {
			mgmtCmd = (MgmtCmd) mgmtCmdDao.findOne(query);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("*** ["+seq+"] firmware Resource findOne exception:"+e.getMessage()+" ***");
			throw new RSCException(CommonCode.RSC.INTERNAL_SERVER_ERROR,CommonUtil.getMessage("aimir.iot.mgmtCmd.find.fail"));
		}

		return mgmtCmd;
	}
	
	public MgmtCmd insert(String seq, RemoteCSE parent, MgmtCmd mgmtCmd) throws RSCException {
		String resourceId = CommonUtil.seqIDToResourceID(SEQ_PREFIX.MGMT_CMD.getValue(), seq);
		String currentTime = CommonUtil.getNowTimestamp();
		
		mgmtCmd.setResourceType(RESOURCE_TYPE.MGMT_CMD.getValue());
		mgmtCmd.setResourceID(resourceId);
		mgmtCmd.setResourceName(!CommonUtil.isEmpty(mgmtCmd.getResourceName()) ? mgmtCmd.getResourceName() : resourceId);
		mgmtCmd.setParentID(parent.getResourceID());
		mgmtCmd.setExpirationTime(mgmtCmd.getExpirationTime());
		mgmtCmd.setExpirationDate(CommonUtil.timestampToDate(mgmtCmd.getExpirationTime()));
		//mgmtCmdItem.getAccessControlPolicyIDs().addAll(mgmtCmdProfile.getAccessControlPolicyIDs());
		//mgmtCmdItem.getLabels().addAll(mgmtCmdProfile.getLabels());
		mgmtCmd.setCreationTime(currentTime);
		mgmtCmd.setLastModifiedTime(currentTime);
		//mgmtCmdItem.setDescription(mgmtCmdProfile.getDescription());
		//mgmtCmdItem.setCmdType(mgmtCmdProfile.getCmdType());
		//mgmtCmdItem.setExecReqArgs(mgmtCmdProfile.getExecReqArgs());
		//mgmtCmdItem.setExecEnable(mgmtCmdProfile.isExecEnable());
		//mgmtCmdItem.setExecTarget(mgmtCmdProfile.getExecTarget());
		//mgmtCmdItem.setExecMode(mgmtCmdProfile.getExecMode());
		//mgmtCmdItem.setExecFrequency(mgmtCmdProfile.getExecFrequency());
		//mgmtCmdItem.setExecDelay(mgmtCmdProfile.getExecDelay());
		//mgmtCmdItem.setExecNumber(mgmtCmdProfile.getExecNumber());
		//mgmtCmdItem.setResourceRef(new ResourceRef(mCommonService.getContentLocation(url, mgmtCmdItem), mgmtCmdItem.getResourceName(), RESOURCE_TYPE.MGMT_CMD.getValue(), mgmtCmdItem.getResourceID(), null));
		
		try {
			mgmtCmdDao.insert(mgmtCmd);
		}catch(Exception e) {
			e.printStackTrace();
			logger.error("*** ["+seq+"] container Resource create exception:"+e.getMessage()+" ***");
			throw new RSCException(CommonCode.RSC.INTERNAL_SERVER_ERROR,CommonUtil.getMessage("aimir.iot.mgmtCmd.create.fail"));
		}
		
		return mgmtCmd;
	}
}
