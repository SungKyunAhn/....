package com.aimir.fep.protocol.nip.command;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

import java.util.HashMap;


public class SnmpTrapOnOff extends AbstractCommand{
    
	String trapStatus;
	
	public String getStatus() {
		return trapStatus;
	}

	public void setStatus(String status) {
		this.trapStatus = status;
	}

	public SnmpTrapOnOff() {
        super(new byte[] {(byte)0x20, (byte)0x15});
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
        String inputStatus = info.get("trapStatus").toString();
        int intInput = Integer.parseInt(inputStatus);

        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);

        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        datas[0].setValue(new byte[]{DataUtil.getByteToInt(intInput)});
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
	
	@Override
	public String toString() {
        if(trapStatus.contains("1")){
            trapStatus = "1(Enable)";
        }else{
            trapStatus = "0(Disable)";
        }
	    return "[SnmpTrapOnOff]"+
	    	   "[trapStatus: "+trapStatus+"]";
	}
	
	@Override
	public void decode(byte[] p) throws Exception {
		// TODO Auto-generated method stub
		byte[] b = new byte[1];
	    System.arraycopy(p, 0, b, 0, b.length);
	    trapStatus = String.valueOf(DataUtil.getIntToByte(b[0]));
	}

	@Override
	public Command trap() throws Exception{return null;}

	@Override
	public Command get(HashMap p) throws Exception{return null;}

	@Override
	public Command set() throws Exception{return null;}
 
	@Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
    }
}
