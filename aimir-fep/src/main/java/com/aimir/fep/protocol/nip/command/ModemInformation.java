package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

public class ModemInformation extends AbstractCommand {
    public enum ModemType {
        Coordinator ((byte)0x00),
        Standard ((byte)0x01),
        Etype ((byte)0x02),
        Gtype ((byte)0x03),
        Water ((byte)0x04),
        Gas ((byte)0x05),
        IraqNBPlc ((byte)0x10),
        SProjRFCoordinator((byte)0x20),
        SProjRFRouter ((byte)0x21),
        SProjMBB ((byte)0x22),
        SProjEthernet ((byte)0x23),
        SProjDongle ((byte)0x24);
        
        private byte code;
        
        ModemType(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    public enum ModemStatus {
        Idle ((byte)0x00),
        MeterReading ((byte)0x01),
        FirmwareUpgrade ((byte)0x02);
        
        private byte code;
        
        ModemStatus(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    public enum ModemMode {
        Push((byte)0x00),
        Poll((byte)0x01);
        private byte code;
        ModemMode(byte code) {
            this.code = code;
        }
        public byte getCode() {
            return this.code;
        }
    }
    
    private String euiId;
    private ModemType modemType;
    private int resetTime;
    private String nodeKind;
    
    private String fwVersion;
    private int bulidNumber;
    private String hwVersion;
    
    private ModemStatus modemStatus;
    private ModemMode modemMode;
    
    public ModemInformation() {
        super(new byte[] {(byte)0x10, (byte)0x01});
    }
    
    public String getEuiId() {
        return euiId;
    }

    public ModemType getModemType() {
        return modemType;
    }

    public int getResetTime() {
        return resetTime;
    }

    public String getNodeKind() {
        return nodeKind;
    }

    public String getFwVersion() {
        return fwVersion;
    }
    
    public int getBulidNumber() {
        return bulidNumber;
    }

    public String getHwVersion() {
        return hwVersion;
    }
    
    public ModemStatus getModemStatus() {
        return modemStatus;
    }
    
    public ModemMode getModemMode() {
        return modemMode;
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
        
        byte[] b = new byte[8];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        euiId = Hex.decode(b);

        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        for (ModemType m : ModemType.values()) {
            if (m.getCode() == b[0]) {
                modemType = m;
                break;
            }
        }
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        resetTime = DataUtil.getIntToByte(b[0]);
        
        b = new byte[20];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        nodeKind = new String(b).trim();
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        fwVersion= new String(b).trim();
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        bulidNumber = DataUtil.getIntTo2Byte(b);
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        hwVersion= new String(b).trim();
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        for (ModemStatus m : ModemStatus.values()) {
            if (m.getCode() == b[0]) {
            	modemStatus = m;
                break;
            }
        }
        
        b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        pos += b.length;
        for (ModemMode m : ModemMode.values()) {
            if (m.getCode() == b[0]) {
            	modemMode = m;
                break;
            }
        }
    }

	@Override
	public String toString() {
	    return "[ModemInformation]"+
	    	   "[euiId:"+euiId+"]"+
	    	   "[modemType:"+modemType.name()+"]"+
	    	   "[resetTime:"+resetTime+"]"+
	    	   "[nodeKind:"+nodeKind+"]"+
	    	   "[fwVersion:"+fwVersion+"]"+
	    	   "[bulidNumber:"+bulidNumber+"]"+
	    	   "[hwVersion:"+hwVersion+"]"+
	    	   "[modemStatus:"+modemStatus.name()+"]"+
	    	   "[modemMode:"+modemMode.name()+"]";
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
