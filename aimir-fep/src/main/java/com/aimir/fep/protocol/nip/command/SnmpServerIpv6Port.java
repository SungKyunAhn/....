package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class SnmpServerIpv6Port extends AbstractCommand{
    public SnmpServerIpv6Port() {
        super(new byte[] {(byte)0x20, (byte)0x10});
    }
    
    private int type;
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
	        ipAddress = DataUtil.decodeIpAddr(b);
	        pos += b.length;
		}else if(type == 1){//IPv6
			b = new byte[16];
	        System.arraycopy(bx, pos, b, 0, b.length);
	        ipAddress = DataUtil.decodeIPv6Addr(b);
	        pos += b.length;
		}
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        port = String.valueOf(DataUtil.getIntToByte(b[0]));
        
    }
	

	@Override
	public String toString() {
	    return "[SnmpServerIpv6Port]"+
	    	   "[type:"+type+"]"+
	    	   "[ipAddress:"+ipAddress+"]"+
	    	   "[port:"+port+"]";
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
		int type = (int)info.get("type"); 
		String IpAddress= (String)info.get("ipAddress");
		String port= (String)info.get("port");
		
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            
            out.write(new byte[]{DataUtil.getByteToInt(type)});
            if(type == 0){//IPv4
            	out.write(new byte[]{DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(0, 2), 16))
                		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(2, 4), 16))
                		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(4, 6), 16))
                		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(6, 8), 16))
            		});
            }else if(type == 1){//Ipv6
    	        out.write(new byte[]{DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(0, 2), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(2, 4), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(4, 6), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(6, 8), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(8, 10), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(10, 12), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(12, 14), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(14, 16), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(16, 18), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(18, 20), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(20, 22), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(22, 24), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(24, 26), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(26, 28), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(28, 30), 16))
    	        		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(30, 32), 16))
    	        		});
            }
            //port
            out.write(new byte[]{DataUtil.getByteToInt(Integer.parseInt(port.substring(0, 2), 16))
            		,DataUtil.getByteToInt(Integer.parseInt(port.substring(2, 4), 16))});

            datas[0].setValue(out.toByteArray());
            attr.setData(datas);
            command.setAttribute(attr);
            return command;
        }
        finally {
            if (out != null) out.close();
        }
    }

	@Override
	public void decode(byte[] p1, CommandType p2) throws Exception{}

	@Override
	public Command get(HashMap p) throws Exception{return null;}

	@Override
	public Command set() throws Exception{return null;}

	@Override
	public Command trap() throws Exception{return null;}
}
