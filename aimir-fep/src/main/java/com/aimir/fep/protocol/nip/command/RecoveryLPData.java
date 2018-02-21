package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class RecoveryLPData extends AbstractCommand{
    public RecoveryLPData() {
        super(new byte[] {(byte)0x00, (byte)0x04});
    }
    
    @Override
    public Command get(HashMap info) throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        datas[0].setValue(new byte[]{DataUtil.getByteToInt((int)info.get("offset")), DataUtil.getByteToInt((int)info.get("count"))});
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
    @Override
    public void decode(byte[] p1, CommandType p2) throws Exception{}
    @Override
    public void decode(byte[] p1) throws Exception{}
    @Override
    public Command get() throws Exception{return null;}
    @Override
    public Command set() throws Exception{return null;}
    @Override
    public Command set(HashMap p) throws Exception{return null;}
    @Override
    public Command trap() throws Exception{return null;}
}
