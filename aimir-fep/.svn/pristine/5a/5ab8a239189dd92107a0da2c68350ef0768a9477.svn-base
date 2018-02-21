package com.aimir.fep.protocol.nip.command;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandFlow;
import com.aimir.fep.protocol.nip.frame.GeneralFrame.CommandType;
import com.aimir.fep.protocol.nip.frame.payload.AbstractCommand;
import com.aimir.fep.protocol.nip.frame.payload.Command;
import com.aimir.fep.util.DataUtil;

public class RealTimeMetering extends AbstractCommand {
	private String status;
	    
	public RealTimeMetering() {
	      super(new byte[] {(byte)0x00, (byte)0x0A});
	}
	
	public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
	@Override
	public Command get() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Command get(HashMap p) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Command set() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public Command set(HashMap info) throws Exception {
	    Object obj = info.get("interval");
	    int interval = 0;
	    int duration = 0;
	    if (obj instanceof Integer) {
	        interval = (int)obj;
	    }
	    else if (obj instanceof String) {
	        interval = Integer.parseInt((String)obj);
	    }
	    obj = info.get("duration");
	    if (obj instanceof Integer) {
	        duration = (int)obj;
        }
        else if (obj instanceof String) {
            duration = Integer.parseInt((String)obj);
        }
    	log.debug("[interval: " + interval + "] [duration: " + duration + "]" );
        Command command = new Command();
        Command.Attribute attr = command.newAttribute();
        Command.Attribute.Data[] datas = attr.newData(1);
        
        command.setCommandFlow(CommandFlow.Request);
        command.setCommandType(CommandType.Set);
        datas[0].setId(getAttributeID());
      
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        out.write(DataUtil.get2ByteToInt(interval));
        out.write(DataUtil.getByteToInt(duration));
        log.debug(out.toByteArray().length);
        datas[0].setValue(out.toByteArray());
        attr.setData(datas);
        command.setAttribute(attr);
        return command;
    }

	@Override
	public Command trap() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
    public void decode(byte[] bx) {
        int pos = 0;
        byte[] b = new byte[1];
        System.arraycopy(bx, pos, b, 0, b.length);
        status =String.valueOf(DataUtil.getIntToByte(b[0]));
    }

	@Override
	public void decode(byte[] p1, CommandType commandType) throws Exception {
		// TODO Auto-generated method stub

	}

}
