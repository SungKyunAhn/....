package com.aimir.fep.protocol.fmp.client;

import java.util.Vector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.protocol.fmp.common.CDMATarget;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.MIBNode;
import com.aimir.fep.util.MIBUtil;

/**
 * Send Command Test Class
 * 
 * @author D.J Park (dong7603@nuritelecom.com)
 * @version $Rev: 1 $, $Date: 2005-11-21 15:59:15 +0900 $,
 */
public class TestCDMAClient
{
    private static Log _log = LogFactory.getLog(TestCDMAClient.class);


    /**
     * constructor
     */
    public TestCDMAClient()
    {
    }

    /**
     * start to send command 
     *
     * @throws Exception
     */
    public void start() throws Exception
    {
        CDMATarget target = new CDMATarget("01092862473","187.1.5.236","1","1");
        target.setTargetId("3000");
        Client client = ClientFactory.getClient(target);

        CommandData command = new CommandData();
        command.setMcuId(target.getTargetId());
        command.setCmd(new OID("199.1.0"));

        MIBUtil mibUtil = MIBUtil.getInstance();
        Vector<?> nodes = (Vector<?>)mibUtil.getDataMIBsByName("sysEntry");
        SMIValue smiValue = null;
        for(int i = 0 ; i < nodes.size() ; i++)
        {
            MIBNode node = (MIBNode)nodes.elementAt(i);
            smiValue = DataUtil.getSMIValue(node.getName(),null);
            command.append(smiValue);
            if(i > 3)
                break;
        }

        command = client.sendCommand(command);

        _log.info("Result["+command.toString()+"]");

    }

    public static void main(String[] args)
    {
        TestCDMAClient mc = null;
        mc = new TestCDMAClient();
        try { mc.start(); } catch(Exception ex)
        { ex.printStackTrace(); }
        
        _log.info("end Main");
        System.exit(-1);
    }
}
