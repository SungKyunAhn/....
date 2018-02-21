package com.aimir.fep.iot.jsonParsing;

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
import com.aimir.fep.iot.utils.CommonCode.MGMT_DEFINITION;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class jsonParsing {

	private static Log log = LogFactory.getLog(jsonParsing.class);
	
	@Autowired
	OperationUtilAction operationUtilAction;
	
    @Test
    public void getResourceType() {
    	ObjectMapper mapper = new ObjectMapper();
    	Resource resource = null;
    	
    	try {
    	 	BigInteger rType = new BigInteger("4");	 	
    	 	log.info("###Resource Message Type : [" + rType + "]");
    	 	
    	 	//resource = getResourceString(rType);
    	 	log.info("###Resource Message obj : [" + resource + "]");
    	 	
    	 	String creationTime = "2017-11-13T13:59:59+09:00";
    	 	log.info("###creationTime yyyymmdd : " + creationTime.substring(0, 4)+creationTime.substring(5, 7)+creationTime.substring(8, 10));
    	 	log.info("###creationTime hhmmss : " + creationTime.substring(11, 13)+creationTime.substring(14, 16)+creationTime.substring(17, 19));
    	 	
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
    
    public Resource getResourceString(BigInteger rType) throws Exception {
    	ObjectMapper mapper = new ObjectMapper();
    	RESOURCE_TYPE resourceType = RESOURCE_TYPE.getThis(rType);
		Resource resource = null;
		
		switch(resourceType) {
		/*case UNKNOW:
			return NEXT;*/
		case REMOTE_CSE:		
			//resource = mapper.readValue(byteData, RemoteCSE.class);
		case MGMT_OBJ:
			log.info("###Resource MGMT_OBJ");
		case CONTENT_INSTANCE:
			log.info("###Resource CONTENT_INSTANCE");
			//resource = mapper.readValue(byteData, ContentInstance.class);
		default:
			resource = null;
		}
		return resource;
	}
    
    
   /* @Test
    public Resource deserialize(String arg0, byte[] arg1) {
  	  ObjectMapper mapper = new ObjectMapper();
  	  Resource resource = null;
  	    try {
  	    	getResourceType(arg1);
  	    	
  	    	resource = mapper.readValue(arg1, ContentInstance.class);
  	    } catch (Exception e) {
  	      e.printStackTrace();
  	    }
  	    return resource;
    }*/
    
}
