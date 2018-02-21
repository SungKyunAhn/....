package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class JoinNetwork extends AbstractCommand{
    public JoinNetwork() {
        super(new byte[] {(byte)0x20, (byte)0x0C});
    }

    @Override
    public Command set(HashMap info) throws Exception {
        //int channel, int panId
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            out.write(DataUtil.getByteToInt((int)info.get("channel")));
            out.write(DataUtil.get2ByteToInt((int)info.get("panId")));
            datas[0].setValue(out.toByteArray());
        }
        finally {
            if (out != null) out.close();
        }
        
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
    public Command set() throws Exception{return null;}
    @Override
    public Command trap() throws Exception{return null;}
    @Override
    public void decode(byte[] p1, CommandType commandType)
                    throws Exception {
        // TODO Auto-generated method stub
        
    }
}
