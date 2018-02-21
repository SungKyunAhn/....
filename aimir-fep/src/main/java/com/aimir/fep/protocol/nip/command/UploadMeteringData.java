package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.command.ResponseResult.Status;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class UploadMeteringData extends AbstractCommand{
    private Status status;
    
    public UploadMeteringData() {
        super(new byte[] {(byte)0x00, (byte)0x02});
    }
    
    public Status getStatus() {
        return status;
    }
    
    @Override
    public Command set(HashMap info) throws Exception {
    	int uploadTime = (int)info.get("uploadTime");
    	int meterCount = (int)info.get("meterCount");
    	int[] port = (int[])info.get("port");
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(DataUtil.get2ByteToInt(uploadTime));
        out.write(DataUtil.getByteToInt(meterCount));
        for (int i = 0; i < port.length; i++) {
            out.write(DataUtil.get2ByteToInt(port[i]));
        }
        datas[0].setValue(out.toByteArray());
        
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }
  
    @Override
    public void decode(byte[] data) throws Exception {
    	   byte[] b = new byte[2];
           System.arraycopy(data, 0, b, 0, b.length);
           for (Status s : Status.values()) {
               if (s.getCode()[0] == b[0] && s.getCode()[1] == b[1]) {
            	   System.out.println("in");
                   status = s;
                   break;
               }
           }
    }
    
	@Override
	public String toString() {
	    return "[UploadMeteringData]"+
	    	   "[status:"+status.name()+"]";
	}

	@Override
	public void decode(byte[] p1, CommandType p2) throws Exception{}

	@Override
	public Command get() throws Exception{return null;}

	@Override
	public Command get(HashMap p) throws Exception{return null;}

	@Override
	public Command set() throws Exception{return null;}

	@Override
	public Command trap() throws Exception{return null;}
}
