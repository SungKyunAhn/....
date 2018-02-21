package com.aimir.fep.protocol.fmp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.client.Client;
import com.aimir.fep.protocol.fmp.client.ClientFactory;
import com.aimir.fep.protocol.fmp.common.GPRSTarget;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.TIMESTAMP;
import com.aimir.fep.protocol.fmp.frame.ServiceDataConstants;
import com.aimir.fep.protocol.fmp.frame.service.EventData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.MIBUtil;
import com.aimir.util.TimeUtil;

/**
 * Send Event Test Class
 * 
 * @author Yeon Kyoung Park 
 * @version $Rev: 1 $, $Date: 2007-04-21 15:59:15 +0900 $,
 */
public class TestEvent_208_1_0
{
    private static Log _log = LogFactory.getLog(TestEvent_208_1_0.class);

    /**
     * constructor
     */
    public TestEvent_208_1_0()
    {
    }
    /**
     * start to send event 
     *
     * @throws Exception
     */
    public void sendEvent() throws Exception
    {
        MIBUtil mibUtil = MIBUtil.getInstance();
        EventData event = new EventData();
        event.setCode(mibUtil.getOid("eventSynchSensorIndex"));
        event.setSrcType(ServiceDataConstants.E_SRCTYPE_MCU);
        //event.setSrcId(new HEX("76F0AC87DBB7E45C"));
        event.setTimeStamp(new TIMESTAMP(TimeUtil.getCurrentTime()));

        //mtrMember,BYTE,1,10.2.2,
        //mtrID,HEX,8,10.2.3,
        //mtrSerial,OCTET,18,10.2.4,      
        
        SMIValue smiValue = null;
        smiValue = DataUtil.getSMIValueByObject("mtrMember","1");
        event.append(smiValue);
        smiValue = DataUtil.getSMIValueByObject("mtrID","000B120000033F1A");
        event.append(smiValue);
        smiValue = DataUtil.getSMIValueByObject("mtrSerial","200701293000017");
        event.append(smiValue);
        _log.info("event : "+ event);
        GPRSTarget target = new GPRSTarget("187.1.10.58",8000);
        target.setTargetId("1003");
        Client client = ClientFactory.getClient(target);
        client.sendEvent(event);
    }

    public static void main(String[] args)
    {
        TestEvent_208_1_0 mc = new TestEvent_208_1_0();
        try {
            mc.sendEvent();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
