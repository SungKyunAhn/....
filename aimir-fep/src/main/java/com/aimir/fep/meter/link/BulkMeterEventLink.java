package com.aimir.fep.meter.link;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BulkMeterEventLink implements MeterEventLink {
    private static Log log = LogFactory.getLog(BulkMeterEventLink.class);
    
    @Override
    public void execute(Object obj) {
        log.debug(obj.toString());
    }

}
