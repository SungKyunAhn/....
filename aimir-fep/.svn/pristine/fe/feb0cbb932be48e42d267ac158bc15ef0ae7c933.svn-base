package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.ObisStatus;
import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class RawRomAccess extends AbstractCommand {   
    
	private byte[] dataAddress;
    private int dataLength;
    private String data;
    private byte[] packet;
    
    public RawRomAccess() {
    	super(new byte[] {(byte)0x20, (byte)0x16});
    }
    
	public byte[] getDataAddress() {
		return dataAddress;
	}

	public void setDataAddress(byte[] dataAddress) {
		this.dataAddress = dataAddress;
	}

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}	

	public byte[] getPacket() {
		return packet;
	}

	public void setPacket(byte[] packet) {
		this.packet = packet;
	}

	@Override
	public void decode(byte[] bx) {		
		packet = bx;
		
		int pos = 0;
        byte[] b = new byte[4];
        System.arraycopy(bx, pos, b, 0, b.length);
        dataAddress = b;
        pos += b.length;
        
        b = new byte[2];
        System.arraycopy(bx, pos, b, 0, b.length);
        dataLength = DataUtil.getIntTo2Byte(b);
        
        pos += b.length;
        
        b = new byte[bx.length - pos];
        System.arraycopy(bx, pos, b, 0, b.length);
        data = String.valueOf(b);   
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
        
    	byte[] data = (byte[])info.get("data");
    	    	
    	Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            out.write(data);
            datas[0].setValue(out.toByteArray());
            attr.setData(datas);
            command.setAttribute(attr);
            return command;
        } catch(Exception e) {
        	log.error(e);
        	throw e;
        }
        finally {
            if (out != null) out.close();
        }
    }
    
    @Override
	public Command get(HashMap info) throws Exception
	{
    	byte[] data = (byte[])info.get("data");
    	
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Get);
        datas[0].setId(getAttributeID());
        
        ByteArrayOutputStream out = null;
        
        try {
            out = new ByteArrayOutputStream();
            out.write(data);
            datas[0].setValue(out.toByteArray());
            attr.setData(datas);
            command.setAttribute(attr);
            return command;
        } catch(Exception e) {
        	log.error(e);
        	throw e;
        }
	}
    
	@Override
	public String toString() {
	    return "[RawRomAccess]"+	    	  
	    	   "[data:"+data+"]";
	}
	
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
