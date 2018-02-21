package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.NiProtocolEventCode.NIPEventCode;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class ModemEventLog extends AbstractCommand{
    public enum CommStatus {
        Normal ((byte)0x00),
        NoResponse ((byte)0x01),
        ProtocolError ((byte)0x02);
        
        private byte code;
        
        CommStatus(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
 
    public ModemEventLog() {
        super(new byte[] {(byte)0x10, (byte)0x06});
    }
    
    public int logCount;
    public int getLogCount() {
        return logCount;
    }
    public void setLogCount(int logCount) {
        this.logCount = logCount;
    }
    
    // INSERT START SP-681
    public int logOffset;
    public int getLogOffset() {
        return logOffset;
    }
    public void setLogOffset(int logOffset) {
        this.logOffset = logOffset;
    }
    // INSERT END SP-681
    
    public LogData[] logDatas;
    
    public LogData[] getLogs() {
        return logDatas;
    }
    public void setLogs(LogData[] logDatas) {
        this.logDatas = logDatas;
    }

    @Override
    public Command get(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        
        // UPDATE START SP-681
//        Object obj = info.get("count");
//        if (obj instanceof Integer){
//        	datas[0].setValue(DataUtil.get2ByteToInt((int)obj));
//        }else if (obj instanceof String){
//        	datas[0].setValue(DataUtil.get2ByteToInt(Integer.parseInt((String)obj)));
//        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Object obj = info.get("count");
        if (obj instanceof Integer){
        	out.write(DataUtil.get2ByteToInt((int)obj));
        }else if (obj instanceof String){
        	out.write(DataUtil.get2ByteToInt(Integer.parseInt((String)obj)));
        }
        
        obj = info.get("offset");
        int offset = 0;
        if (obj instanceof Integer){
        	offset = (int)obj;
        }else if (obj instanceof String){
        	offset = Integer.parseInt((String)obj);
        }
        
        // if offset < 0 then old version 
        if (offset >= 0) {
        	log.debug("Offset option is FW Ver 1.2 option. offset = " + offset );
        	out.write(DataUtil.get2ByteToInt(offset));
        }
        
        datas[0].setValue(out.toByteArray());
//        System.out.println(new String(Hex.decode(out.toByteArray())));
        // UPDATE END SP-681
        
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
    
    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        logCount = DataUtil.getIntTo2Byte(b);
        logDatas = new LogData[logCount];
        for (int i = 0; i < logDatas.length; i++) {
            logDatas[i] = new LogData();
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            logDatas[i].setIdx(DataUtil.getIntTo2Byte(b));
            
            b = new byte[7];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            logDatas[i].setTime(String.format("%02d/%02d/%4d %02d:%02d:%02d", 
                    DataUtil.getIntToByte(b[2]),
                    DataUtil.getIntToByte(b[3]),
                    DataUtil.getIntTo2Byte(new byte[]{b[0], b[1]}),
                    DataUtil.getIntToByte(b[4]),
                    DataUtil.getIntToByte(b[5]),
                    DataUtil.getIntToByte(b[6])));
            
            b = new byte[2];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            logDatas[i].setCode(getHexDump(b));
     
            b = new byte[4];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            logDatas[i].setValue(getHexDump(b));
        }
    }
    
    @Override
    public String toString() {
        StringBuffer rtn= new StringBuffer();
        NIPEventCode code;
        String eventName;
        String descr;
        for(int i=0; i< logDatas.length ;i++){
        	code = NiProtocolEventCode.NIPEventCode.getNipEventCode(((LogData)logDatas[i]).getCode(),((LogData)logDatas[i]).getValue());
        	if(code != null){
	        	eventName = code.getEventName();
	        	descr = code.getDescription();
        	}else{
        		eventName = " - ";
        		descr = " - ";
        	}
        		
        	rtn.append("\n[NO: "+i+", ");
            rtn.append("IDX: "+((LogData)logDatas[i]).getIdx()+", ");
            rtn.append("TIME: "+((LogData)logDatas[i]).getTime()+", ");
            rtn.append("EVENT: "+eventName+"("+((LogData)logDatas[i]).getCode()+"), ");
            rtn.append("VALUE: "+descr+"("+((LogData)logDatas[i]).getValue()+")]");
        }
        return "[ModemLog]"+
        "[Count:"+logCount+"]"+
        rtn.toString();	   
    }
    
    public class LogData {
        private int idx;
        private String time;
        private String code;
        private String value;
        public int getIdx() {
            return idx;
        }
        public void setIdx(int idx) {
            this.idx = idx;
        }
        public String getTime() {
            return time;
        }
        public void setTime(String time) {
            this.time = time;
        }
        public String getCode() {
            return code;
        }
        public void setCode(String code) {
            this.code = code;
        }
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }
 
    @Override
    public Command get() throws Exception{return null;}
    @Override
    public Command set() throws Exception{return null;}
    @Override
    public Command set(HashMap p) throws Exception{return null;}
    @Override
    public Command trap() throws Exception{return null;}
    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
    
    public String getHexDump(byte[] b){
        
        StringBuffer buf = new StringBuffer();
        
        try{
            for(int i = 0; i < b.length; i++) {
                int val = b[i];
                val = val & 0xff;
                buf.append(frontAppendNStr('0',Integer.toHexString(val),2));
                if(((i+1)%24) == 0) buf.append('\n');
            }
        }catch(Exception e){
            
        }
        return buf.toString();
    }
    
    public String frontAppendNStr(char append, String str, int length)
    {
        StringBuffer b = new StringBuffer("");

        try {
            if(str.length() < length)
            {
               for(int i = 0; i < length-str.length() ; i++)
                   b.append(append);
               b.append(str);
            }
            else
            {
                b.append(str);
            }
        } catch(Exception e) {
            
        }
        return b.toString();
    }

}
