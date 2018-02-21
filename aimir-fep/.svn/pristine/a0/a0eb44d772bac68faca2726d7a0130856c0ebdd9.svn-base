package com.aimir.fep.event;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.constants.CommonConstants.EventStatus;
import com.aimir.constants.CommonConstants.SeverityType;
import com.aimir.constants.CommonConstants.TargetClass;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.EventUtil;
import com.aimir.model.device.EventAlert;
import com.aimir.model.device.EventAlertLog;
import com.aimir.model.system.Location;
import com.aimir.model.system.Supplier;
import com.aimir.util.DateTimeUtil;

public class TestEventUtil {
    private static Log log = LogFactory.getLog(TestEventUtil.class);
    
    @Ignore
    public void test_EquipmentRegistration() {
        try {
            DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
            EventUtil.sendEvent("Equipment Registration",
                    TargetClass.Modem,
                    "000D6F0000234633",
                    new String[][] {
                                    {"mcuID", "8001"},
                                    {"sensorID", "Id[000D6F0000234633] Port[0]"}
                    },
                    new EventAlertLog());
        }
        catch (Exception e) {
            log.error(e);
        }
    }
    
    @Test
    public void test_dooropen() throws Exception {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
        
        for (int i=0; i < 1000; i++) {
            try {
                EventAlertLog eal = new EventAlertLog();
                eal.setId((new Date()).getTime());
                eal.setActivatorId("17449846");
                eal.setActivatorType(TargetClass.EnergyMeter);
                EventAlert ea = new EventAlert();
                ea.setId(15);
                ea.setName("Cover Alarm");
                ea.setOid("200.25.0,200.26.0,210.1.0");
                eal.setEventAlert(ea);
                String currentTime = DateTimeUtil.getCurrentDateTimeByFormat("yyyyMMddHHmmss");
                eal.setOpenTime(currentTime);
                eal.setStatus(EventStatus.Open);
                eal.setSeverity(SeverityType.Warning);
                eal.setWriteTime(currentTime);
                eal.setMessage("Cover Open");
                Supplier supplier = new Supplier();
                supplier.setId(1);
                eal.setSupplier(supplier);
                Location loc = new Location();
                loc.setName("Accra");
                eal.setLocation(loc);
                
                EventUtil.sendNotification(eal);
            }
            catch (Exception e) {}
            Thread.sleep(1000);
        }
    }
    
    @Ignore
    public void test_getnotification() {
        try {
            DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
            EventUtil.getNotification();
        }
        catch (Exception e) {
            log.error(e, e);
        }
    }
}
