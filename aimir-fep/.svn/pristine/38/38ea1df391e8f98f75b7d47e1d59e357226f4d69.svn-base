package com.aimir.fep.protocol.fmp.client;

import org.junit.Test;

import com.aimir.constants.CommonConstants.McuType;
import com.aimir.constants.CommonConstants.Protocol;
import com.aimir.fep.BaseTestCase;
import com.aimir.fep.protocol.fmp.client.lan.LANClient;
import com.aimir.fep.protocol.fmp.common.LANTarget;
import com.aimir.fep.protocol.fmp.datatype.INT;
import com.aimir.fep.protocol.fmp.datatype.OID;
import com.aimir.fep.protocol.fmp.datatype.SMIValue;
import com.aimir.fep.protocol.fmp.datatype.STRING;
import com.aimir.fep.protocol.fmp.frame.service.CommandData;

public class IF4_1_2_ONDEMAND_LAN extends BaseTestCase{

    private static LANTarget target;
    
    @Test
    public void setUp(){
    	target = new LANTarget("localhost", 8900);
        target.setProtocol(Protocol.LAN);
        target.setTargetType(McuType.Indoor);
        target.setIpAddr("localhost");
        target.setPort(8900);
        target.setTargetId("testonly");
        target.setNameSpace("NG");
        //target.setMeterModel(MeterModel.ELSTER_A1830RLNQ.getCode());
    }
    
    @Test
    public void testOnDemandByMeter(){
        try {
        	
        	/*
        	1.9	INT	Option	Metering Option
        	1.9	INT	Offset	Metering Offset
        	1.9	INT	Count	Metering Count
        	1.11	STRING	MeterId	Target Meter Id
        	*/       	
            LANClient client = (LANClient)ClientFactory.getClient(target);
            CommandData command = new CommandData();
            command.setMcuId(target.getTargetId());
            command.setCmd(new OID("110.2.2"));//on-demand meter
            SMIValue noption = new SMIValue(new OID("1.9.0"),new INT(0));
            command.append(noption);
            SMIValue offset = new SMIValue(new OID("1.9.0"),new INT(0));
            command.append(offset);
            SMIValue count = new SMIValue(new OID("1.9.0"),new INT(12));
            command.append(count);
            SMIValue modemNumber = new SMIValue(new OID("1.11.0"),new STRING(""));
            command.append(modemNumber);
            client.sendCommand(command);
		}  catch (Exception e) {
			e.printStackTrace();
		}
    }
}
