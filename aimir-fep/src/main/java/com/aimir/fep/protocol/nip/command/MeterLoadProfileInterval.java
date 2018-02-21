package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class MeterLoadProfileInterval extends AbstractCommand{
    private int meterCount;
    private int[] meterPortAddress;
    private int LpInterval;
    private Status status;
    
    public MeterLoadProfileInterval() {
        super(new byte[] {(byte)0x30, (byte)0x01});
    }
    
    public int getMeterCount() {
        return meterCount;
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

    public int getLpInterval() {
        return LpInterval;
    }

    public void setLpInterval(int lpInterval) {
        LpInterval = lpInterval;
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
        //int meterCount, int LpInterval ,int[] meterPortAddress
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        
        int meterCount = (int)info.get("meterCount"); 
        int LpInterval = (int)info.get("LpInterval");
        int[] meterPortAddress = (int[])info.get("meterPortAddress");
        
        ByteArrayOutputStream out = null;
        
        try {
            out = new ByteArrayOutputStream();
            
            //meterCount
            out.write((new byte[]{DataUtil.getByteToInt(meterCount)}));
            //meterPortAddress
            byte[] cmdByte = new byte[meterCount];
            for(int i=0; i<cmdByte.length ; i++){
            	cmdByte[i] = DataUtil.getByteToInt(meterPortAddress[i]);
            }
            out.write(cmdByte);
            out.write((new byte[]{DataUtil.getByteToInt(LpInterval)}));
            
            datas[0].setValue(out.toByteArray());
            attr.setData(datas);
            command.setAttribute(attr);
            return command;
        }
        finally {
            if (out != null) out.close();
        }
    }
	
    @Override
    public String toString() {
        return "[MeterLoadProfileInterval]"+
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
