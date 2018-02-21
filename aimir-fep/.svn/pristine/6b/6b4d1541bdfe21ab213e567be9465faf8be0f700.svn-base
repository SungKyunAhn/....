package com.aimir.fep.command.conf;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Mccb
{
    private static Log _log = LogFactory.getLog(Mccb.class);
    
    public static String PE = "[PE]Phase Error";
    public static String DE = "[DE]Device Error";
    public static String NPE = "[NPE]No Phase Error";
    public static String NDE = "[NDE]No Device Error";
    public static String RE = "[RE]Relay Error";
    public static String BE = "[BE]Button Error";
    
    public static String[] MSG_REQ = {"",
                                      "",
                                      "",
                                      "Enable Use - Disconnected",
                                      "Enable Use - Automatically",
                                      "Enable Use - Connect Now",
                                      "Disable Use - Disconnect",
                                      "",
                                      "",
                                      "Get Device Status",
                                      "Get Phase Status",
                                      "Get Last Accepted Control Msg",
                                      "",
                                      "",
                                      "",
                                      ""};
    
    public static String[][] AIMIR_OPCODE = {
                                            { "Enable Use - Disconnected", "200.2.1" },
                                            { "Enable Use - Automatically", "200.2.2" },
                                            { "Enable Use - Connect Now", "200.2.3" },
                                            { "Disable Use - Disconnect", "200.2.4" },
                                            { "Get Device Status", "200.2.5" },
                                            { "Get Phase Status", "200.2.6" },
                                            { "Get Last Accepted Control Msg","200.2.7" } };

    public static byte[] MSG_BIT = {(byte) 0x41,
                                    (byte) 0x47,
                                    (byte) 0x4B,
                                    (byte) 0x4D,
                                    (byte) 0x53,
                                    (byte) 0x55,
                                    (byte) 0x59,
                                    (byte) 0x5F,
                                    (byte) 0x63,
                                    (byte) 0x65,
                                    (byte) 0x69,
                                    (byte) 0x6F,
                                    (byte) 0x71,
                                    (byte) 0x77,
                                    (byte) 0x7B,
                                    (byte) 0x7D,
                                    (byte) 0x7F,
                                    (byte) 0xFF};
    
    public static String[] MSG_ACK = {"",
                                      "",
                                      "OK",
                                      "",
                                      DE,
                                      PE,
                                      DE + "+" + PE,
                                      "",
                                      "",
                                      "Rejected " +
                                      "MCCB has already accepted the same message or " +
                                      "MCCB has already accepted a Enable message and different enable message is sent",
                                      "",
                                      "",
                                      "Undefined " +
                                      "frame is ok but message is not recognized",
                                      "",
                                      "",
                                      "",
                                      "MCCB Communication Fail",
                                      "Not Ready"};
    
    public static String[] MSG_DEV = {NDE + ";" + NPE,
                                      NDE + ";" + PE,
                                      "",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "",
                                      DE + ";" + NPE,
                                      DE + ";" + PE,
                                      DE + "+" + BE,
                                      DE + "+" + BE + ";" + PE,
                                      DE + "+" + RE,
                                      DE + "+" + RE + ";" + PE,
                                      DE + "+" + RE + "+" + BE,
                                      DE + "+" + RE + "+" + BE + ";" + PE,
                                      "MCCB Communication Fail",
                                      "Not Ready"};
    
    public static String[] MSG_LAC = {"",
                                      "",
                                      "",
                                      "OK+Enable Use - Disconnected",
                                      "OK+Enable Use - Automatically",
                                      "OK+Enable Use - Connected",
                                      "OK+Disable Use - Disconnect",
                                      "",
                                      "",
                                      "E+Enable Use - Disconnected",
                                      "E+Enable Use - Automatically",
                                      "E+Enable Use - Connected",
                                      "E+Disable Use - Disconnect",
                                      "",
                                      "",
                                      "",
                                      "MCCB Communication Fail",
                                      "Not Ready"};
    
    public static String[] MSG_PHA = {"All phases detected",
                                      "Phase 1 missing",
                                      "Phase 2 missing",
                                      "Phases 1 and 2 missing",
                                      "Phase 3 missing",
                                      "Phases 1 and 3 missing",
                                      "Phases 2 and 3 missing",
                                      "Phases 1, 2 and 3 missing",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "",
                                      "MCCB Communication Fail",
                                      "Not Ready"};
    
    public static byte getReqBit(String req) throws Exception {
        for (int i = 0; i < MSG_REQ.length; i++) {
            if (MSG_REQ[i].equalsIgnoreCase(req))
                return MSG_BIT[i];
        }
        throw new Exception("Request bits not found exception");
    }
    
    public static String getMsg(byte req, byte res) throws Exception {
        _log.debug("REQ[" + req + "],RES[" + res + "]");
        
        if (MSG_BIT[3] == req || MSG_BIT[4] == req ||
                MSG_BIT[5] == req || MSG_BIT[6] == req)
            return getMsgAck(res);
        else if (MSG_BIT[9] == req)
            return getMsgDev(res);
        else if (MSG_BIT[10] == req)
            return getMsgPha(res);
        else if (MSG_BIT[11] == req)
            return getMsgLac(res);
        else
            throw new Exception("MCCB request bit is wrong");
    }
    
    public static String getMsgAck(byte res) throws Exception {
        for (int i = 0; i < MSG_BIT.length; i++) {
            if (MSG_BIT[i] == res)
                return MSG_ACK[i];
        }
        throw new Exception("Acknowledge response message not found exception");
    }
    
    public static String getMsgDev(byte res) throws Exception {
        for (int i = 0; i < MSG_BIT.length; i++) {
            if (MSG_BIT[i] == res)
                return MSG_DEV[i];
        }
        throw new Exception("Device status response message not found exception");
    }
    
    public static String getMsgPha(byte res) throws Exception {
        for (int i = 0; i < MSG_BIT.length; i++) {
            if (MSG_BIT[i] == res)
                return MSG_PHA[i];
        }
        throw new Exception("Phase status response message not found exception");
    }
    
    public static String getMsgLac(byte res) throws Exception {
        for (int i = 0; i < MSG_BIT.length; i++) {
            if (MSG_BIT[i] == res)
                return MSG_LAC[i];
        }
        throw new Exception("Last accepted control message not found exception");
    }
    
    public static String getAimirOPCode(String req){
        for(int i=0;i<AIMIR_OPCODE.length;i++){
            if(AIMIR_OPCODE[i][0].equals(req)){
                return AIMIR_OPCODE[i][1];
            }
            
        }
        return null;
    }
}
