package com.aimir.fep.protocol.fmp.client;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.constants.CommonConstants.Interface;
import com.aimir.fep.protocol.fmp.client.cdma.CDMAAMUClient;
import com.aimir.fep.protocol.fmp.common.CDMATarget;
import com.aimir.fep.protocol.fmp.frame.AMUGeneralDataFrame;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Send Command Test Class
 * 
 * @author J.S Park (elevas@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2011-05-24 15:59:15 +0900 $,
 */
public class TestAMUClient
{
    private static Log _log = LogFactory.getLog(TestAMUClient.class);
    
    static {
        DataUtil.setApplicationContext(new ClassPathXmlApplicationContext(new String[]{"/config/spring.xml"}));
    }
    
    /**
     * constructor
     */
    public TestAMUClient()
    {
    }

    /**
     * start to send command 
     *
     * @throws Exception
     */
    @Test
    public void test_AMUClient() throws Exception
    {
        CDMATarget target = new CDMATarget("01041356826","187.1.5.236","1","1");
        target.setTargetId("3000");
        target.setInterfaceType(Interface.AMU);
        CDMAAMUClient client = (CDMAAMUClient)ClientFactory.getClient(target);
        client.connect();
        
        AMUGeneralDataFrame request = new AMUGeneralDataFrame();
        // request 내용을 채워야 함.
        AMUGeneralDataFrame response = client.amuSendCommand(request);
        _log.debug(Hex.decode(response.getAmuFramePayLoad().getRawData()));
    }
}
