package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class GatheringDataPoll extends AbstractCommand{
    public DLMSMeter dlmsMeter;
    
    public GatheringDataPoll() {
        super(new byte[] {(byte)0xC2, (byte)0x03});
    }

    @Override
    public void decode(byte[] bx) {
        int pos = 0;
       
        byte[] b = new byte[6];
        System.arraycopy(bx, pos, b, 0, b.length);
        dlmsMeter.obis = String.valueOf(DataUtil.getIntToBytes(b));
        pos += b.length;
	        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        dlmsMeter.dlmsClass=  String.valueOf(DataUtil.getIntTo2Byte(b));
        pos += b.length;
	        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        dlmsMeter.attr=  String.valueOf(DataUtil.getIntToByte(b[0]));
        pos += b.length;
	        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        dlmsMeter.tag=  DataUtil.getIntToByte(b[0]);
        pos += b.length;
    }
 
    @Override
    public Command get(HashMap info) throws Exception {
        //int tid
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
       
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        datas[0].setValue(DataUtil.get2ByteToInt((int)info.get("tid")));
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
	
    @Override
    public String toString() {
        return "[GatheringDataPoll]"+
                "[obis:"+dlmsMeter.obis+"]"+
                "[class:"+dlmsMeter.dlmsClass+"]"+
                "[attr:"+dlmsMeter.attr+"]"+
                "[tag:"+dlmsMeter.tag+"]";
    }
	
    public class DLMSMeter{
		
        public String obis;
        public String dlmsClass;
        public String attr;
		public int tag;
		public String data;
		public int length;
		
		public String getObis() {
			return obis;
		}
		public void setObis(String obis) {
			this.obis = obis;
		}
		public String getDlmsClass() {
			return dlmsClass;
		}
		public void setDlmsClass(String dlmsClass) {
			this.dlmsClass = dlmsClass;
		}
		public String getAttr() {
			return attr;
		}
		public void setAttr(String attr) {
			this.attr = attr;
		}
		public int getTag() {
			return tag;
		}
		public void setTag(int tag) {
			this.tag = tag;
		}
		public String getData() {
			return data;
		}
		public void setData(String data) {
			this.data = data;
		}
		public int getLength() {
			return length;
		}
		public void setLength(int length) {
			this.length = length;
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
	
}
