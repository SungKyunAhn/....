package com.aimir.fep.protocol.fmp.parser.alarm;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.FMPProperty;

public class AlarmMessageFactory
{
    
    private static Log log = LogFactory.getLog(AlarmMessageFactory.class);
    private static final String prefix = 
        "Protocol.parser.alarm.vendor.";
    public static AlarmParser getInstance(int vendor)
    {
        AlarmParser parser = null;
        String className = null;
        try {
            className = FMPProperty.getProperty(prefix+vendor);
            Class cls = Class.forName(className);
            parser = (AlarmParser)cls.newInstance();
        }catch(Exception ex)
        {
            log.error("Can not find AlarmParser Class["
                    +className+"]",ex);
        }
        return parser;
    }
}
