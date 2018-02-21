package com.aimir.fep.protocol.nip.command;

import java.util.HashMap;

import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class TestConfiguration extends AbstractCommand{
    public TestConfiguration() {
        super(new byte[] {(byte)0x50, (byte)0x01});
    }
    
    private String eui64;
    private String data;
    private int dataInterval;
    private int dataCount;

	public String getEui64() {
		return eui64;
	}

	public void setEui64(String eui64) {
		this.eui64 = eui64;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getDataInterval() {
		return dataInterval;
	}

	public void setDataInterval(int dataInterval) {
		this.dataInterval = dataInterval;
	}

	public int getDataCount() {
		return dataCount;
	}

	public void setDataCount(int dataCount) {
		this.dataCount = dataCount;
	}

	@Override
	public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[8];
        System.arraycopy(bx, pos, b, 0, b.length);
        eui64 = String.valueOf(DataUtil.getIntTo8Byte(b));
        pos += b.length;
        
        b = new byte[4];
        System.arraycopy(bx, pos, b, 0, b.length);
        data = String.valueOf(DataUtil.getIntTo4Byte(b));
        
    }
	
	@Override
	public String toString() {
	    return "[TestConfiguration]"+
	    	   "[eui64:"+eui64+"]"+
	    	   "[data:"+data+"]";
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
		int dataInterval = (int)info.get("dataInterval");
		int dataCount = (int)info.get("dataCount");
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        
        ByteArrayOutputStream out = null;
        try {
            out = new ByteArrayOutputStream();
            out.write(DataUtil.get2ByteToInt(dataInterval));
            out.write(DataUtil.get2ByteToInt(dataCount));
         
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
