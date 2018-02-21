package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;

public class CoordinatorBootup extends AbstractCommand{
    private Status status;
    private int jumpCode;

    public CoordinatorBootup() {
        super(new byte[] {(byte)0xA0, (byte)0x04});
    }
    
    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);

        for (Status s : Status.values()) {
            if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
                status = s;
                break;
            }
        }
    }

    @Override
    public Command trap() throws Exception {
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
	    return "[CoordinatorBootup]"+
	    	   "[status:"+status.name()+"]";
	}
	
	@Override
	public Command get() throws Exception{return null;}
	@Override
	public Command get(HashMap p) throws Exception{return null;}
	@Override
	public Command set() throws Exception{return null;}
	@Override
	public Command set(HashMap p) throws Exception{return null;}
    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
	
}
