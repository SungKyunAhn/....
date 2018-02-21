package com.aimir.fep.command.conf;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.CRCUtil;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.util.DateTimeUtil;

public class KamstrupCIDMeta
{
    private static Log _log = LogFactory.getLog(KamstrupCIDMeta.class);
    
    public static byte[][] getRequest(String[] req) throws Exception {
        for (int i = 0; i < CID.values().length; i++) {
            if (req[0].equals(CID.values()[i].getCommand()))
                return CID.values()[i].getRequest(req);
        }
        
        throw new Exception("Not found command[" + req[0] + "]");
    }
    
    public static String[] getRequestValue(String[] req) throws Exception {
        for (int i = 0; i < CID.values().length; i++) {
            if (req[0].equals(CID.values()[i].getCommand()))
                return CID.values()[i].getRequestValue(req);
        }
        
        throw new Exception("Not found command[" + req[0] + "]");
    }
    
    public static String getAimirOpCode(String[] req){
        for (int i = 0; i < CID.values().length; i++) {
            if (req[0].equals(CID.values()[i].getCommand()))
                return CID.values()[i].getAimirOpCode(req);
        }
        return null;
    }
    
    public static String getAimirDescription(String[] req){
        for (int i = 0; i < CID.values().length; i++) {
            if (req[0].equals(CID.values()[i].getCommand())){
                if(CID.values()[i].getArgs()==null){
                    return CID.values()[i].getCommand();
                }else{
                    String[][] temp = CID.values()[i].getArgs();
                    for(int j=0;j<temp.length;j++){
                        if(temp[j][0].equals(req[1]))
                            return CID.values()[i].getCommand()+" " + temp[j][1];
                    }
                }
            }
        }
        return null;   
    }
    
    public static byte[] destuff(byte[] res){
        String decodeStr=Hex.decode(res);
        if(decodeStr.contains("1B")){
            decodeStr=decodeStr.replaceAll("1B7F", "80");
            decodeStr=decodeStr.replaceAll("1BBF", "40");
            decodeStr=decodeStr.replaceAll("1BF2", "0D");
            decodeStr=decodeStr.replaceAll("1BF9", "06");
            decodeStr=decodeStr.replaceAll("1BE4", "1B");
            return Hex.encode(decodeStr);
        }
        else return Hex.encode(decodeStr.substring(6));        
    }
    
    public static byte[] stuff(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        for (byte b : data) {
            switch (b) {
            case (byte)0x80 :
            case (byte)0x40 :
            case (byte)0x0D :
            case (byte)0x06 :
            case (byte)0x1B :
                out.write(new byte[]{0x1B});
                out.write(~b);
                break;
            default :
                out.write(b);
                break;
            }
        }
        return out.toByteArray();
    }
    
    public static Object[] getResult(byte[] res) throws Exception {
        _log.info("Before Stuffing Res : "+ Hex.decode(res));
        res = destuff(res);
        _log.info("after Stuffing Res : "+ Hex.decode(res));
        for (int i = 0; i < CID.values().length; i++) {
            if (res[0] == CID.values()[i].getCid()) {
                return CID.values()[i].getResponse(res);
            }
        }
        
        throw new Exception("Not found command[" + Hex.decode(res) + "]");
    }
    
    public static SUB_CID getSubCid(byte cid) {
        for (SUB_CID c : SUB_CID.values()) {
            if (c.getCid() == cid) {
                return c;
            }
        }
        
        return null;
    }
    
    public enum SUB_CID {
        GetLogTimePresent((byte)0x00, "GetLogTimePresent", 0, "Read a given no of loggings from a certain date till today", null),
        GetLogIDPresent((byte)0x02, "GetLogIDPresent", 0, "Read a given no of loggings from a certain ID till today", null),
        GetLogTimePast((byte)0x03, "GetLogTimePast", 0, "Read a given no of loggings from a certain date back in time", null);
        
        private byte cid = 0;
        private String command = null;
        private String descr = null;
        private int argsSize = 0;
        private String[][] args = null;
        
        SUB_CID(byte cid, String command, int argsSize, String descr, String[][] args) {
            this.cid = cid;
            this.command = command;
            this.argsSize = argsSize;
            this.descr = descr;
            this.args = args;
        }
        
        public byte getCid() {
            return this.cid;
        }
        
        public String getLogType(int logType) {
            switch (logType) {
            case 1 : return "Debiting logger 1";
            case 2 : return "Status event logger";
            case 3 : return "RTC event logger";
            case 5 : return "Disconnect event logger";
            case 6 : return "Load profile logger";
            case 8 : return "Debiting logger 2";
            case 10 : return "Load profile eventlogger";
            case 11 : return "Analysis logger";
            case 12 : return "Neutral fault logger";
            case 13 : return "Upload audit success";
            case 14 : return "Upload audit fail";
            case 16 : return "VoltageQualityLogger1";
            case 17 : return "VoltageQualityLogger2";
            default : return "Unknown";
            }
        }
    }
    
    public static CID getCid(byte cid) {
        for (CID c : CID.values()) {
            if (c.getCid() == cid) {
                return c;
            }
        }
        
        return null;
    }
    
    public enum CID {
        GetType((byte)0x01, "GetType", 0, "Indentification of meter type and SW revision", null),
        Reset((byte)0x07, "Reset", 0, "Performs reset of the meter.", null),
        SetClock((byte)0x09, "SetClock", 2, "Commando for adjusting of time.", 
                 new String[][] {{"", "Date(YYMMDD)"},
                                 {"", "Time(HHMMSS)"}}),
        GetRegister((byte)0x10, "GetRegister", 1, "Read-out of register", null),
        PutRegister((byte)0x11, "PutRegister", 0, "Setting of value in register.", null),
        ResetNoBackup((byte)0x85, "ResetNoBackup", 0, "Performs reset of the meter without backup.", null),
        SetDispYES((byte)0x86, "SetDispYES", 0, "YES is showed in the unit field.", null),
        DebStop((byte)0x88, "DebStop", 0, "Performs debiting stop.", null),
        SetCutOffState((byte)0x97, "SetCutOffState", 1, "Disconnect functionality", 
                       new String[][] {{"01", "Disconnect relays"},
                                       {"02", "Reconnect relays possible. Action only possible from state (Disconnect relays)"},
                                       {"03", "Reconnect relays. Action only possible from state (Reconnected relays possible)"}}),
        GetCutOffState((byte)0x98, "GetCutOffState", 0, "Disconnect functionality", null),
        ReadEventStatus((byte)0x9B, "ReadEventStatus", 0, "Read-out of event flag", null),
        ClrEventStatus((byte)0x9C, "ClrEventStatus", 0, "Resetting of event flag", null),
        GetLogTimePresent((byte)0xA0, "GetLogTimePresent", 0, "Logger read out", null),
        GetLogLastPresent((byte)0xA1, "GetLogLastPresent", 0, "Logger read out", null),
        GetLogIDPresent((byte)0xA2, "GetLogIDPresent", 0, "Logger read out", null),
        GetLogTimePast((byte)0xA3, "GetLogTimePast", 0, "Logger read out", null),
        SetVoltQualConfig((byte)0xA6, "SetVoltQualConfig", 0, "Voltage quality registration", null),
        GetVoltQualConfig((byte)0xA7, "GetVoltQualConfig", 0, "Voltage quality registration", null),
        LoggerReadOut((byte)0xB8, "LoggerReadOut", 0, "Used for reading out the different loggers", null),
        OK((byte)0x06, "OK", 1, "Valid result", null);
        
