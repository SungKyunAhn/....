package com.aimir.fep.protocol.fmp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.datatype.OCTET;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.protocol.fmp.processor.EventProcessor;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.fep.util.MIBUtil;
import com.aimir.util.TimeUtil;

/**
 * Send Event Test Class
 * 
 * @author jihoon (jihoon@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2012-08-22 13:45:15 +0900 $,
 */
public class EV_217_1_0_Test
{
    private static Log log = LogFactory.getLog(EV_217_1_0_Test.class);
    static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring-listener.xml"}));
    }
    
    @Test
    public void test_IHD()
    {
        try {
            log.info("targetIp="+System.getProperty("targetIp") + ", port=" + System.getProperty("targetPort"));
            
            MIBUtil mibUtil = MIBUtil.getInstance();
            EventData event = new EventData();
            event.setCode(mibUtil.getOid("eventIHD"));
            event.setSrcType(ServiceDataConstants.E_SRCTYPE_MCU);
            //event.setSrcId(new HEX("76F0AC87DBB7E45C"));
            event.setTimeStamp(new TIMESTAMP(TimeUtil.getCurrentTime()));
            SMIValue smiValue = null;
            smiValue = DataUtil.getSMIValueByObject("sensorID", "000D6F00015DB82B"); 
            event.append(smiValue);
            byte[] byteDatas = Hex.encode("02495301003103");
            log.debug(Hex.getHexDump(byteDatas));
            smiValue = DataUtil.getSMIValue(new OCTET(byteDatas));
            event.append(smiValue);
            
            event.setMcuId("0");
            event.setIpAddr("127.0.0.1");
            EventProcessor ep = (EventProcessor) DataUtil.getBean("eventProcessor");
            // ep.processing(event);
//            log.info("event : "+ event);
            /*GPRSTarget target = new GPRSTarget("127.0.0.1",8001);
            target.setTargetId("100");
//            GPRSTarget target = new GPRSTarget(System.getProperty("targetIp"),
//                    Integer.parseInt(System.getProperty("targetPort")));
//            target.setTargetId("1001");
            Client client = ClientFactory.getClient(target);
            client.sendEvent(event);*/
        }
        catch (Exception e) {
            log.error(e);
        }
    }
}
