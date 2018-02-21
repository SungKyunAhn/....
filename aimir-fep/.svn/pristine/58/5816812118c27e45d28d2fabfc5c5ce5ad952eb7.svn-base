package com.aimir.fep.util.iot;

import java.io.Serializable;
import java.util.Map;

import org.apache.kafka.common.serialization.Serializer;
import com.aimir.fep.iot.domain.resources.Resource;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ResourceSerializer implements Serializer<Resource>{
  
  @Override 
  public void configure(Map<String, ?> map, boolean b) {
  }
  
  @Override
  public byte[] serialize(String arg0, Resource arg1) {
    byte[] retVal = null;
    ObjectMapper objectMapper = new ObjectMapper();
    try {
      retVal = objectMapper.writeValueAsString(arg1).getBytes();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return retVal;
  }
  
  @Override 
  public void close() {
  }

}


