package com.aimir.cms.util;

import java.io.InputStream;
import java.util.Properties;
import java.util.Enumeration;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * FMP properties manager
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class CMSProperty
{
    private static Log log = LogFactory.getLog(CMSProperty.class);
    private static final Properties properties;

    static {
        String it = "/cms.properties";

        Properties result = new Properties();
        try
        {
            InputStream is = 
                CMSProperty.class.getResourceAsStream(it);
            result.load(is);
            is.close();

        } catch(Exception e) {
            log.error(e, e);
        }

        properties = result;
    }

    private CMSProperty() {
        super();
    }

    /**
     * get PropertyURL
     *
     * @param key <code>String</code>
     * @return url <code>URL</code>
     */
    public static URL getPropertyURL(String key)
    {
        String val = properties.getProperty(key);

        URL url = null;
        try { url = new URL(val); }catch(Exception ex) {}

        if(url == null)
        {
            url = CMSProperty.class.getResource(val);
        }

        return url;

    }

    /**
     * get property
     *
     * @param key <code>String</code> key
     * @return value <code>String</code>
     */
    public static String getProperty(String key)
    {
        return properties.getProperty(key);
    }

    /**
     * get property
     *
     * @param key <code>String</code> key
     * @param key <code>String</code> value
     * @return value <code>String</code>
     */
    public static String getProperty(String key,String value)
    {
        return properties.getProperty(key,value);
    }

    /**
     * get property names
     *
     * @return enumeration <code>Enumeration</code>
     */
    public static Enumeration<?> propertyNames()
    {
        return properties.propertyNames();
    }
}
