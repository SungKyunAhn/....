package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.ObisStatus;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;


public class ObisRemove extends AbstractCommand{
    public int obisIndex;
    private ObisStatus obisStatus;
    
    public ObisRemove() {
        super(new byte[] {(byte)0x40, (byte)0x03});
    }
    
	public int getObisIndex() {
		return obisIndex;
	}

	public void setObisIndex(int obisIndex) {
		this.obisIndex = obisIndex;
	}

	public ObisStatus getObiusStatus() {
		return obisStatus;
	}

	public void setObiusStatus(ObisStatus obiusStatus) {
		this.obisStatus = obisStatus;
	}
	
	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, 0, b, 0, b.length);
        for (ObisStatus s : ObisStatus.values()) {
            if (s.getCode() == b[0]) {
            	obisStatus = s;
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
        datas[0].setValue((new byte[]{DataUtil.getByteToInt((int)info.get("obisIndex"))}));
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
	
	@Override
	public String toString() {
	    return "[ObisRemove]"+
	    	   "[status:"+obisStatus.name()+"]";
	}

	@Override
	public void decode(byte[] p1, CommandType p2) throws Exception{}

	@Override
	public Command get() throws Exception{return null;}

	@Override
	public Command get(HashMap p) throws Exception{return null;}

	@Override
	public Command set() throws Exception{return null;}

	@Override
	public Command trap() throws Exception{return null;}
}
