package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class NetworkIpv6Prefix extends AbstractCommand{
    private String networkIpv6Prefix;
    
    public NetworkIpv6Prefix() {
        super(new byte[] {(byte)0xA0, (byte)0x05});
    }
    
    public String getNetworkIpv6Prefix() {
        return networkIpv6Prefix;
    }

    public void setNetworkIpv6Prefix(String networkIpv6Prefix) {
        this.networkIpv6Prefix = networkIpv6Prefix;
    }

    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[6];
        System.arraycopy(bx, pos, b, 0, b.length);
        networkIpv6Prefix =  String.valueOf(DataUtil.getIntToBytes(b));
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
        datas[0].setValue(DataUtil.get6ByteToInt((long)info.get("networkIpv6Prefix")));
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }

    @Override
	public String toString() {
	    return "[NetworkIpv6Prefix]"+
	    	   "[networkIpv6Prefix:"+networkIpv6Prefix+"]";
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
