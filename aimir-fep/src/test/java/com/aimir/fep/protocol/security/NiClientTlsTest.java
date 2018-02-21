package com.aimir.fep.protocol.security;

import com.aimir.fep.protocol.fmp.common.Target;
import com.aimir.fep.protocol.nip.client.NiClient;
import com.aimir.fep.protocol.nip.common.GeneralDataFrame;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import org.junit.Test;

import java.util.HashMap;

public class NiClientTlsTest
{
    @Test
    public void test() throws Exception {
        NiClient client = new NiClient();
        Target target = new Target();
        target.setIpv6Addr("187.1.30.155");
        target.setPort(8004);
//         target.setIpv6Addr("187.1.10.111");
//         target.setPort(8001);

        /*
        GeneralFrame frame = new GeneralFrame();
        frame._networkType = NetworkType.Ethernet;
        frame.fcAck = FrameControl_Ack.None;
//        frame.fcAck = FrameControl_Ack.Ack;
//        client.sendCommand(target, frame, new byte[]{(byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF, (byte)0xFF});
        client.sendCommand(target, frame, new byte[]{(byte)0x51, (byte)0xF8, (byte)0x04, (byte)0x00, (byte)0x03, (byte)0x00, (byte)0x00, (byte)0x09, (byte)0x01, (byte)0x00, (byte)0x01, (byte)0x10, (byte)0x06, (byte)0x00, (byte)0x02, (byte)0x00, (byte)0x01, (byte)0x08, (byte)0x2C});
        */

        byte[] maxPayloadcnt = new byte[] { (byte) 0x00 };
        // command frame
        GeneralFrame command = new GeneralFrame();
        GeneralDataFrame data = new GeneralDataFrame();
        command.setClientBaseInfo(command.niAttributeId.ModemEventLog, command._commandFlow.Request,
                command._commandType.Get, command._networkType.Ethernet, command.fcPending.LastFrame,
                command.fcAck.None, command.foNetworkStatus.None, command.foAddrType.None, command.foType.Command,
                maxPayloadcnt, null, null);

        // create request variable
        HashMap param = new HashMap();
        param.put("count", 1);
        param.put("baudRate", 38400);

        //create request data
        byte[] sendData = data.make(command, param);
        client.sendCommand(target, command, sendData);
    }
}
