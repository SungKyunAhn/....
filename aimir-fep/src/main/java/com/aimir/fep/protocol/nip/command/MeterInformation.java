package com.aimir.fep.protocol.nip.command;

import java.util.Arrays;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class MeterInformation extends AbstractCommand {
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
    
    public MeterInformation() {
        super(new byte[] {(byte)0x10, (byte)0x05});
    }
    
    private CommStatus commStatus;
    private int meterCount;
    private String[] meterSerial;
    
    public CommStatus getCommStatus() {
        return commStatus;
    }

    public int getMeterCount() {
        return meterCount;
    }

    public String[] getMeterSerial() {
        return meterSerial;
    }
 
    @Override
    public Command get() throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }

    @Override
    public Command trap() throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }

    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        for (CommStatus m : CommStatus.values()) {
            if (m.getCode() == b[0]) {
                commStatus = m;
                break;
            }
        }
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        meterCount = DataUtil.getIntToByte(b[0]);
        
        meterSerial = new String[meterCount];
        for (int i = 0; i < meterCount; i++) {
            b = new byte[20];
            System.arraycopy(bx, pos, b, 0, b.length);
            pos += b.length;
            meterSerial[i] = new String(b).trim();
        }
    }
    

    @Override
    public String toString() {
        return "[MeterInformation]"+
                "[commStatus:"+commStatus.name()+"]"+
                "[meterCount:"+meterCount+"]"+
                "[meterSerial:"+Arrays.toString(meterSerial)+"]";
    }
    
	@Override
	public Command get(HashMap p) throws Exception{return null;}
	@Override
	public Command set() throws Exception{return null;}
	@Override
	public Command set(HashMap p) throws Exception{return null;}

    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
	
}