        private byte cid = 0;
        private String command = null;
        private String descr = null;
        private int argsSize = 0;
        private String[][] args = null;
        
        CID(byte cid, String command, int argsSize, String descr, String[][] args) {
            this.cid = cid;
            this.command = command;
            this.argsSize = argsSize;
            this.descr = descr;
            this.args = args;
        }

        public Object[] getResponse(byte[] res) throws Exception {
            switch (this) {
            case GetType :                                
                String[] str = new String[2];
                if (res[1] == 0x53) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K382B / K382C";
                    }
                    else if (res[2] == 0x01) {
                        str[0] = "Meter K382Jx2, 3, 4, 5, 6, 7, 8, 9";
                    }
                    else if (res[2] == 0x03) {
                        str[0] = "Meter K382Kx2, 3, 5, 7";
                    }
                }
                else if (res[1] == 0x55) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K382D / K382E";
                    }
                    else if (res[2] == 0x01) {
                        str[0] = "Meter K382JxB, C, D, F, G";
                    }
                    else if (res[2] == 0x02) {
                        str[0] = "Meter K382J NTA";
                    }
                    else if (res[2] == 0x03) {
                        str[0] = "Meter K382KxB C, E, G";
                    }
                }
                else if (res[1] == 0x56) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K162B / K162C";
                    }
                    else if (res[2] == 0x01) {
                        str[0] = "Meter K162Jx3, 6, 7";
                    }
                    else if (res[2] == 0x03) {
                        str[0] = "Meter K162Kx3, 7";
                    }
                }
                else if (res[1] == 0x57) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K162D/ K162E";
                    }
                    else if (res[2] == 0x01) {
                        str[0] = "Meter K162JxC, F, G";
                    }
                    else if (res[2] == 0x02) {
                        str[0] = "Meter K162J NTA";
                    }
                    else if (res[2] == 0x03) {
                        str[0] = "Meter K162KxC, G";
                    }
                }
                else if (res[1] == 0x58) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K282B / K282C";
                    }
                    else if (res[2] == 0x01) {
                        str[0] = "Meter K282Jx2, 3, 4, 5, 6, 7, 8, 9";
                    }
                    else if (res[2] == 0x03) {
                        str[0] = "Meter K282Kx2, 3, 5, 7";
                    }
                }
                else if (res[1] == 0x59) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K282D / K282E";
                    }
                    else if (res[2] == 0x01) {
                        str[0] = "Meter K282JxB, C, D, E, F, G";
                    }
                    else if (res[2] == 0x03) {
                        str[0] = "Meter K282KxB, C, E, G";
                    }
                }
                else if (res[1] == 0x60) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter 351B";
                    }
                }
                else if (res[1] == 0x61) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter 351B Aron";
                    }
                }
                else if (res[1] == 0x62) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K382Lx2, 3, 4, 5, 6, 7, 8, 9";
                    }
                }
                else if (res[1] == 0x63) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K382LxB, C, D, E, F, G";
                    }
                }
                else if (res[1] == 0x64) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K162Lx3, 6, 7";
                    }
                }
                else if (res[1] == 0x65) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K162LxC, F, G";
                    }
                }
                else if (res[1] == 0x66) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K282Lx2, 3, 4, 5, 6, 7, 8, 9";
                    }
                }
                else if (res[1] == 0x67) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K282LxB, C, D, E, F, G";
                    }
                }
                else if (res[1] == 0x68) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K382Mx2, 3, 4, 5, 6, 7, 8, 9";
                    }
                }
                else if (res[1] == 0x69) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K382MxB, C, D, E, F, G";
                    }
                }
                else if (res[1] == 0x70) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K162Mx3, 6, 7";
                    }
                }
                else if (res[1] == 0x71) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K162MxC, F, G";
                    }
                }
                else if (res[1] == 0x72) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K282Mx2, 3, 4, 5, 6, 7, 8, 9";
                    }
                }
                else if (res[1] == 0x73) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter K282MxB, C, D, E, F, G";
                    }
                }
                else if (res[1] == 0x75) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter 351C";
                    }
                }
                else if (res[1] == 0x76) {
                    if (res[2] == 0x00) {
                        str[0] = "Meter 251C";
                    }
                }
                else if (res[1] == 0x5A) {
                    if (res[2] == 0x00) {
                        str[0] = "OMNIPOWER (Electricity meter)";
                    }
                }
                
                str[1] = " rev.";
                if (res[3] == 0x01) {
                    str[1] += "A";
                }
                else if (res[3] == 0x02) {
                    str[1] += "B";
                }
                else if (res[3] == 0x03) {
                    str[1] += "C";
                }
                else if (res[3] == 0x04) {
                    str[1] += "D";
                }
                else if (res[3] == 0x05) {
                    str[1] += "E";
                }
                else if (res[3] == 0x06) {
                    str[1] += "F";
                }
                else if (res[3] == 0x07) {
                    str[1] += "G";
                }
                else if (res[3] == 0x08) {
                    str[1] += "H";
                }
                else if (res[3] == 0x09) {
                    str[1] += "I";
                }
                else if (res[3] == 0x0A) {
                    str[1] += "J";
                }
                else if (res[3] == 0x0B) {
                    str[1] += "K";
                }
                else if (res[3] == 0x0C) {
                    str[1] += "L";
                }
                else if (res[3] == 0x0D) {
                    str[1] += "M";
                }
                else if (res[3] == 0x0E) {
                    str[1] += "N";
                }
                else if (res[3] == 0x0F) {
                    str[1] += "O";
                }
                else if (res[3] == 0x10) {
                    str[1] += "P";
                }
                else if (res[3] == 0x11) {
                    str[1] += "Q";
                }
                else if (res[3] == 0x12) {
                    str[1] += "R";
                }
                else if (res[3] == 0x13) {
                    str[1] += "S";
                }
                else if (res[3] == 0x14) {
                    str[1] += "T";
                }
                else if (res[3] == 0x15) {
                    str[1] += "U";
                }
                else if (res[3] == 0x16) {
                    str[1] += "V";
                }
                else if (res[3] == 0x17) {
                    str[1] += "W";
                }
                else if (res[3] == 0x18) {
                    str[1] += "X";
                }
                else if (res[3] == 0x19) {
                    str[1] += "Y";
                }
                else if (res[3] == 0x1A) {
                    str[1] += "Z";
                }
                else if (res[3] == 0x1B) {
                    str[1] += "AA";
                }
                
                str[1] += DataUtil.getIntToByte(res[4]);                
                return str;
            case Reset :
                return new String[] {command};
            case SetClock :
                if (res[2] == 0x06)
                    return new String[] {command, "Success"};
                else
                    return new String[] {command, "Fail"};
            case GetRegister :
                String rid="";
                String unit="";
                int cntRegVal=0;
                byte byteSiEx;
                double siEx=0;
                double regVal=0;
                for(int bitIdx = 0; bitIdx < res.length;){
                    _log.debug("res.length: "+(res.length)+" bitIdx: "+bitIdx);
                    rid = getRid(DataUtil.getIntTo2Byte(res[bitIdx++], res[bitIdx++]));//getRid
                    unit = getUnit(DataUtil.getIntToByte(res[bitIdx++]));//getUnit
                    cntRegVal = DataUtil.getIntToByte(res[bitIdx++]);//getValueCount
                    _log.debug("cntRegVal: "+cntRegVal);
                    
                    byteSiEx = res[bitIdx++];
                    siEx=makeSiEx(byteSiEx);//-1^SI*-1^SE*exponent                
                    _log.debug("Sign Exp: "+siEx);
                    
                    byte temp[]=new byte[cntRegVal];            
                    System.arraycopy(res, bitIdx, temp, 0, temp.length);
                    bitIdx += temp.length;
                    _log.debug("val: "+DataUtil.getIntToBytes(temp));
                    
                    regVal = DataUtil.getIntToBytes(temp)*siEx;
                    _log.debug("====Rid :"+rid+" Value: "+regVal+" "+unit+" bitIdx:"+bitIdx);
                    
                }
                return new Object[] {command, regVal};
            case DebStop :
                return new String[] {command};
            case SetCutOffState :
            case GetCutOffState :
            	_log.debug("RES2:"+res[2]);
                str = new String[6];
                if (res[1] == 0x00)
                    str[0] = "Not supported relay";
                else if (res[1] == 0x01)
                    str[0] = "Relays disconnected by command";
                else if (res[1] == 0x04)
                    str[0] = "Relays connected";
                else if (res[1] == 0x05)
                    str[0] = "Pre cutoff warning";
                else if (res[1] == 0x06)
                    str[0] = "Cutoff";
                else if (res[1] == 0x07)
                    str[0] = "Cutoff Prepyament";
                else if (res[1] == 0x08)
                    str[0] = "Relays released for reconnection";
                else if (res[1] == 0x09)
                    str[0] = "Cutoff Prepayment, Lowmax expired";    
                else if (res[1] == 0x0B)
                    str[0] = "Relays disconnected by push button";
                
                str[1] = "- Voltage L1 ";
                if ((res[2] & 0x01) != 0)
                    str[1] += ": Voltage";
                else
                    str[1] += ": No voltage";
                
                str[2] = "- Voltage L2 ";
                if ((res[2] & 0x02) != 0)
                    str[2] += ": Voltage";
                else
                    str[2] += ": No voltage";
                
                str[3] = "- Voltage L3 ";
                if ((res[2] & 0x04) != 0)
                    str[3] += ": Voltage";
                else
                    str[3] += ": No voltage";
                
                str[4] = "- Validation ";
                if ((res[2] & 0x08) != 0)
                    str[4] += ": L1~L3 Voltage is valid";
                else
                    str[4] += ": L1~L3 Voltage not valid";
                
                str[5] = "- Interval relay control status ";
                if ((res[2] & 0x10) != 0)
                    str[5] += ": No Error";
                else
                    str[5] += ": Error";                
            	return str;
            case ReadEventStatus :
                return new String[] {command};
            case ClrEventStatus :
                return new String[] {command};
            case GetLogTimePresent :
                return new String[] {command};
            case GetLogLastPresent :
                return new String[] {command};
            case GetLogIDPresent :
                return new String[] {command};
            case GetLogTimePast :
                return new String[] {command};
            case SetVoltQualConfig :
                return new String[] {command};
            case GetVoltQualConfig :
                return new String[] {command};
            case OK :
                return new String[] {command};
            }
            return null;
        }
        public String getAimirOpCode(String[] req) {
            switch(this) {
            case GetType :
                return "200.3.1";
            case GetCutOffState :
                return "200.3.2";
            case SetCutOffState :
                if(req[1].equals(this.args[0][0]))
                    return "200.3.3";
                if(req[1].equals(this.args[1][0]))
                    return "200.3.4";
                if(req[1].equals(this.args[2][0]))
                    return "200.3.5";                
            }
            return null;
        }
        public byte[][] getRequest(String[] req) throws Exception {
            switch(this) {
            case GetType :
                return new byte[][] {{cid}};
            case Reset :
                return new byte[][] {{cid}};
            case SetClock :
                /*
                ByteBuffer buf = ByteBuffer.allocate(9); 
                buf.put(cid);
                buf.put(DataUtil.get4ByteToInt(Integer.parseInt(req[1])));
                buf.put(DataUtil.get4ByteToInt(Integer.parseInt(req[2])));
                return buf.array();
                */
                byte[] bx = new byte[8];
                
                if (req[1] != null && !"".equals(req[1]) && req[2] != null && !"".equals(req[2])) {
                    System.arraycopy(DataUtil.get4ByteToInt(Integer.parseInt(req[1])),
                            0, bx, 0, 4);
                    System.arraycopy(DataUtil.get4ByteToInt(Integer.parseInt(req[2])),
                            0, bx, 4, 4);
                }
                else {
                    String yyyyMMddHHmmss = DateTimeUtil.getDateString(new Date());
                    System.arraycopy(DataUtil.get4ByteToInt(Integer.parseInt(yyyyMMddHHmmss.substring(2,8))),
                            0, bx, 0, 4);
                    System.arraycopy(DataUtil.get4ByteToInt(Integer.parseInt(yyyyMMddHHmmss.substring(8))),
                            0, bx, 4, 4);
                }
                
                return new byte[][] {{cid}, bx};
                
            case GetRegister :
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                out.write(DataUtil.getByteToInt(req.length-1));
                for (int i = 1; i < req.length; i++) {
                    out.write(DataUtil.get2ByteToInt(Integer.parseInt(req[i])));
                }
                out.close();
                return new byte[][] {{cid}, out.toByteArray()};
            case PutRegister :
                return null;
            case ResetNoBackup :
                return null;
            case SetDispYES :
                return null;
            case DebStop :
                return null;
            case SetCutOffState :
                return new byte[][] {{cid}, {DataUtil.getByteToInt(Integer.parseInt(req[1]))}};
            case GetCutOffState :
                return new byte[][] {{cid}};
            case ReadEventStatus :
                return null;
            case ClrEventStatus :
                return null;
            case GetLogTimePresent :
                return null;
            case GetLogLastPresent :
                return null;
            case GetLogIDPresent :
                return null;
            case GetLogTimePast :
                return null;
            case SetVoltQualConfig :
                return null;
            case GetVoltQualConfig :
                return null;
            }
            return null;
        }
        
        public String[] getRequestValue(String[] req) throws Exception {
            switch(this) {
            case GetType :
                return null;
            case Reset :
                return null;
            case SetClock :
                return null;
            case GetRegister :                
                return null;
            case PutRegister :
                return null;
            case ResetNoBackup :
                return null;
            case SetDispYES :
                return null;
            case DebStop :
                return null;
            case SetCutOffState :
                return new String[] {"cmSetCutOff", "0"+Integer.parseInt(req[1])};                
            case GetCutOffState :
                return new String[] {"cmGetCutOff"};
            case ReadEventStatus :
                return null;
            case ClrEventStatus :
                return null;
            case GetLogTimePresent :
                return null;
            case GetLogLastPresent :
                return null;
            case GetLogIDPresent :
                return null;
            case GetLogTimePast :
                return null;
            case SetVoltQualConfig :
                return null;
            case GetVoltQualConfig :
                return null;
            }
            return null;
        }
        
        public String getRid(int rid){
            switch(rid){
            case 1:
                return "Active energy A14";
            case 2:
                return "Active energy A23";
            case 1031:
                return "Active energy A1234";
            case 3:
                return "Reactive energy R12";
            case 4:
                return "Reactive energy R34";
            case 5:
                return "Reactive energy R1";
            case 1275 :
                return "Reactive energy R2";
            case 1276 :
                return "Reactive energy R3";
            case 6:
                return "Reactive energy R4";
            case 1277 :
                return "Apparent energy E14";
            case 1278 :
                return "Apparent energy E23";
            case 13 :
                return "Active energy A14";
            case 14 :
                return "Active energy A23";
            case 15 :
                return "Reactive energy R12";
            case 16 :
                return "Reactive energy R34";
            case 19:
                return "Active energy A14 Tariff 1";
            case 23:
                return "Active energy A14 Tariff 2";
            case 27:
                return "Active energy A14 Tariff 3";
            case 31:
                return "Active energy A14 Tariff 4";
            case 1059:
                return "Active energy A14 Tariff 5";
            case 1060:
                return "Active energy A14 Tariff 6";
            case 1061:
                return "Active energy A14 Tariff 7";
            case 1062:
                return "Active energy A14 Tariff 8";
            case 20:
                return "Active energy A23 Tariff 1";
            case 24:
                return "Active energy A23 Tariff 2";
            case 28:
                return "Active energy A23 Tariff 3";
            case 32:
                return "Active energy A23 Tariff 4";
            case 1063:
                return "Active energy A23 Tariff 5";
            case 1064:
                return "Active energy A23 Tariff 6";
            case 1065:
                return "Active energy A23 Tariff 7";
            case 1066:
                return "Active energy A23 Tariff 8";
            case 21:
                return "Reactive energy R12 Tariff 1";
            case 25:
                return "Reactive energy R12 Tariff 2";
            case 29:
                return "Reactive energy R12 Tariff 3";
            case 33:
                return "Reactive energy R12 Tariff 4";
            case 1067:
                return "Reactive energy R12 Tariff 5";
            case 1068:
                return "Reactive energy R12 Tariff 6";
            case 1069:
                return "Reactive energy R12 Tariff 7";
            case 1070:
                return "Reactive energy R12 Tariff 8";
            case 22:
                return "Reactive energy R34 Tariff 1";
            case 26:
                return "Reactive energy R34 Tariff 2";
            case 30:
                return "Reactive energy R34 Tariff 3";
            case 34:
                return "Reactive energy R34 Tariff 4";
            case 1071:
                return "Reactive energy R34 Tariff 5";
            case 1072:
                return "Reactive energy R34 Tariff 6";
            case 1073:
                return "Reactive energy R34 Tariff 7";
            case 1074:
                return "Reactive energy R34 Tariff 8";
            case 1279 :
                return "Apparent energy E14 Tariff 1";
            case 1280 :
                return "Apparent energy E14 Tariff 2";
            case 1281 :
                return "Apparent energy E14 Tariff 3";
            case 1282 :
                return "Apparent energy E14 Tariff 4";
            case 1283 :
                return "Apparent energy E14 Tariff 5";
            case 1284 :
                return "Apparent energy E14 Tariff 6";
            case 1285 :
                return "Apparent energy E14 Tariff 7";
            case 1286 :
                return "Apparent energy E14 Tariff 8";
            case 1287 :
                return "Apparent energy E23 Tariff 1";
            case 1288 :
                return "Apparent energy E23 Tariff 2";
            case 1289 :
                return "Apparent energy E23 Tariff 3";
            case 1290 :
                return "Apparent energy E23 Tariff 4";
            case 1291 :
                return "Apparent energy E23 Tariff 5";
            case 1292 :
                return "Apparent energy E23 Tariff 6";
            case 1293 :
                return "Apparent energy E23 Tariff 7";
            case 1294 :
                return "Apparent energy E23 Tariff 8";
            case 1299 :
                return "Active positive energy A14 L1";
            case 1300 :
                return "Active positive energy A14 L2";
            case 1301 :
                return "Active positive energy A14 L3";
            case 1302 :
                return "Active positive energy A23 L1";
            case 1303 :
                return "Active positive energy A23 L2";
            case 1304 :
                return "Active positive energy A23 L3";
            case 17:
                return "Resetable counter A14";
            case 18:
                return "Resetable counter A23";
            case 1295 :
                return "Resetable counter R12";
            case 1296 :
                return "Resetable counter R34";
            case 1297 :
                return "Resetable counter E14";
            case 1298 :
                return "Resetable counter E23";
            case 1023 :
                return "Actual power P14";
            case 1024 :
                return "Actual power P23";
            case 1025 :
                return "Actual power Q12";
            case 1026 :
                return "Actual power Q34";
            case 1305 :
                return "Actual power S14";
            case 1306 :
                return "Actual power S23";
            case 1307 :
                return "Actual power S1234";
            case 35 :
                return "Average power P14";
            case 36 :
                return "Average power P23";
            case 37 :
                return "Average power Q12";
            case 38 :
                return "Average power Q34";
            case 1308 :
                return "Average power S14";
            case 1309 :
                return "Average power S23";
            case 39:
                return "Max power P14";
            case 40:
                return "Max power P23";
            case 41:
                return "Max power Q12";
            case 42:
                return "Max power Q34";
            case 1326 :
                return "Max power S14";
            case 1310 :
                return "Max power S23";
            case 43:
                return "Accumulated max power P14";
            case 44:
                return "Accumulated max power P23";
            case 45:
                return "Accumulated max power Q12";
            case 46:
                return "Accumulated max power Q34";
            case 47:
                return "Number of debiting periods";
            case 1049:
                return "Max power P14 RTC";
            case 1033 :
                return "Max power P14 Tariff 1";
            case 1050 :
                return "Max power P14 Tariff 1 RTC";
            case 1036 :
                return "Max power P14 Tariff 2";
            case 1051 :
                return "Max power P14 Tariff 2 RTC";
            case 1312 :
                return "Max power P14 Tariff 3";
            case 1313 :
                return "Max power P14 Tariff 3 RTC";
            case 1314 :
                return "Max power P14 Tariff 4";
            case 1315 :
                return "Max power P14 Tariff 4 RTC";
            case 1316 :
                return "Max power P14 Tariff 5";
            case 1317 :
                return "Max power P14 Tariff 5 RTC";
            case 1318 :
                return "Max power P14 Tariff 6";
            case 1319 :
                return "Max power P14 Tariff 6 RTC";
            case 1320 :
                return "Max power P14 Tariff 7";
            case 1321 :
                return "Max power P14 Tariff 7 RTC";
            case 1322 :
                return "Max power P14 Tariff 8";
            case 1323 :
                return "Max power P14 Tariff 8 RTC";
            case 1127 : 
                return "Max power Q14 RTC";
            case 1130 :
                return "Max power Q23 Tariff 1";
            case 1131 :
                return "Max power Q23 Tariff 1 RTC";
            case 1134 :
                return "Max power Q23 Tariff 2";
            case 1135 :
                return "Max power Q23 Tariff 2 RTC";
            case 1327 :
                return "Max power S14 RTC";
            case 1311 :
                return "Max power S23 RTC";
            case 1328 :
                return "Max power S14 Tariff 1";
            case 1329 :
                return "Max power S14 Tariff 1 RTC";
            case 1330 :
                return "Max power S14 Tariff 2";
            case 1331 :
                return "Max power S14 Tariff 2 RTC";
            case 1332 :
                return "Max power S14 Tariff 3";
            case 1333 :
                return "Max power S14 Tariff 3 RTC";
            case 1334 :
                return "Max power S14 Tariff 4";
            case 1335 :
                return "Max power S14 Tariff 4 RTC";
            case 1336 :
                return "Max power S14 Tariff 5";
            case 1337 :
                return "Max power S14 Tariff 5 RTC";
            case 1338 :
                return "Max power S14 Tariff 6";
            case 1339 :
                return "Max power S14 Tariff 6 RTC";
            case 1340 :
                return "Max power S14 Tariff 7";
            case 1341 :
                return "Max power S14 Tariff 7 RTC";
            case 1342 :
                return "Max power S14 Tariff 8";
            case 1343 :
                return "Max power S14 Tariff 8 RTC";
            case 58:
                return "Pulse input";
            case 1004:
                return "Hour counter";
            case 1392 :
                return "Hour counter Tariff 1";
            case 1393 :
                return "Hour counter Tariff 2";
            case 1394 :
                return "Hour counter Tariff 3";
            case 1395 :
                return "Hour counter Tariff 4";
            case 1396 :
                return "Hour counter Tariff 5";
            case 1397 :
                return "Hour counter Tariff 6";
            case 1398 :
                return "Hour counter Tariff 7";
            case 1399 :
                return "Hour counter Tariff 8";
            case 1047 :
                return "RTC";
            case 1048 :
                return "RTC 2";
            case 1045 :
                return "RTC status";
            case 57:
                return "Special Data 1";
            case 1021:
                return "Special Data 2";
            case 1010:
                return "Total meter number";
            case 51:
                return "Meter number 1";
            case 52:
                return "Meter number 2";
            case 53:
                return "Meter number 3";
            case 50:
                return "Meter status";
            case 1001:
                return "Serial number";
            case 1058:
                return "Type number";
            case 1126 :
                return "Complete type number";
            case 1268 :
                return "CommAddrForMBusAndK1";
            case 1271 :
                return "CommAddrKMP";
            case 1470 :
                return "PhaseSequenceDetaction";
            case 2010:
                return "Active tariff";
            case 1032 :
                return "Operation mode";
            case 1039 :
                return "Power threshold value";
            case 6008 :
                return "Power threshold value tariff 1";
            case 6009 :
                return "Power threshold value tariff 2";
            case 6010 :
                return "Power threshold value tariff 3";
            case 6011 :
                return "Power threshold value tariff 4";
            case 6012 :
                return "Power threshold value tariff 5";
            case 6013 :
                return "Power threshold value tariff 6";
            case 6014 :
                return "Power threshold value tariff 7";
            case 6015 :
                return "Power threshold value tariff 8";
            case 1040 :
                return "Power threshold counter";
            case 1400 :
                return "Power thrshold counter tariff 1";
            case 1401 :
                return "Power thrshold counter tariff 2";
            case 1402 :
                return "Power thrshold counter tariff 3";
            case 1403 :
                return "Power thrshold counter tariff 4";
            case 1404 :
                return "Power thrshold counter tariff 5";
            case 1405 :
                return "Power thrshold counter tariff 6";
            case 1406 :
                return "Power thrshold counter tariff 7";
            case 1407 :
                return "Power thrshold counter tariff 8";
            case 1046:
                return "VCOPCO Status";
            case 1054:
                return "Voltage L1";
            case 1055:
                return "Voltage L2";
            case 1056:
                return "Voltage L3";
            case 1076:
                return "Current L1";
            case 1077:
                return "Current L2";
            case 1078:
                return "Current L3";
            case 1080:
                return "Actual power P14 L1";
            case 1081:
                return "Actual power P14 L2";
            case 1082:
                return "Actual power P14 L3";
            case 1344 :
                return "Actual power P23 L1";
            case 1345 :
                return "Actual power P23 L2";
            case 1346 :
                return "Actual power P23 L3";
            case 1347 :
                return "Actual power Q12 L1";
            case 1348 :
                return "Actual power Q12 L2";
            case 1349 :
                return "Actual power Q12 L3";
            case 1350 :
                return "Actual power Q34 L1";
            case 1351 :
                return "Actual power Q34 L2";
            case 1352 :
                return "Actual power Q34 L3";
            case 1353 :
                return "Actual power S14 L1";
            case 1354 :
                return "Actual power S14 L2";
            case 1355 :
                return "Actual power S14 L3";
            case 1356 :
                return "Actual power S23 L1";
            case 1357 :
                return "Actual power S23 L2";
            case 1358 :
                return "Actual power S23 L3";
            case 1170 :
                return "Power factor L1";
            case 1171 :
                return "Power factor L2";
            case 1172 :
                return "Power factor L3";
            case 1173 :
                return "Total power factor";
            case 1215 :
                return "Avg Voltage L1";
            case 1216 :
                return "Avg Voltage L2";
            case 1217 :
                return "Avg Voltage L3";
            case 1002:
                return "Clock";
            case 1003:
                return "Date";
            case 54:
                return "Configurations number 1";
            case 55:
                return "Configurations number 2";
            case 56:
                return "Configurations number 3";
            case 1029:
                return "Configurations number 4";
            case 1075:
                return "Configurations number 5";
            case 1385 :
                return "Average Voltage L1 - (10min)";
            case 1386 :
                return "Average Voltage L2 - (10min)";
            case 1387 :
                return "Average Voltage L3 - (10min)";
            case 1218 :
                return "Avg Current L1";
            case 1219 :
                return "Avg Current L2";
            case 1220 :
                return "Avg Current L3";
            case 1359 :
                return "Average power P14 L1";
            case 1360 :
                return "Average power P14 L2";
            case 1361 :
                return "Average power P14 L3";
            case 1362 :
                return "Average power P23 L1";
            case 1363 :
                return "Average power P23 L2";
            case 1364 :
                return "Average power P23 L3";
            case 1365 :
                return "Average power Q12 L1";
            case 1366 :
                return "Average power Q12 L2";
            case 1367 :
                return "Average power Q12 L3";
            case 1368 :
                return "Average power Q34 L1";
            case 1369 :
                return "Average power Q34 L2";
            case 1370 :
                return "Average power Q34 L3";
            case 1371 :
                return "Average power S14 L1";
            case 1372 :
                return "Average power S14 L2";
            case 1373 :
                return "Average power S14 L3";
            case 1374 :
                return "Average power S23 L1";
            case 1375 :
                return "Average power S23 L2";
            case 1376 :
                return "Average power S23 L3";
            case 1377 :
                return "Average power factor L1";
            case 1378 :
                return "Average power factor L2";
            case 1379 :
                return "Average power factor L3";
            case 1380 :
                return "Average power factor total";
            case 1434 :
                return "THDu - L1 - instantaneous";
            case 1435 :
                return "THDu - L1 - mean (10min)";
            case 1436 :
                return "THDu - L1 - mean (analysis)";
            case 1437 :
                return "THDu - L2 - instantaneous";
            case 1438 :
                return "THDu - L2 - mean (10min)";
            case 1439 :
                return "THDu - L2 - mean (analysis)";
            case 1440 :
                return "THDu - L3 - instantaneous";
            case 1441 :
                return "THDu - L3 - mean (10min)";
            case 1442 :
                return "THDu - L3 - mean (analysis)";
            case 1443 :
                return "THDi - L1 - instantaneous";
            case 1444 :
                return "THDi - L1 - mean (10min)";
            case 1445 :
                return "THDi - L1 - mean (analysis)";
            case 1446 :
                return "THDi - L2 - instantaneous";
            case 1447 :
                return "THDi - L2 - mean (10min)";
            case 1448 :
                return "THDi - L2 - mean (analysis)";
            case 1449 :
                return "THDi - L3 - instantaneous";
            case 1450 :
                return "THDi - L3 - mean (10min)";
            case 1451 :
                return "THDi - L3 - mean (analysis)";
            case 1388 :
                return "Supply voltage unbalance";
            case 1389 :
                return "Supply voltage unbalance - mean";
            case 1390 :
                return "Frequency";
            case 1391 :
                return "Frequency - mean";
            case 1083:
                return "ROM checksum";
            case 1471 :
                return "Power factor angle L1";
            case 1472 :
                return "Power factor angle L2";
            case 1473 :
                return "Power factor angle L3";
            case 1474 :
                return "Power factor angle total";
            case 1084 :
                return "Voltage extremity";
            case 1085 :
                return "Voltage event";
            case 1087:
                return "Connection status";
            case 1088:
                return "Connection feedback";
            case 1102 :
                return "Module port I/O configuration";
            case 1108 :
                return "I shortcircuit";
            case 1109 :
                return "K1";
            case 1110 :
                return "K2";
            case 1111 :
                return "K3";
            case 1112 :
                return "T1";
            case 1113 :
                return "T2";
            case 1114 :
                return "T3";
            case 1115 :
                return "Tconnectwait";
            case 1116 :
                return "Tprepayment low";
            case 1117 :
                return "Switching on";
            case 1118 :
                return "Cutoff basis";
            case 1119 :
                return "ThLow";
            case 1120 :
                return "ThHigh";
            case 1121 :
                return "A14prepayment";
            case 1408 :
                return "A14pp credit";
            case 6041 :
                return "T PP exception start";
            case 6042 :
                return "T PP exception stop";
            case 1411 :
                return "LastPayment value";
            case 1412 :
                return "LastPayment RTC";
            case 1413 :
                return "TRemaining";
            case 6043 :
                return "PP alarmLimit";
            case 1122 : 
                return "ThDisconnect";
            case 1175 :
                return "Debitinglogger2 loginterval";
            case 199 :
                return "Load profile loginterval";
            case 1190 :
                return "P14 Maximum";
            case 1191 :
                return "P14 Minimum";
            case 1180 :
                return "Password";
            case 222 :
                return "ConfigChangedEventCount";
            case 231 :
                return "IncrementConfigChangedEventCount";
            case 1189 :
                return "Config code 000";
            case 1192 :
                return "LegalLoggerSize";
            case 1193 :
                return "LegalLoggerDepth";
            case 1195 :
                return "AnalysisLoggerInterval";
            case 1194 :
                return "AnalysisLoggerDepth";
            case 1198 :
                return "P14 Maximum RTC";
            case 1201 :
                return "P14 Minumum RTC";
            case 1222 :
                return "LoadProfile Event status";
            case 1210 :
                return "LoadProfileRegisterSetup";
            case 1211 :
                return "LoadProfileLoggerSetup";
            case 1212 :
                return "VQLogULow";
            case 1213 :
                return "VQLogUHigh";
            case 1214 :
                return "VQLogTeventMinDuration";
            case 1226 :
                return "Load1Active";
            case 1227 :
                return "Load1Mode";
            case 1228 :
                return "Load1ConvertTariffToPos";
            case 1244 :
                return "Load1VariableDelayCnt";
            case 1229 :
                return "Load2Active";
            case 1230 :
                return "Load2Mode";
            case 1231 :
                return "Load2ConvertTariffToPos";
            case 1232 :
                return "Load2VariableDelayCnt";
            case 1233 :
                return "WorkingDaysSetup";
            case 1234 :
                return "PulseInputLevel";
            case 1235 :
                return "EventstatusA";
            case 1236 :
                return "EventMaskA";
            case 1237 :
                return "EventstatusB";
            case 1238 :
                return "EventMaskB PosEdge";
            case 1239 :
                return "EventMaskB negEdge";
            case 1240 :
                return "DayLightSavingConfig";
            case 1241 :
                return "DataQualitymask";
            case 1242 :
                return "NeutralFaultLogEvent";
            case 1246 :
                return "NeutralFaultVNth";
            case 1247 :
                return "NeutalFaultVLTh";
            case 1248 :
                return "NeutralFaultTime";
            case 1249 :
                return "NeutralVoltage";
            case 1243 :
                return "ModuleIndentity";
            case 1250 :
                return "Displaytest";
            case 1251 :
                return "DisplayUserForcedCall";
            case 1252 :
                return "DisplayDisconnect";
            case 1253 :
                return "DisplayDebitingLogger";
            case 1254 :
                return "DisplayLoadProfileLogger";
            case 1258 :
                return "PowerfaultTimeThreshold";
            case 1259 :
                return "DisplayOptionsConfig";
            case 1460 :
                return "VoltageQuality1Phase";
            case 1461 :
                return "VoltageQuality1Event";
            case 1462 :
                return "VoltageQuality1MeanValue";
            case 1463 :
                return "VoltageQuality1MaxValue";
            case 1464 :
                return "VoltageQuality1MinValue";
            case 1489 :
                return "VQCounterTHDIL1";
            case 1490 :
                return "VQCounterTHDIL2";
            case 1491 :
                return "VQCounterTHDIL3";
            case 1414 :
                return "Counter 1";
            case 1415 :
                return "Counter 2";
            case 1416 :
                return "Counter 3";
            case 1417 :
                return "Counter 4";
            case 1418 :
                return "Counter 5";
            case 1419 :
                return "Counter 6";
            case 1420 :
                return "Counter 7";
            case 1421 :
                return "Counter 8";
            case 1422 :
                return "CounterVoltageVariationLow1";
            case 1423 :
                return "CounterVoltageVariationLow2";
            case 1424 :
                return "CounterVoltageVariationHigh";
            case 1425 :
                return "CounterRapidVoltageChanges";
            case 1426 :
                return "CounterVoltageUnbalance";
            case 1427 :
                return "CounterInteruptsLong";
            case 1428 :
                return "CounterInteruptsShort";
            case 1429 :
                return "CountTHDL1";
            case 1430 :
                return "CountTHDL2";
            case 1431 :
                return "CountTHDL3";
            case 1432 :
                return "CountDips";
            case 1433 :
                return "CountSwells";
            case 2011 :
                return "TariffMode";
            case 1261 :
                return "EnergyA14IntervalValueDay";
            case 1262 :
                return "EnergyA14IntervalValueWeek";
            case 1263 :
                return "EnergyA14IntervalValueMonth";
            case 1264 :
                return "EnergyA14IntervalValueYear";
            case 6162 :
                return "IEC1107UserDataSelectPrimaryModule";
            case 6163 :
                return "IEC1107UserDataSelectSecondaryModule";
            case 6164 :
                return "IEC1107UserDataSelectIRModule";
            case 6165 :
                return "IEC1107ModeD0TimeOutPrimaryModule";
            case 6166 :
                return "IEC1107ModeD0TimeOutSecondaryModule";
            case 6167 :
                return "IEC1107ModeD0TimeOutIRModule";
            case 6168 :
                return "IEC1107ModeD0BaudRatePrimaryModule";
            case 6169 :
                return "IEC1107ModeD0BaudRateSecondaryModule";
            case 6170 :
                return "IEC1107ModeD0BaudRateIRModule";
            case 6171 :
                return "IEC1107ComModeSelectPrimaryModule";
            case 6172 :
                return "IEC1107ComModeSelectSecondaryModule";
            case 6173 :
                return "IEC1107ComModeSelectIRModule";
            case 6174 :
                return "IEC1107ProtocolTimerTa";
            case 6175 :
                return "IEC1107ProtocolTimerTr";
            case 6176 :
                return "IEC1107ProtocolTimerTt";
            case 6177 :
                return "IEC1107ProtocolTimerTi";
            case 1454 :
                return "cLogAttribStatus";
            case 1455 :
                return "cLogAttribDataQuality";
            case 1456 :
                return "cLogAttribLogsInUse";
            case 1457 :
                return "cLogAttribLogDepth";
            case 1458 :
                return "cLogAttribLogId";
            case 1459 :
                return "cLogAttribNoBFlash";
            case 6200 :
                return "DataQualityOverVoltageThreshold";
            case 6201 :
                return "DataQulaityPowerOutageTimeThreshold";
            case 6193 :
                return "DataQualityPowerOutageTimeThreshold";
            case 6067 :
                return "DataQualityPowerOutageTimeThreshold";
            case 6068 :
                return "EnableWelmecLogFill";
            case 6045 :
                return "Debit1StopTime";
            case 6002 :
                return "Format_KMP_Energy";
            case 6031 :
                return "SoftwareRevision";
            case 6152 :
                return "SoftwareUploadInformationPart";
            case 6154 :
                return "SoftwareVariantRevision_Old";
            case 6196 :
                return "OriginatorID";
            case 6156 :
                return "StartUploadInterface";
                
            case 1224 :
                return "Logger status 2";
            // Additional registers for 351B
            case 7 :
                return "Seconday active energy A14";
            case 8 :
                return "Secondary active energy A23";
            case 9 : 
                return "Secondary reactive energy R12";
            case 10 :
                return "Secondary reactive energy R34";
            case 11 :
                return "Secondary reactive energy R1";
            case 12 :
                return "Secondary reacitve energy R4";
            case 1138 :
                return "Secondary active energy A14 Tariff 1";
            case 1139 :
                return "Secondary active energy A14 Tariff 2";
            case 1140 :
                return "Secondary active energy A14 Tariff 3";
            case 1141 :
                return "Secondary active energy A14 Tariff 4";
            case 1142 :
                return "Secondary active energy A14 Tariff 5";
            case 1143 :
                return "Secondary active energy A14 Tariff 6";
            case 1144 :
                return "Secondary active energy A14 Tariff 7";
            case 1145 :
                return "Secondary active energy A14 Tariff 8";
            case 1146 :
                return "Secondary active energy A23 Tariff 1";
            case 1147 :
                return "Secondary active energy A23 Tariff 2";
            case 1148 :
                return "Secondary active energy A23 Tariff 3";
            case 1149 :
                return "Secondary active energy A23 Tariff 4";
            case 1150 :
                return "Secondary active energy A23 Tariff 5";
            case 1151 :
                return "Secondary active energy A23 Tariff 6";
            case 1152 :
                return "Secondary active energy A23 Tariff 7";
            case 1153 :
                return "Secondary active energy A23 Tariff 8";
            case 1154 :
                return "Secondary reactive energy R12 Tariff 1";
            case 1155 :
                return "Secondary reactive energy R12 Tariff 2";
            case 1156 :
                return "Secondary reactive energy R12 Tariff 3";
            case 1157 :
                return "Secondary reactive energy R12 Tariff 4";
            case 1158 :
                return "Secondary reactive energy R12 Tariff 5";
            case 1159 :
                return "Secondary reactive energy R12 Tariff 6";
            case 1160 :
                return "Secondary reactive energy R12 Tariff 7";
            case 1161 :
                return "Secondary reactive energy R12 Tariff 8";
            case 1162 :
                return "Secondary reactive energy R34 Tariff 1";
            case 1163 :
                return "Secondary reactive energy R34 Tariff 2";
            case 1164 :
                return "Secondary reactive energy R34 Tariff 3";
            case 1165 :
                return "Secondary reactive energy R34 Tariff 4";
            case 1166 :
                return "Secondary reactive energy R34 Tariff 5";
            case 1167 :
                return "Secondary reactive energy R34 Tariff 6";
            case 1168 :
                return "Secondary reactive energy R34 Tariff 7";
            case 1169 :
                return "Secondary reactive energy R34 Tariff 8";
            
            default:
                return "Unknown[" + rid + "]";
            }
        }
        
        public String getUnit(int unit){
            switch(unit){
            case 1:
                return "Wh";
            case 2:
                return "kWh";
            case 3:
                return "MWh";
            case 4:
                return "GWh";
            case 13:
                return "varh";
            case 14:
                return "kvarh";
            case 15:
                return "Mvarh";
            case 16:
                return "Gvarh";
            case 17:
                return "VAh";
            case 18:
                return "kVAh";
            case 19:
                return "MVAh";
            case 20:
                return "GVAh";
            case 21:
                return "W";
            case 22:
                return "kW";
            case 23:
                return "MW";
            case 24:
                return "GW";
            case 25:
                return "var";
            case 26:
                return "kvar";
            case 27:
                return "Mvar";
            case 28:
                return "Gvar";
            case 29:
                return "VA";
            case 30:
                return "kVA";
            case 31:
                return "MVA";
            case 32:
                return "GVA";
            case 33:
                return "V";
            case 34:
                return "A";
            case 35:
                return "kV";
            case 36:
                return "kA";
            case 37:
                return "c";
            case 38:
                return "K";
            case 39:
                return "l";
            case 40:
                return "m3";
            case 46:
                return "h";
            case 47:
                return "clock";
            case 48:
                return "date1";
            case 49:
                return "date2";
            case 50:
                return "date3";
            case 51:
                return "number";
            case 53:
                return "RTC";
            case 54:
                return "ASCII coded data";
            case 55:
                return "m3 x 10";
            case 56:
                return "ton x 10";
            case 57:
                return "Gj x 10";
            case 59:
                return "bit";
            case 60:
                return "s";
            case 61:
                return "ms";
            case 62:
                return "days";
            case 63:
                return "RTC-Q";
            case 64:
                return "Datetime";
            case 67:
                return "Hz";
            case 68:
                return "Degree";
            case 72:
                return "KamDateTime";
            default:
                return "Unknown";
            }
            
        }
        public byte getCid()
        {
            return cid;
        }

        public void setCid(byte cid)
        {
            this.cid = cid;
        }

        public String getCommand()
        {
            return command;
        }

        public void setCommand(String command)
        {
            this.command = command;
        }

        public String getDescr()
        {
            return descr;
        }

        public void setDescr(String descr)
        {
            this.descr = descr;
        }

        public int getArgsSize()
        {
            return argsSize;
        }

        public void setArgsSize(int argsSize)
        {
            this.argsSize = argsSize;
        }

        public String[][] getArgs()
        {
            return args;
        }

        public void setArgs(String[][] args)
        {
            this.args = args;
        }
    }
    
    public static double makeSiEx(byte bSiEx) {
        int signInt = (bSiEx & 128)/128;
        int signExp = (bSiEx & 64)/64;
        int exp = ((bSiEx&32) + (bSiEx&16) + (bSiEx&8) + (bSiEx&4) + (bSiEx&2) + (bSiEx&1));
        double siEx = Math.pow(-1, signInt)*Math.pow(10, Math.pow(-1, signExp)*exp);//-1^SI*-1^SE*exponent
        return siEx;
    }
    
    public static byte[] makeKmpCmd(byte[] cid, byte[] req) throws IOException {
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            out.write(0x3F);
            out.write(cid);
            out.write(req);
            byte[] crc = CRCUtil.calculate_Xmodem_Ccitt(out.toByteArray());
            out.write(crc);
            byte[] stuff_data = stuff(out.toByteArray());
            
            out = new ByteArrayOutputStream();
            out.write(new byte[]{(byte)0x80});
            out.write(stuff_data);
            out.write(new byte[]{(byte)0x0D});
            
            return out.toByteArray();
        }
        finally {
            if (out != null) out.close();
        }
    }
}
