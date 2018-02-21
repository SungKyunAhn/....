package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;


public class WatchdogTest extends AbstractCommand{
    public WatchdogTest() {
        super(new byte[] {(byte)0x00, (byte)0x08});
    }

	@Override
	public Command set() throws Exception {
       Command command = new Command();
       Command.Attribute attr = command.newAttribute();
       Command.Attribute.Data[] datas = attr.newData(1);
       
       command.setCommandFlow(CommandFlow.Request);
       command.setCommandType(CommandType.Set);
       datas[0].setId(getAttributeID());
       
       attr.setData(datas);
       command.setAttribute(attr);
       return command;
    }
	
	@Override
    public String toString() {
        return "[WatchdogTest]";
    }

	@Override
	public void decode(byte[] p) throws Exception {
		// TODO Auto-generated method stub
	}
	@Override
	public Command get() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Command trap() throws Exception{return null;}

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
