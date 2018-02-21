package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class AcceptLeave extends AbstractCommand{
    private String eui64;
    
    public AcceptLeave() {
        super(new byte[] {(byte)0xC0, (byte)0x02});
    }

    public String getEui64() {
		return eui64;
	}

	public void setEui64(String eui64) {
		this.eui64 = eui64;
	}

	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[8];
        System.arraycopy(bx, pos, b, 0, b.length);
        eui64 =  String.valueOf(DataUtil.getIntTo8Byte(b));
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
    public String toString() {
        return "[AcceptLeave]"+
        	   "[eui64:"+eui64+"]";
    }

	@Override
	public Command set() throws Exception{return null;}

	@Override
	public Command get(HashMap p) throws Exception{return null;}

	@Override
	public Command set(HashMap p) throws Exception{return null;}
 
	@Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
}
