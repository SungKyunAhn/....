package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class ModemTxPower extends AbstractCommand{
    private int txPower;
    private Status status;
    
    public ModemTxPower() {
        super(new byte[] {(byte)0x20, (byte)0x08});
    }
    
    public int getTxPower() {
        return txPower;
    }

    public Status getStatus() {
        return status;
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
        datas[0].setValue(new byte[]{DataUtil.getByteToInt((int)info.get("txPower"))});
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
  
    @Override
    public void decode(byte[] data) {
        // status result
        if (data.length == 2) {
            byte[] b = new byte[2];
            System.arraycopy(data, 0, b, 0, b.length);

            for (Status s : Status.values()) {
                if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
                    status = s;
                    break;
                }
            }
        }
        else if (data.length == 1) {
            byte[] b = new byte[1];
            System.arraycopy(data, 0, b, 0, b.length);
            txPower = DataUtil.getByteToInt(b[0]);
        }
    }
 
    @Override
	public String toString() {
	    return "[ModemTxPower]"+
	    	   "[status:"+status.name()+"]"+
	    	   "[txPower:"+txPower+"]";
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
