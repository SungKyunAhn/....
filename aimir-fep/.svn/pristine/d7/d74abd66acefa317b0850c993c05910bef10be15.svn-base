package com.aimir.fep.test;

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
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class TestEvent_203_10_0
{
    private static Log _log = LogFactory.getLog(TestEvent_203_10_0.class);

    /**
     * constructor
     */
    public TestEvent_203_10_0()
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
        event.setCode(mibUtil.getOid("eventSensorAlarm"));
        event.setSrcType(ServiceDataConstants.E_SRCTYPE_ZMU);
        //event.setSrcId(new HEX("76F0AC87DBB7E45C"));
        event.setTimeStamp(new TIMESTAMP(TimeUtil.getCurrentTime()));
        SMIValue smiValue = null;
        smiValue = DataUtil.getSMIValueByObject("sensorID",
                "76F0AC87DBB7E45C"); 
        event.append(smiValue);
        _log.info("event : "+ event);
        GPRSTarget target = new GPRSTarget("127.0.0.1",8001);
        target.setTargetId("3000");
        Client client = ClientFactory.getClient(target);
        client.sendEvent(event);
    }

    public static void main(String[] args)
    {
        TestEvent_203_10_0 mc = new TestEvent_203_10_0();
        try {
            mc.sendEvent();
        }catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
