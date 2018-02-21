package com.aimir.fep.protocol.fmp.client;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.dao.system.LocationDao;
import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.FMPProperty;
import com.aimir.fep.util.MIBUtil;
import com.aimir.model.system.Location;
import com.aimir.util.TimeUtil;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Send Event Test Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class EV_200_11_0_Test
{
    private static Log log = LogFactory.getLog(EV_200_11_0_Test.class);
    static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
    }
    @Autowired
    LocationDao locationDao;
    
    @Test
    public void test_login()
    {
        try {
            log.info("targetIp="+System.getProperty("targetIp") + ", port=" + System.getProperty("targetPort"));
            
            MIBUtil mibUtil = MIBUtil.getInstance();
            EventData event = new EventData();
            event.setCode(mibUtil.getOid("eventMcuOamLogin"));
            event.setSrcType(ServiceDataConstants.E_SRCTYPE_MCU);
            //event.setSrcId(new HEX("76F0AC87DBB7E45C"));
            event.setTimeStamp(new TIMESTAMP(TimeUtil.getCurrentTime()));
            SMIValue smiValue = null;
            smiValue = DataUtil.getSMIValueByObject("ipEntry", "187.1.1.1"); 
            event.append(smiValue);
            
            List<Location> locations = null;
        	String defaultLocName = FMPProperty.getProperty("loc.default.name");
        	log.info("defaultLocName : "+defaultLocName);
        	if(defaultLocName != null && !"".equals(defaultLocName)){
        		locations = locationDao.getLocationByName(defaultLocName);
        		log.info(locations);

        	}else{
        		locations = locationDao.getAll();
        	}
            if(locations != null && locations.size() > 0){
                locations.get(0);                  
            }
            
            log.info("event : "+ event);
            GPRSTarget target = new GPRSTarget(System.getProperty("targetIp"),
                    Integer.parseInt(System.getProperty("targetPort")));
            target.setTargetId("1001");
            Client client = ClientFactory.getClient(target);
            client.sendEvent(event);
        }
        catch (Exception e) {
            log.error(e);
        }
    }
}
