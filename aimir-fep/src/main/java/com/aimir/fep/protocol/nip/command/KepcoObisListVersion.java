package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class KepcoObisListVersion extends AbstractCommand{
    private int obisListVersion;

    public KepcoObisListVersion() {
        super(new byte[] {(byte)0x40, (byte)0x06});
    }
    
    public int getObisListVersion() {
        return obisListVersion;
    }
    
    public void setObisListVersion(int obisListVersion) {
        this.obisListVersion = obisListVersion;
    }

    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        obisListVersion = DataUtil.getIntToByte(b[0]);
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
    public String toString() {
        return "[KepcoObisListVersion]"+
                "[obisListVersion:"+obisListVersion+"]";
    }
    
    @Override
    public Command get(HashMap p) throws Exception{return null;}
    @Override
    public Command set() throws Exception{return null;}
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
