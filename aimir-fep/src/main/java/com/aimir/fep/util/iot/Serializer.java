package com.aimir.fep.util.iot;

import java.io.Closeable;
import java.util.Map;
import com.aimir.fep.iot.domain.resources.Resource;

public interface Serializer extends Closeable {
  
  void configure(Map<String, ?> var1, boolean var2);
  
  byte[] serialize(String var1, Resource var2);
  
  void close();
}