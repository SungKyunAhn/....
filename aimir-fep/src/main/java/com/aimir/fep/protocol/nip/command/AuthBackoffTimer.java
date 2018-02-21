package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class AuthBackoffTimer extends AbstractCommand{
    private String authTimer;
    
    public AuthBackoffTimer() {
        super(new byte[] {(byte)0xC0, (byte)0x04});
    }
    
	public String getAuthTimer() {
		return authTimer;
	}

	public void setAuthTimer(String authTimer) {
		this.authTimer = authTimer;
	}

	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[4];
        System.arraycopy(bx, pos, b, 0, b.length);
        authTimer =  String.valueOf(DataUtil.getIntTo4Byte(b));
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
		//String authTimer
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        datas[0].setValue(DataUtil.get4ByteToInt(Integer.parseInt((String)info.get("authTimer"))));
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
	
	@Override
    public String toString() {
        return "[AuthBackoffTimer]"+
        	   "[authTimer:"+authTimer+"]";
    }

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
