package com.aimir.fep.util.iot;

import java.math.BigInteger;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.common.serialization.Deserializer;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.kafka.support.converter.StringJsonMessageConverter;

import com.aimir.fep.iot.domain.resources.ContentInstance;
import com.aimir.fep.iot.domain.resources.DeviceInfo;
import com.aimir.fep.iot.domain.resources.Firmware;
import com.aimir.fep.iot.domain.resources.RemoteCSE;
import com.aimir.fep.iot.domain.resources.Resource;
import com.aimir.fep.iot.utils.CommonCode.MGMT_DEFINITION;
import com.aimir.fep.iot.utils.CommonCode.RESOURCE_TYPE;
import com.aimir.fep.util.Hex;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ResourceDeSerializer implements Deserializer<Resource>{

  private static Log log = LogFactory.getLog(ResourceDeSerializer.class);
  
  @Override 
  public void close() {
  }
  
  @Override 
  public void configure(Map<String, ?> map, boolean b) {
  }
  
  @Override
  public Resource deserialize(String arg0, byte[] arg1) {
	  //ObjectMapper mapper = new ObjectMapper();
	  Resource resource = null;
	    try {
	    	resource = getResourceType(arg1);
	    	log.info("###ResourceDeSerializer resource : [" + resource + "]");
	    	//resource = mapper.readValue(arg1, ContentInstance.class);
	    } catch (Exception e) {
	      e.printStackTrace();
	    }
	    return resource;
  }
  
  public Resource getResourceType(byte[] byteData) {
	ObjectMapper mapper = new ObjectMapper();
	Resource resource = null;
  	String str = new String(byteData);
  	
  	try {
	 	JsonElement element = StringToJsonArray(str);
	 	JsonObject jsonObj = element.getAsJsonObject();
	 	BigInteger rType = new BigInteger(jsonObj.get("resourceType").toString());
	 	log.info("###Resource Message rType : [" + rType + "]");
	 	
	 	RESOURCE_TYPE resourceType = RESOURCE_TYPE.getThis(rType);
	 	log.info("###Resource resourceType : [" + resourceType + "]");
	 	
	 	switch(resourceType) {
			/*case UNKNOW:
				return NEXT;*/
			case REMOTE_CSE:		
				return resource = mapper.readValue(byteData, RemoteCSE.class);
			case MGMT_OBJ:
				BigInteger mType = new BigInteger(jsonObj.get("mgd").toString());
				MGMT_DEFINITION mgmt_resourceType = MGMT_DEFINITION.getThis(mType);
				switch(mgmt_resourceType) {
					case FIRMWARE:
						return resource = mapper.readValue(byteData, Firmware.class);
					case DEVICE_INFO:
						return resource = mapper.readValue(byteData, DeviceInfo.class);
					default:
						return null;
				}
			case CONTENT_INSTANCE:
				return resource = mapper.readValue(byteData, ContentInstance.class);
			default:
				return null;
 		}
  	} catch (Exception e) {
		log.error("ResourceDeSerializer getResourceType Error ~~~ ");
  		e.printStackTrace();
	}
  	return resource;
  }
  
  public JsonElement StringToJsonArray(String str) {
      JsonParser jsonParser = new JsonParser();
      return jsonParser.parse(str);
  }
}


