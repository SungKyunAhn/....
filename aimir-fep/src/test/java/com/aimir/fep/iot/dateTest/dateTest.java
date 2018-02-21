package com.aimir.fep.iot.dateTest;

import java.math.BigInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.aimir.fep.iot.domain.resources.Container;
import com.aimir.fep.iot.domain.resources.ContentInstance;
import com.aimir.fep.iot.domain.resources.DeviceInfo;
import com.aimir.fep.iot.domain.resources.Firmware;
import com.aimir.fep.iot.domain.resources.MgmtCmd;
import com.aimir.fep.iot.domain.resources.MgmtResource;
import com.aimir.fep.iot.domain.resources.RemoteCSE;
import com.aimir.fep.iot.domain.resources.Resource;
import com.aimir.fep.iot.service.action.OperationUtilAction;
import com.aimir.fep.iot.utils.DateUtil;
import com.aimir.fep.iot.utils.CommonCode.MGMT_DEFINITION;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class dateTest {

	private static Log log = LogFactory.getLog(dateTest.class);
	
	@Autowired
	OperationUtilAction operationUtilAction;
	
    @Test
    public void getResourceType() {
    	
    	String currentTime = DateUtil.getCurrentDateTime();
    	
    	String yyyymmddhhmmss = "2017-11-28T21:40:27-05:00";
    	
    	String tm = yyyymmddhhmmss.replaceAll(":", "");
    	tm = tm.replaceAll("T", "");
    	tm = tm.replaceAll("-", "");
    	tm = tm.substring(0, 14);
    	
    	try {
    	 	
    		log.info("###tm : " + tm);
    		
    	 	log.info("###currentTime yyyyMMddHHmmss : " + currentTime);
    	 	
    	 	log.info("###yyyymmdd : " + yyyymmddhhmmss.substring(0, 4)+yyyymmddhhmmss.substring(5, 7)+yyyymmddhhmmss.substring(8, 10));
    	 	log.info("###HHmmss : " + yyyymmddhhmmss.substring(11, 13)+yyyymmddhhmmss.substring(14, 16)+yyyymmddhhmmss.substring(17, 19));
    	 	
    	 	//RESOURCE_TYPE resourceType = RESOURCE_TYPE.getThis(rType);
    	 	//String resourceName = ((Resource)obj).getResourceName();
    	 	
    	 	//log.info("###Resource resourceType : [" + resourceType.getName() + "]");
    	 	//log.info("###Resource resourceType : [" + getBean(RESOURCE_TYPE.CSE_BASE) + "]");

        	/*resource = (Resource)Class.forName(resourceType.getName()).newInstance();
        	log.info("###Resource resourceType : [" + resource + "]");*/
    	 	//resource = mapper.readValue(arg1, ContentInstance.class);
    	 	
    	} catch (Exception e) {
    	      e.printStackTrace();
    	}
    	
    }   
}
