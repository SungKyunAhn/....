package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class SingleActionSchedule extends AbstractCommand{
    public SingleActionSchedule() {
        super(new byte[] {(byte)0x30, (byte)0x03});
    }
    
    private int meterCount;
    private int[] meterPortAddress;
    private int day;
    private Status status;

    public int getMeterCount() {
		return meterCount;
	}

	public void setMeterCount(int meterCount) {
		this.meterCount = meterCount;
	}

	public int[] getMeterPortAddress() {
		return meterPortAddress;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public void setMeterPortAddress(int[] meterPortAddress) {
		this.meterPortAddress = meterPortAddress;
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
	public String toString() {
	    return "[SingleActionSchedule]"+
	    	   "[status:"+status.name()+"]";
	}

	@Override
	public Command set(HashMap info) throws Exception {
		int meterCount = (int)info.get("meterCount"); 
		int day = (int)info.get("day");
		int[] meterPortAddress = (int[])info.get("meterPortAddress");
		
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        //meterCount
        datas[0].setValue((new byte[]{DataUtil.getByteToInt(meterCount)}));
        //meterPortAddress
        byte[] cmdByte = new byte[meterCount];
        for(int i=0; i<cmdByte.length ; i++){
        	cmdByte[i] = DataUtil.getByteToInt(meterPortAddress[i]);
        }
        datas[0].setValue(cmdByte);
        datas[0].setValue((new byte[]{DataUtil.getByteToInt(day)}));
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
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
