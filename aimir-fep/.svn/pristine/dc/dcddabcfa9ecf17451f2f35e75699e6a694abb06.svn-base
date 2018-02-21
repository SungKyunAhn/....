package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;

public class FactorySetting extends AbstractCommand{
    
    public FactorySetting() {
        super(new byte[] {(byte)0x00, (byte)0x03});
    }
    
    @Override
    public Command set() throws Exception {
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        datas[0].setValue(new byte[]{(byte)0x03, (byte)0x14});//공장 초기화
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }

    @Override
    public void decode(byte[] p1) throws Exception{}
    @Override
    public Command get() throws Exception{return null;}
    @Override
    public Command get(HashMap p) throws Exception{return null;}
    @Override
    public Command set(HashMap p) throws Exception{return null;}
    @Override
    public Command trap() throws Exception{return null;}
    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
}