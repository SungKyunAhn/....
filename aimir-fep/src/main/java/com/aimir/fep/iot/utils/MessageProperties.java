package com.aimir.fep.iot.utils;

import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.FMPProperty;

public class MessageProperties {
	private static Log log = LogFactory.getLog(MessageProperties.class);
	private static final Properties properties;
	
	static {
		Properties prop = new Properties();
		try {
			String lang = FMPProperty.getProperty("iot.local", "en");
			String path = "/lang/message_" + lang + ".properties";
			
			InputStream is = MessageProperties.class.getResourceAsStream(path);
			prop.load(is);
			is.close();
		}catch(Exception e) {
			log.debug(e, e);
		}
		
		properties = prop;
	}
	
	private MessageProperties(){
		super();
	}
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public static String getProperty(String key, String value) {
		return properties.getProperty(key, value);
	}
	
}
