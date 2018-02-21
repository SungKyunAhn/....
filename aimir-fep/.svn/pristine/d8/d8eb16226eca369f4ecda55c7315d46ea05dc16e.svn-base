package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class NbPlcModulatioin extends AbstractCommand{
    
    private Modulation modulation;
    private Status status;
    
    public NbPlcModulatioin() {
        super(new byte[] {(byte)0x20, (byte)0x07});
    }
 
    public enum Modulation {
        Robo((byte)0x00),
        Bpsk ((byte)0x01),
        Qpsk ((byte)0x02),
        Eightpsk ((byte)0x03),
        SuperRobo ((byte)0x05);
        
        private byte code;
        
        Modulation(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }
    }
    
    public Modulation getModulation() {
        return modulation;
    }
 
    @Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, 0, b, 0, b.length);
        for (Status s : Status.values()) {
            if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
                status = s;
                break;
            }
        }
    }
	@Override
	public Command set(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        //count
        datas[0].setValue((new byte[]{DataUtil.getByteToInt((int)info.get("code"))}));
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }

	@Override
	public String toString() {
	    return "[NbPlcModulatioin]"+
	    	   "[status:"+status.name()+"]";
	}

	@Override
	public Command get() throws Exception{return null;}
	@Override
	public Command get(HashMap p) throws Exception{return null;}
	@Override
	public Command set() throws Exception{return null;}
	@Override
	public Command trap() throws Exception{return null;}
    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
}
