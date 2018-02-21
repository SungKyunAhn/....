package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class RecoveryMetering extends AbstractCommand{
    private int meterCount;
    private int[] meterPortAddress;
    private String lpStartTime;
    private String lpEndTime;
    
    public RecoveryMetering() {
        super(new byte[] {(byte)0x30, (byte)0x04});
    }
    
    private Status status;
    
	public int getMeterCount() {
		return meterCount;
	}

	public String getLpStartTime() {
		return lpStartTime;
	}

	public void setLpStartTime(String lpStartTime) {
		this.lpStartTime = lpStartTime;
	}

	public String getLpEndTime() {
		return lpEndTime;
	}

	public void setLpEndTime(String lpEndTime) {
		this.lpEndTime = lpEndTime;
	}

	public void setMeterCount(int meterCount) {
		this.meterCount = meterCount;
	}

	public int[] getMeterPortAddress() {
		return meterPortAddress;
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
	public Command set(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        
        int meterCount =(int)info.get("meterCount");
        int[] meterPortAddress=(int[])info.get("meterPortAddress");
        String lpStartTime=(String)info.get("lpStartTime");
        String lpEndTime=(String)info.get("lpEndTime");
        
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
        
        byte[] b = DataUtil.get2ByteToInt(Integer.parseInt(lpStartTime.substring(0, 4)));
        datas[0].setValue(new byte[]{b[1], b[0],
                DataUtil.getByteToInt(Integer.parseInt(lpStartTime.substring(4,6))),
                DataUtil.getByteToInt(Integer.parseInt(lpStartTime.substring(6,8))),
                DataUtil.getByteToInt(Integer.parseInt(lpStartTime.substring(8,10))),
                DataUtil.getByteToInt(Integer.parseInt(lpStartTime.substring(10,12))),
                DataUtil.getByteToInt(Integer.parseInt(lpStartTime.substring(12,14)))});
        
        byte[] b2 = DataUtil.get2ByteToInt(Integer.parseInt(lpEndTime.substring(0, 4)));
        datas[0].setValue(new byte[]{b2[1], b2[0],
                DataUtil.getByteToInt(Integer.parseInt(lpEndTime.substring(4,6))),
                DataUtil.getByteToInt(Integer.parseInt(lpEndTime.substring(6,8))),
                DataUtil.getByteToInt(Integer.parseInt(lpEndTime.substring(8,10))),
                DataUtil.getByteToInt(Integer.parseInt(lpEndTime.substring(10,12))),
                DataUtil.getByteToInt(Integer.parseInt(lpEndTime.substring(12,14)))});
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
	
	
	@Override
	public String toString() {
	    return "[RecoveryMetering]"+
	    	   "[status:"+status.name()+"]";
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
