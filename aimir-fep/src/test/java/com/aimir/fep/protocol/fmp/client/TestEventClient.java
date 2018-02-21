package com.aimir.fep.protocol.fmp.client;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.dao.device.MeterDao;
import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.ClientFactory;
import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.util.DataUtil;
import com.aimir.model.device.Meter;
import com.aimir.util.Condition;
import com.aimir.util.TimeUtil;
import com.aimir.util.Condition.Restriction;

import org.junit.Test;

/**
 * Send Event Test Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class TestEventClient
{
    private static Log _log = LogFactory.getLog(TestEventClient.class);
    
    private int tcount = 1;

    static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
    }
    
    /**
     * constructor
     */
    public TestEventClient()
    {
    }

    /**
     * start to send event 
     *
     * @throws Exception
     */
    public void start() throws Exception
    {
        GPRSTarget target = new GPRSTarget("187.1.10.58",8000);
        target.setTargetId("1000");
        for(int i = 0 ; i < tcount ; i++)
        {
            ClientThread ct = new ClientThread(target);
            ct.run();
        }
    }

    /**
     * Client Thread
     */
    public class ClientThread 
    {
        private GPRSTarget target = null;
        public ClientThread(GPRSTarget target)
        {
            this.target = target;
        }
        public void run()
        {
            try{
            Client client = ClientFactory.getClient(target);
            EventData event = new EventData();
            event.setCode(new OID("200.1.0"));
            event.setSrcType(ServiceDataConstants.E_SRCTYPE_MCU);
            //event.setSrcId(new HEX("76F0AC87DBB7E45C"));
            event.setTimeStamp(new TIMESTAMP(TimeUtil.getCurrentTime()));
            SMIValue smiValue = null;
            smiValue = DataUtil.getSMIValueByObject("sysPhoneNumber",
                    "01191807327"); 
            event.append(smiValue);
            smiValue = DataUtil.getSMIValueByObject("ethIpAddr", 
                    "187.1.5.235"); 
            event.append(smiValue);
            smiValue = DataUtil.getSMIValueByObject("sysType","0"); 
            event.append(smiValue);
            smiValue = DataUtil.getSMIValueByObject("sysMobileType","0"); 
            event.append(smiValue);
            smiValue = DataUtil.getSMIValueByObject("sysMobileMode","2"); 
            event.append(smiValue);
            smiValue = DataUtil.getSMIValueByObject("sysLocalPort","8000"); 
            event.append(smiValue);
            client.sendEvent(event);
            }catch(Exception ex) { ex.printStackTrace(); }
            _log.info("end ClientThread");
        }
    } 

    @Test
    public void test_getMeterByCondition() {
        MeterDao meterDao = (MeterDao)DataUtil.getBean(MeterDao.class);
        Set<Condition> condition = new HashSet<Condition>();
        condition.add(new Condition("modem.deviceSerial", new Object[] { "000D6F0000AC5233" }, null, Restriction.EQ));
        condition.add(new Condition("modemPort", new Object[] { 0 }, null, Restriction.EQ));
        
        List<Meter> meters = meterDao.findByConditions(condition);
        if(meters != null && meters.size() > 0){
            _log.info(meters.size());
        }

    }
    
    @Test
    public void test_getMeterByDao() {
        MeterDao meterDao = (MeterDao)DataUtil.getBean(MeterDao.class);
        Meter meter = meterDao.getMeterByModemDeviceSerial("000D6F0000AC5233", 0);
        _log.info(meter.toString());
    }
}
