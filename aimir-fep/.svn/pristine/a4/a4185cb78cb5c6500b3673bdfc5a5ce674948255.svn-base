
package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class ModemIpInformation extends AbstractCommand{
    public ModemIpInformation() {
        super(new byte[] {(byte)0x20, (byte)0x0F});
    }
    
	public enum TargetType {
		DCU((byte)0x00),
        HES((byte)0x01),
        SNMP((byte)0x02),
        NTP((byte)0x03),
        Modem((byte)0x04);
        
        private byte code;
        
        private TargetType(byte code) {
            this.code = code;
        }
        
        public byte getCode() {
            return this.code;
        }

        public static TargetType valueOf(byte code) {
            for (TargetType type : values()) {
                if (type.getCode() == code) {
                	return type;
                }
            }
            throw new IllegalArgumentException("no such enum object for the code: " + code);
        }

    }
	private TargetType targetType;
	public TargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}
	private enum IpType{
		IPv4((byte) 0x00),
		IPv6((byte) 0x01);
		private byte code;

		private IpType(byte code) {
			this.code = code;
		}

		public byte getCode() {
			return this.code;
		}

		public static IpType valueOf(byte code) {
			for (IpType type : values()) {
				if (type.getCode() == code) {
					return type;
				}
			}
			throw new IllegalArgumentException("no such enum object for the code: " + code);
		}
	}
    private IpType ipType;
    public IpType getIpType() {
		return ipType;
	}

    public void setIpType(IpType ipType) {
		this.ipType = ipType;
	}

	private String ipAddress;
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	
	private Status status;
	
	/**
	 * @return the status
	 */
	public Status getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        targetType = TargetType.valueOf(b[0]);
        pos += b.length;
        
        System.arraycopy(bx, pos, b, 0, b.length);
        ipType = IpType.valueOf(b[0]);
        pos += b.length;
        
        if(ipType == IpType.IPv4 ){
	        b = new byte[4];
	        System.arraycopy(bx, pos, b, 0, b.length);
	        ipAddress = DataUtil.decodeIpAddr(b);
	        pos += b.length;
		}else if(ipType == IpType.IPv6){
			b = new byte[16];
	        System.arraycopy(bx, pos, b, 0, b.length);
	        ipAddress = DataUtil.decodeIPv6Addr(b);
	        pos += b.length;
		}
        status = Status.Success;
    }
	

	@Override
	public String toString() {
		String targetTypeStr = "";
		if ( targetType != null ){
			targetTypeStr = targetType.name()+"(" + targetType.getCode() +")";
		}
		String ipTypeStr = "";
		if ( ipType != null ){
			ipTypeStr = ipType.name();
		}
		
		return "[ModemIpInformation]"+
	    	   "[targetType:"+ targetTypeStr + "]" +
	    	   "[ipType:" + ipTypeStr + "]" +
	    	   "[ipAddress:"+ipAddress+"]";
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
		int targetType;
		int ipType;
	
		if ( info.get("targetType") instanceof String ){
			targetType  = Integer.parseInt((String)info.get("targetType"));
		}
		else {
			targetType = (int)info.get("targetType");
		}
		if ( info.get("ipType") instanceof String ){
			ipType = Integer.parseInt((String)info.get("ipType"));
		}
		else{
			ipType = (int)info.get("ipType");
		}
		
		String IpAddress= (String)info.get("ipAddress");
		
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            out.write(new byte[]{DataUtil.getByteToInt(targetType)});      
            out.write(new byte[]{DataUtil.getByteToInt(ipType)});
            if(ipType == 0){//IPv4
            	out.write(new byte[]{DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(0, 2), 16))
                		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(2, 4), 16))
                		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(4, 6), 16))
                		,DataUtil.getByteToInt(Integer.parseInt(IpAddress.substring(6, 8), 16))
            		});
            }else if(ipType == 1){//Ipv6
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
	public Command get(HashMap info) throws Exception
	{
		// UPDATE START SP-701
		int targetType;
		int ipType;

		if ( info.get("targetType") instanceof String ){
			targetType  = Integer.parseInt((String)info.get("targetType"));
		}
		else {
			targetType = (int)info.get("targetType");
		}
		if ( info.get("ipType") instanceof String ){
			ipType = Integer.parseInt((String)info.get("ipType"));
		}
		else{
			ipType = (int)info.get("ipType");
		}
		// UPDATE END SP-701

		byte[]  paramdata = new byte[2];
        
        paramdata[0] = DataUtil.getByteToInt(targetType);
        paramdata[1] = DataUtil.getByteToInt(ipType);
        
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());   

        datas[0].setValue(paramdata);
        attr.setData(datas);
        command.setAttribute(attr);

        return command;
	}

	@Override
	public Command set() throws Exception{return null;}

	@Override
	public Command trap() throws Exception{return null;}
}
