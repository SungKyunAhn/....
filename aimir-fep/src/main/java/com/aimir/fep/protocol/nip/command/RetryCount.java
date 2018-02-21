package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class RetryCount extends AbstractCommand{
    public RetryCount() {
        super(new byte[] {(byte)0x20, (byte)0x14});
    }
    
    private int retryCount;
	public int getRetryCount() {
		return retryCount;
	}
	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, 0, b, 0, b.length);
        retryCount = DataUtil.getIntToByte(b[0]);
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
	public Command set(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        //count
        datas[0].setValue(new byte[]{DataUtil.getByteToInt((int)info.get("retryCount"))});
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
	

	@Override
	public String toString() {
	    return "[RetryCount]"+
	    	   "[retryCount:"+retryCount+"]";
	}

	@Override
	public void decode(byte[] p1, CommandType p2) throws Exception{}

	@Override
	public Command get(HashMap p) throws Exception{return null;}

	@Override
	public Command set() throws Exception{return null;}

	@Override
	public Command trap() throws Exception{return null;}

}
