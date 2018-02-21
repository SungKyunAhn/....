package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class HesIpv6Port extends AbstractCommand{
    private int type;
    
    public HesIpv6Port() {
        super(new byte[] {(byte)0x20, (byte)0x0F});
    }
    
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    private String ipAddress;
    private String port;
	
    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
	
    @Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        type = DataUtil.getIntToByte(b[0]);
        pos += b.length;
        
        if(type == 0){//IPv4
            b = new byte[4];
            System.arraycopy(bx, pos, b, 0, b.length);
            ipAddress = DataUtil.decodeIPv6Addr(b);
            pos += b.length;
        }
        else if(type == 1){//IPv6
            b = new byte[16];
            System.arraycopy(bx, pos, b, 0, b.length);
            ipAddress = DataUtil.decodeIpAddr(b);
            pos += b.length;
        }
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        port = String.valueOf(DataUtil.getIntToByte(b[0]));
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
        //int type ,String IpAddress, String port
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        
        int type = (int)info.get("type");
        String ipAddress =(String)info.get("ipAddress");
        String port = (String)info.get("port");
        ByteArrayOutputStream out = null;

        try {
            out = new ByteArrayOutputStream();
            out.write(DataUtil.getByteToInt(type));
            
            if(type == 0){//IPv4
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(0, 1))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(1, 2))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(2, 3))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(3, 4))));
            }
            else if(type == 1){//Ipv6
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(0, 1))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(1, 2))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(2, 3))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(3, 4))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(4, 5))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(5, 6))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(6, 7))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(7, 8))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(8, 9))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(9, 10))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(10, 11))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(11, 12))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(12, 13))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(13, 14))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(14, 15))));
                out.write(DataUtil.getByteToInt(Integer.parseInt(ipAddress.substring(15, 16))));
            }
            //port
            out.write(new byte[]{DataUtil.getByteToInt(Integer.parseInt(port.substring(0, 2)))
            		,DataUtil.getByteToInt(Integer.parseInt(port.substring(2, 4)))});
            
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
	public String toString() {
	    return "[HesIpv6Port]"+
	    	   "[type:"+type+"]"+
	    	   "[ipAddress:"+ipAddress+"]"+
	    	   "[port:"+port+"]";
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
